package info.yuryv.hellocustomdiscover

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.PixelFormat
import android.os.Bundle
import android.os.IBinder
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.WindowManager
import com.android.launcher3.ILauncherOverlay
import com.android.launcher3.ILauncherOverlayCallback

class DebugActivity : Activity() {

    private var connection: ServiceConnection? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent("com.android.launcher3.WINDOW_OVERLAY")
        intent.setPackage(packageName)

        connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val overlay = ILauncherOverlay.Stub.asInterface(service)
                val params = WindowManager.LayoutParams(
                    MATCH_PARENT,
                    MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_PANEL,
                    0,
                    PixelFormat.TRANSLUCENT,
                )
                params.token = window.decorView.windowToken
                overlay.windowAttached(params, NoOpCallback(), 0)
            }

            override fun onServiceDisconnected(name: ComponentName?) {}
        }

        bindService(intent, connection!!, BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        connection?.let { unbindService(it) }
        connection = null
    }

    private class NoOpCallback : ILauncherOverlayCallback.Stub() {
        override fun overlayScrollChanged(progress: Float) {}
        override fun overlayStatusChanged(status: Int) {}
    }
}
