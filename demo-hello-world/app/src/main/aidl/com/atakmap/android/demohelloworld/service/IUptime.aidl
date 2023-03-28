// Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
// Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
// IUptime.aidl
package com.atakmap.android.demohelloworld.service;

// Declare any non-default types here with import statements
import com.atakmap.android.demohelloworld.service.IUptimeCallback;

interface IUptime {

    // pass a callback function for use by the uptime counter class
    void register(IUptimeCallback callback);

}