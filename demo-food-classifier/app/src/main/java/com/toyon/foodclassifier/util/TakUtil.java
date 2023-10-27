package com.toyon.foodclassifier.util;

import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.atakmap.coremap.cot.event.CotDetail;
import com.atakmap.coremap.cot.event.CotEvent;
import com.atakmap.coremap.maps.time.CoordinatedTime;

/** Inter-process Communication Constants and Static Functions */
public class TakUtil {
    private static final String TAG = TakUtil.class.getSimpleName();

    /** Construct a CoT Event Object to broadcast to ATAK devices to delete a map item */
    public static CotEvent deleteCotEvent(String itemUid) {
        // start with the most nested element: <link uid='' type='none' relation='none'>
        CotDetail linkDetail = new CotDetail("link");
        linkDetail.setAttribute("uid", itemUid);
        linkDetail.setAttribute("relation", "none");
        linkDetail.setAttribute("type", "none");

        // Can use the default <point/> so we don't set anything
        // set CoT Event detail children with delete info <detail><link/><__forcedelete></detail>
        CotDetail deleteDetails = new CotDetail();
        deleteDetails.addChild(linkDetail);
        deleteDetails.addChild(new CotDetail("__forcedelete"));

        // set base CoT Event attributes and details child <event><detail>...</detail></event>
        CotEvent deleteEvent = new CotEvent();
        deleteEvent.setUID("any_uid_is_good");
        deleteEvent.setHow("m-g");
        deleteEvent.setType("t-x-d-d");
        CoordinatedTime currentTime = new CoordinatedTime(CoordinatedTime.currentTimeMillis());
        deleteEvent.setStale(currentTime);
        deleteEvent.setStart(currentTime);
        deleteEvent.setTime(currentTime);
        deleteEvent.setDetail(deleteDetails);
        Log.d(TAG, "Delete CoT Event\n" + deleteEvent);
        return deleteEvent;
    }


    /**
     * Add a long click listener to the provided button which displays a Toast message with the
     * specified text when the button is long pressed.
     *
     * @param atakContext ATAK map context required for Toast notifications
     * @param btn Button to add the long click listener
     * @param str The string to display for the toast
     */
    public static void setButtonToast(Context atakContext, @NonNull Button btn, String str) {
        btn.setOnLongClickListener(view -> {
            Toast.makeText(atakContext, str, Toast.LENGTH_LONG).show();
            return true;
        });
    }

}
