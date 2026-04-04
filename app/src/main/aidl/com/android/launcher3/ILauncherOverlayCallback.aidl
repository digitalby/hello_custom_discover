package com.android.launcher3;

oneway interface ILauncherOverlayCallback {
    void overlayScrollChanged(float progress);
    void overlayStatusChanged(int status);
}
