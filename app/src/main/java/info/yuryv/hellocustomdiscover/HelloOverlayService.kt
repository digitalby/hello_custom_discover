package info.yuryv.hellocustomdiscover

import android.app.Service
import android.content.Intent
import android.os.IBinder

class HelloOverlayService : Service() {
    override fun onBind(intent: Intent?): IBinder = OverlayController(this)
}
