package info.yuryv.hellocustomdiscover

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder

class DebugActivity : Activity() {

    private var connection: ServiceConnection? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent("com.android.launcher3.WINDOW_OVERLAY")
        intent.setPackage(packageName)

        connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as HelloOverlayBinder
                val view = binder.createView()
                setContentView(view)
            }

            override fun onServiceDisconnected(name: ComponentName?) {}
        }

        bindService(intent, connection!!, BIND_AUTO_CREATE)
    }
}