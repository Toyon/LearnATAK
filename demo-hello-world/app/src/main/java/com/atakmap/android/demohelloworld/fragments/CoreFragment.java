/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.atakmap.android.demohelloworld.fragments;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.atakmap.android.demohelloworld.plugin.R;
import com.atakmap.android.demohelloworld.DemoService;
import com.atakmap.android.demohelloworld.service.IUptime;
import com.atakmap.android.demohelloworld.service.IUptimeCallback;
import com.atakmap.android.util.NotificationUtil;
import com.atakmap.android.demohelloworld.Util;


/** Demonstrate core Android application and UI elements. */
public class CoreFragment extends Fragment {

    private final String TAG = CoreFragment.class.getSimpleName();
    private final String NOTIFICATION_CHANNEL_ID = "com.atakmap.android.demohelloworld.notify";

    private Context pluginCtx;
    private Context atakCtx;
    private TextView serviceUptimeText;
    private View fragmentView;

    private final IUptimeCallback.Stub handler = new IUptimeCallback.Stub() {
        @Override
        public void update(int uptimeSeconds) throws RemoteException {
            String updateText = "Uptime: " + uptimeSeconds + " sec";
            try {
                // ensure we are on the UI thread when updating the text view
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        serviceUptimeText.setText(updateText);
                    }
                });
            }
            catch (Exception e) {
                Log.w(TAG, "Unable to update uptime text");
                Log.e(TAG, e.toString());
                e.printStackTrace();
            }
        }
    };

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "SERVICE CONNECTED");
            IUptime service = IUptime.Stub.asInterface(iBinder);
            try {
                Log.d(TAG, "ATTEMPTING TO REGISTER");
                service.register(handler);
            } catch (RemoteException e) {
                Log.e(TAG, "FAILED TO REGISTER CALLBACK");
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "SERVICE DISCONNECTED");
        }
    };

    /** Create an instance of the CoreFragment */
    public CoreFragment construct(Context plugin, Context atakContext) {
        pluginCtx = plugin;
        atakCtx = atakContext;
        return this;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = LayoutInflater.from(pluginCtx).inflate(
                R.layout.tab_core, container, false);
        initRadioButton();
        initNotifications();
        initServiceButton();
        return fragmentView;
    }

    @Override
    public void onDestroyView() {
        try { pluginCtx.unbindService(connection); } catch (Exception ignored) { }
        super.onDestroyView();
    }

    /** Setup radio buttons to control gravity of the parent holding the remaining UI elements. */
    private void initRadioButton() {
        TextView labelActiveGravity = fragmentView.findViewById(R.id.label_active_gravity);
        RadioGroup gravityRadioGroup = fragmentView.findViewById(R.id.radio_group_gravity);
        LinearLayout componentContainer = fragmentView.findViewById(R.id.topic_android_container);
        gravityRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
                String activeGravityText = "Gravity Active: ";
                if (checkId ==  R.id.radio_btn_left) {
                    activeGravityText += "Left";
                    componentContainer.setGravity(Gravity.START);
                } else if (checkId ==  R.id.radio_btn_center) {
                    activeGravityText += "Center";
                    componentContainer.setGravity(Gravity.CENTER);
                } else if (checkId ==  R.id.radio_btn_right) {
                    activeGravityText += "Right";
                    componentContainer.setGravity(Gravity.END);
                } else
                    Log.e(TAG, "Check Changed Value Unknown ID" + checkId);
                labelActiveGravity.setText(activeGravityText);
            }
        });
    }

    /** Setup buttons that generate notifications. */
    private void initNotifications() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = pluginCtx.getString(R.string.notify_channel_name);
            String description = pluginCtx.getString(R.string.notify_channel_desc);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager) atakCtx
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        // must utilize an icon from atakmap package
        Button atakNotifyBtn = fragmentView.findViewById(R.id.atak_notification_btn);
        atakNotifyBtn.setOnClickListener(view -> NotificationUtil.getInstance().postNotification(
                1,
                com.atakmap.app.R.drawable.info,
                "NotificationUtil",
                "NotificationUtil",
                "Generated by ATAK Util Notification",
                null,
                true));
        Util.setButtonToast(atakCtx, atakNotifyBtn,
                "Generate Notification using ATAK Utility");

        // notificationId is a unique int for each notification that you must define
        int androidNotificationId = 54321;
        Button androidNotifyBtn = fragmentView.findViewById(R.id.android_notification_btn);
        androidNotifyBtn.setOnClickListener(view -> {
            NotificationManager notificationManager = (NotificationManager) atakCtx
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.Builder builder = (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) ?
                    new Notification.Builder(atakCtx) :
                    new Notification.Builder(atakCtx, NOTIFICATION_CHANNEL_ID);
            builder.setContentTitle("Notification Builder generated notification")
                    .setSmallIcon(com.atakmap.app.R.drawable.hints);
            notificationManager.notify(androidNotificationId, builder.build());
        });
        Util.setButtonToast(atakCtx, androidNotifyBtn,
                "Generate Notification using Android Notification Builder");
    }

    /** Setup button to start a bound uptime counter service. */
    private void initServiceButton() {
        serviceUptimeText = fragmentView.findViewById(R.id.service_uptime);
        Button startServiceBtn = fragmentView.findViewById(R.id.start_service);
        Util.setButtonToast(atakCtx, startServiceBtn, "Starts a Demo Service");
        startServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serviceIntent = new Intent(pluginCtx, DemoService.class);
                boolean result = pluginCtx.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
                Log.d(TAG, "Successfully started and bound to service? " + result);
            }
        });
    }

}
