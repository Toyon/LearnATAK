// Copyright (c) 2023 Toyon Research Corporation. No Rights Reserved.
// Report problems or provide feedback at: https://github.com/Toyon/LearnATAK/issues
// IUptimeCallback.aidl
package com.atakmap.android.demohelloworld.service;

interface IUptimeCallback {

    // function to do something when a second is "counted"
    void update(int uptimeSeconds);

}