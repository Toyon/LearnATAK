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

package com.atakmap.android.democamera;

import android.content.res.AssetManager;
import android.view.Surface;

public class NcnnYolov7
{
  public native boolean createCamera();
  public native boolean openCamera();
  public native boolean closeCamera();
  public native boolean destroyCamera();
  public native boolean setOutputWindow(Surface surface);
  public native boolean releaseOutputWindow(Surface surface);

  // added for ATAK orientation info
  public native void setAssetManager(AssetManager assetManager);
  public native void setPrefOrientation(int orientation);

  static {
      System.loadLibrary("demo_camera");
  }
}
