// Tencent is pleased to support the open source community by making ncnn available.
//
// Copyright (C) 2021 THL A29 Limited, a Tencent company. All rights reserved.
//
// Licensed under the BSD 3-Clause License (the "License"); you may not use this file except
// in compliance with the License. You may obtain a copy of the License at
//
// https://opensource.org/licenses/BSD-3-Clause
//
// Unless required by applicable law or agreed to in writing, software distributed
// under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
// CONDITIONS OF ANY KIND, either express or implied. See the License for the
// specific language governing permissions and limitations under the License.

package com.atakmap.android.democnn;

import android.content.res.AssetManager;
import android.util.Log;
import android.view.Surface;

public class NcnnYolov7 {

  public native boolean loadModel(AssetManager mgr);
  public native boolean createCamera();
  public native boolean openCamera();
  public native boolean closeCamera();
  public native boolean destroyCamera();
  public native boolean setOutputWindow(Surface surface);
  public native boolean releaseOutputWindow(Surface surface);

  // added to example open source code
  public native void capture();
  public native void setImageDirectory(String absolutePath);


  static {
    try {
      System.loadLibrary("democnn");
    } catch (UnsatisfiedLinkError e) {
      Log.w("NcnnYolov7.jaja", "Already loaded shared library");
    }
  }
}
