/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.atakmap.android.demohelloworld;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;

import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.dropdown.DropDown;
import com.atakmap.android.dropdown.DropDownReceiver;
import com.atakmap.android.gui.PluginSpinner;
import com.atakmap.android.icons.UserIcon;
import com.atakmap.android.demohelloworld.plugin.R;
import com.atakmap.android.demohelloworld.list.DynamicRecyclerView;
import com.atakmap.android.demohelloworld.list.IconAdapter;
import com.atakmap.android.icons.UserIconDatabase;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.util.SimpleItemSelectedListener;
import com.atakmap.coremap.filesystem.FileSystemUtils;
import com.atakmap.database.CursorIface;
import com.atakmap.database.android.AndroidDatabaseAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Broadcast receiver and accompanying logic for UI functionality of 2D map icon viewer.
 * Click an icon to show a toast and log message of the path required for creating the marker icon.
 */
public class Icon2dDropDown extends DropDownReceiver implements DropDown.OnStateListener {

    public static final String SHOW_ICON_PANE = "demohelloworld.dropdown.mapiconview";
    public static final String TAG = Icon2dDropDown.class.getSimpleName();

    private final View paneView;
    private final Context pluginCtx;
    private final DynamicRecyclerView iconRecyclerView;

    protected Icon2dDropDown(MapView mapView, final Context pluginContext) {
        super(mapView);
        pluginCtx = pluginContext;
        paneView = PluginLayoutInflater.inflate(pluginContext, R.layout.pane_map_icon_2d, null);
        iconRecyclerView = paneView.findViewById(R.id.icon_recycler);
        PluginSpinner groupNameSpinner = new PluginSpinner(pluginCtx);
        groupNameSpinner.setBackgroundColor(ContextCompat.getColor(pluginCtx, R.color.darker_gray));
        groupNameSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (view instanceof TextView) {
                    ((TextView) view).setTextColor(Color.CYAN);
                    List<UserIcon> groupIcons = getGroupIcons(((TextView) view).getText().toString(), paneView.getContext());
                    IconAdapter iconAdapter = new IconAdapter(groupIcons, paneView.getContext());
                    iconRecyclerView.setAdapter(iconAdapter);
                }
            }
        });
        groupNameSpinner.setAdapter(getIconGroupNames());
        LinearLayout spinnerContainer = paneView.findViewById(R.id.spinner_holder);
        spinnerContainer.addView(groupNameSpinner);
        if (!groupNameSpinner.getAdapter().isEmpty())
            groupNameSpinner.setSelection(0);
    }

    private ArrayAdapter<String> getIconGroupNames() {
        List<Pair<String, String>> groups = getIconGroupIdentifiers();
        String[] groupNames = new String[groups.size()];
        for (int i = 0; i < groups.size(); i++) {
            groupNames[i] = groups.get(i).first;
        }
        return new ArrayAdapter<String>(pluginCtx, R.layout.item_spinner_text, groupNames);
    }

    @Override
    protected void disposeImpl() { }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action == null)
            return;
        if (action.equals(SHOW_ICON_PANE))
            showDropDown(paneView,
                    FULL_WIDTH, FULL_HEIGHT, // Landscape Dimensions
                    FULL_WIDTH, FULL_HEIGHT, // Portrait Dimensions
                    false, this);
    }

    @Override
    public void onDropDownSelectionRemoved() { }

    @Override
    public void onDropDownClose() { }

    @Override
    public void onDropDownSizeChanged(double v, double v1) { }

    @Override
    public void onDropDownVisible(boolean b) { }

    /**
     * Get a List of all unique Specific Icon Group Names recorded for more refined search of icons
     * by categories. UserIconDatabase.getIconSets uses the "name" of an icon set to identify the
     * author of the icons ("Default" is ATAK). These group names are actually required when
     * creating the path to a specific icon to be used by a marker.
     *
     * @return List of pairs first = groupName, second = iconset_uid
     */
    private List<Pair<String, String>> getIconGroupIdentifiers() {
        String iconSqliteLocation = FileSystemUtils.getItem("Databases" + File.separatorChar + "iconsets.sqlite").getAbsolutePath();
        AndroidDatabaseAdapter adb = AndroidDatabaseAdapter.openDatabase(iconSqliteLocation, SQLiteDatabase.OPEN_READONLY);
        List<Pair<String, String>> groups = new ArrayList<>();
        CursorIface result = null;
        try {
            result = adb.compileQuery("SELECT DISTINCT groupName, iconset_uid FROM icons;");

            while (result.moveToNext()) {
                groups.add(new Pair<>(result.getString(0), result.getString(1)));
            }
            Collections.sort(groups, new Comparator<Pair<String, String>>() {
                @Override
                public int compare(final Pair<String, String> o1, Pair<String, String> o2) {
                    assert o1.first != null;
                    assert o2.first != null;
                    return o1.first.compareTo(o2.first);
                }
            });
        } catch (NullPointerException nullPointerException) {
            Log.e(TAG, "~ Failed to sort null pointer exception");
            nullPointerException.printStackTrace();
        }
        finally {
            if (result != null)
                result.close();
            else
                Log.d(TAG, "~ Query failed to find icon group names");
        }
        return groups;
    }

    /**
     * Get a list of UserIcons that are categorized under the specified group name
     * @param groupName Group name search for related icons
     * @param atakContext ATAK context to use for searching ATAK icon assets
     * @return List of UserIcons for the group
     */
    private static List<UserIcon> getGroupIcons(String groupName, Context atakContext) {
        UserIconDatabase userIconDatabase = UserIconDatabase.instance(atakContext);
        List<UserIcon> icons = new ArrayList<>();
        CursorIface cursor = null;
        try {
            cursor = AndroidDatabaseAdapter.query(
                    userIconDatabase.getReadableDatabase(), UserIconDatabase.TABLE_ICONS,
                    new String[]{ "id" }, "groupName=?", new String[]{groupName},
                    null, null, null);
            while (cursor.moveToNext()) {
                int iconId = cursor.getInt(cursor.getColumnIndex("id"));
                // we don't load all bitmaps into memory, do this in your recycler view
                icons.add(userIconDatabase.getIcon(iconId, false));
            }
            Collections.sort(icons, new Comparator<UserIcon>() {
                @Override
                public int compare(final UserIcon o1, UserIcon o2) {
                    return o1.getFileName().compareTo(o2.getFileName());
                }
            });
        } catch (IndexOutOfBoundsException indexExcept) {
            Log.e(TAG, "Issue finding column position");
            indexExcept.printStackTrace();
        }
        catch (NullPointerException nullException) {
            Log.e(TAG, "Null Exception UserIcon Filename sort is likely null");
            nullException.printStackTrace();
        }
        finally {
            if (cursor != null)
                cursor.close();
            else
                Log.d(TAG, "~ Query failed to find icons with group name " + groupName);
        }
        return icons;
    }

}
