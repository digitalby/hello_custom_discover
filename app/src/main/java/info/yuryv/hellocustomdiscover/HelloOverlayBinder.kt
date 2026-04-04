package info.yuryv.hellocustomdiscover

import android.content.Context
import android.graphics.Color
import android.os.Binder
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.TextView

class HelloOverlayBinder(
  private val context: Context
) : Binder() {

  fun createView(): View {
    return FrameLayout(context).apply {
      setBackgroundColor(Color.BLACK)

      addView(TextView(context).apply {
        text = "Hello from -1 page"
        textSize = 24f
        setTextColor(Color.WHITE)
        gravity = Gravity.CENTER
        layoutParams = FrameLayout.LayoutParams(
          MATCH_PARENT,
          MATCH_PARENT
        )
      })
    }
  }
}