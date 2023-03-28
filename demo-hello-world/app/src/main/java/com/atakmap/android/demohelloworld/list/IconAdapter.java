/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.atakmap.android.demohelloworld.list;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atakmap.android.icons.UserIcon;
import com.atakmap.android.icons.UserIconDatabase;
import com.atakmap.android.demohelloworld.plugin.R;

import java.util.List;

public class IconAdapter extends RecyclerView.Adapter<IconAdapter.ViewHolder> {

    private static final String TAG = IconAdapter.class.getSimpleName();
    private final List<UserIcon> userIcons;
    private final UserIconDatabase userIconDatabase;
    private final Context atakContext;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView filename;
        private final TextView iconId;
        private final ImageView renderIcon;

        public ViewHolder(View view) {
            super(view);
            filename = view.findViewById(R.id.icon_filename);
            iconId = view.findViewById(R.id.icon_id);
            renderIcon = view.findViewById(R.id.render_icon);
        }

        public TextView getFilename() {
            return filename;
        }

        public TextView getIconId() {
            return iconId;
        }

        public ImageView getRenderIcon() { return renderIcon; }

        public void bind(UserIcon icon, Context atakContext) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(atakContext, icon.getIconsetPath(), Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Icon Path: " + icon.getIconsetPath());
                }
            });
        }
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet UserIcon[] containing the data to populate views to be used
     * by RecyclerView
     */
    public IconAdapter(List<UserIcon> dataSet, Context atakContext) {
        this.atakContext = atakContext;
        this.userIconDatabase = UserIconDatabase.instance(atakContext);
        this.userIcons = dataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.item_icon, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        try {
            viewHolder.getFilename().setText(userIcons.get(position).getFileName());
            viewHolder.getIconId().setText(String.valueOf(userIcons.get(position).getId()));
            viewHolder.getRenderIcon().setImageBitmap(
                    this.userIconDatabase.getIcon(
                            userIcons.get(position).getId(), true).getBitMap());
            viewHolder.bind(userIcons.get(position), this.atakContext);
        } catch (Exception e) {
            Log.e(TAG, "Error populating icon adapter view - " + e);
        }
    }

    @Override
    public int getItemCount() {
        return userIcons.size();
    }
}
