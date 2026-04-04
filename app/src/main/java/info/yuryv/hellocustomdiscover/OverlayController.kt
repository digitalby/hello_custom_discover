package info.yuryv.hellocustomdiscover

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import com.android.launcher3.ILauncherOverlay
import com.android.launcher3.ILauncherOverlayCallback

class OverlayController(private val context: Context) : ILauncherOverlay.Stub() {

    private val windowManager: WindowManager = context.getSystemService(WindowManager::class.java)
    private val mainHandler = Handler(Looper.getMainLooper())

    // Accessed only on the main thread
    private var overlayView: View? = null
    private var callback: ILauncherOverlayCallback? = null
    private var lastScrollProgress = 0f

    // --- Window attachment ---

    override fun windowAttached(
        attrs: WindowManager.LayoutParams,
        cb: ILauncherOverlayCallback,
        options: Int,
    ) {
        mainHandler.post {
            detachView()
            callback = cb
            val view = buildOverlayView()
            overlayView = view
            windowManager.addView(view, attrs)
            cb.overlayStatusChanged(STATUS_READY)
        }
    }

    override fun windowAttached2(bundle: Bundle, cb: ILauncherOverlayCallback) {
        Log.d(TAG, "windowAttached2 bundle keys: ${bundle.keySet()}")

        val token = BUNDLE_TOKEN_KEYS.firstNotNullOfOrNull { key -> bundle.getBinder(key) }

        val params = WindowManager.LayoutParams(
            MATCH_PARENT,
            MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_PANEL,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT,
        )
        if (token != null) {
            params.token = token
        }

        windowAttached(params, cb, 0)
    }

    override fun windowDetached(isChangingConfigurations: Boolean) {
        mainHandler.post { detachView() }
    }

    // --- Scroll ---

    override fun startScroll() {}

    override fun onScroll(progress: Float) {
        // progress 0.0 = overlay fully visible; 1.0 = home screen fully visible (overlay off-screen)
        lastScrollProgress = progress
        mainHandler.post {
            val view = overlayView ?: return@post
            view.translationX = -view.width.toFloat() * progress
        }
    }

    override fun endScroll() {
        val progress = lastScrollProgress
        mainHandler.post {
            callback?.overlayScrollChanged(progress)
        }
    }

    // --- Programmatic show/hide ---

    override fun openOverlay(options: Int) {
        mainHandler.post {
            overlayView?.animate()?.translationX(0f)?.start()
        }
    }

    override fun closeOverlay(options: Int) {
        mainHandler.post {
            val view = overlayView ?: return@post
            view.animate().translationX(-view.width.toFloat()).start()
        }
    }

    // --- Voice (not implemented) ---

    override fun requestVoiceDetection(start: Boolean) {}
    override fun getVoiceSearchLanguage(): String = ""
    override fun isVoiceDetectionRunning(): Boolean = false

    // --- Lifecycle / misc ---

    override fun onPause() {}
    override fun onResume() {}
    override fun multiInstanceUpdateState(state: Int) {}
    override fun disableWindowCornerRadius(disable: Boolean) {}

    // --- Internal ---

    private fun buildOverlayView(): View = FrameLayout(context).apply {
        setBackgroundColor(Color.BLACK)
        addView(
            TextView(context).apply {
                text = "Hello from -1 page"
                textSize = 24f
                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER
                layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            }
        )
    }

    private fun detachView() {
        val view = overlayView ?: return
        overlayView = null
        callback = null
        try {
            windowManager.removeViewImmediate(view)
        } catch (_: IllegalArgumentException) {
            // view was never successfully added
        }
    }

    companion object {
        private const val TAG = "OverlayController"
        private const val STATUS_READY = 1

        // Common Bundle keys used by different Launcher3 forks for the window IBinder token
        private val BUNDLE_TOKEN_KEYS = listOf("window_on_top_of", "window", "window_token")
    }
}
