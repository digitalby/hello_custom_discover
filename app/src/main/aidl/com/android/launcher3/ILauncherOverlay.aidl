package com.android.launcher3;

import android.os.Bundle;
import com.android.launcher3.ILauncherOverlayCallback;

interface ILauncherOverlay {
    void windowAttached(in WindowManager.LayoutParams attrs,
                        in ILauncherOverlayCallback cb,
                        int options);
    void windowDetached(boolean isChangingConfigurations);
    void closeOverlay(int options);
    void openOverlay(int options);
    void requestVoiceDetection(boolean start);
    String getVoiceSearchLanguage();
    boolean isVoiceDetectionRunning();
    oneway void onPause();
    oneway void onResume();
    oneway void startScroll();
    oneway void onScroll(float progress);
    oneway void endScroll();
    oneway void windowAttached2(in Bundle bundle, in ILauncherOverlayCallback cb);
    oneway void multiInstanceUpdateState(int state);
    oneway void disableWindowCornerRadius(boolean disable);
}
