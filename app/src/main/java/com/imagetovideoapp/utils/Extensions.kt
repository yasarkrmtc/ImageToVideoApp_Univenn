import android.os.SystemClock
import android.view.View
import com.imagetovideoapp.utils.Constants


fun View.clickWithDebounce(debounceTime: Long = Constants.debounceTime, action: (View) -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0
        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else action(v)
            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}