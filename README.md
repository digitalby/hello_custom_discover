# hello_custom_discover

A minimal Android app that replaces the left-swipe "−1 page" feed panel on Launcher3-based launchers with a custom view, using the undocumented `ILauncherOverlay` AIDL protocol. No root required.

## How it works

AOSP Launcher3 (and forks built on it) queries for any installed app that exports a service with the action `com.android.launcher3.WINDOW_OVERLAY`. When found, it binds to the service and casts the returned binder to `ILauncherOverlay` — a reverse-engineered AIDL interface from the Launcher3 source.

The service receives a window token from the launcher via `windowAttached` or `windowAttached2`, adds its view to that window using `WindowManager` with `TYPE_APPLICATION_PANEL` (no `SYSTEM_ALERT_WINDOW` permission needed), and responds to scroll/open/close callbacks to track the swipe gesture.

## Compatibility

| Launcher | Status |
|----------|--------|
| Lawnchair | Works |
| Rootless Pixel Launcher | Works |
| Other Launcher3 forks | Should work |
| Stock Pixel Launcher | Untested — may restrict the overlay to Google's own package on some builds |
| Samsung One UI | Not possible without root ([see #1](https://github.com/digitalby/hello_custom_discover/issues/1)) |

## Project structure

```
app/src/main/
├── aidl/com/android/launcher3/
│   ├── ILauncherOverlay.aidl         # Overlay service interface (must be this package)
│   └── ILauncherOverlayCallback.aidl # Scroll/status callbacks from the launcher
└── java/info/yuryv/hellocustomdiscover/
    ├── OverlayController.kt           # ILauncherOverlay.Stub — the feed panel implementation
    ├── HelloOverlayService.kt         # Bound service that returns OverlayController
    └── DebugActivity.kt               # Launcher icon entry point for standalone preview
```

The AIDL package **must** be `com.android.launcher3`. The binder descriptor is derived from the package name and must match the descriptor compiled into the launcher — any deviation causes a silent cast failure.

## Build and sideload

```bash
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

Then set a Launcher3-based launcher (e.g. Lawnchair) as your default and swipe left from the home screen.

The `DebugActivity` (the app's launcher icon) simulates the window attachment flow in-process so you can preview the overlay without switching launchers.

## Known limitations

- **`windowAttached2` bundle keys are undocumented.** Different Launcher3 forks use different Bundle keys for the window token. `OverlayController` tries `window_on_top_of`, `window`, and `window_token` in order and logs the actual keys received on first launch. If your launcher uses a different key the overlay will not attach; check logcat for `OverlayController` to find the key name.
- **Samsung One UI:** no public API exists for replacing the zero-page panel in a sideloaded app. Tracked in [#1](https://github.com/digitalby/hello_custom_discover/issues/1).
