/**
 * Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
 * Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
 */
package com.atakmap.android.demohelloworld;

import com.atakmap.android.demohelloworld.service.IUptime;
import com.atakmap.android.demohelloworld.service.IUptimeCallback;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DemoService extends Service {

    private final static String TAG = DemoService.class.getSimpleName();

    private final AtomicInteger uptimeSec = new AtomicInteger(0);
    private ScheduledFuture<?> timerSchedule;
    private IUptimeCallback uptimeCallback = null;

    private final IUptime.Stub binder = new IUptime.Stub() {
        @Override
        public void register(IUptimeCallback callback) throws RemoteException {
            uptimeCallback = callback;
            Log.d(TAG, "Registered callback and starting uptime counter ~~~~~~~~~~~~");
        }
    };

    /** The count increment command to execute on one second interval */
    private Runnable countTask() {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    int count = uptimeSec.incrementAndGet();
                    if (uptimeCallback != null)
                        uptimeCallback.update(count);
                } catch (Exception e) {
                    Log.e(TAG, "FAILED TO EXECUTE UPDATE");
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "CREATED SERVICE");
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        // (0) command, (1) initial delay, (2) period, (3) time unit of provided values
        timerSchedule = executor.scheduleAtFixedRate(countTask(), 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "SERVICE DESTROYED");
        if (timerSchedule != null)
            timerSchedule.cancel(true);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "SERVICE BOUND");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "UNBOUND SERVICE");
        return super.onUnbind(intent);
    }

}
