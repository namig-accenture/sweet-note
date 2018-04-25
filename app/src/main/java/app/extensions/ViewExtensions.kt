package app.extensions

import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.TextView
import com.example.namigtahmazli.sweetnote.R

enum class Duration(val type: Int) {
    SHORT(Snackbar.LENGTH_SHORT), LONG(Snackbar.LENGTH_LONG)
}

inline fun <V : View> V.showSnackBar(@StringRes messageId: Int? = null,
                                     message: String = "",
                                     duration: Duration = Duration.SHORT,
                                     @StringRes buttonTextId: Int = R.string.ok,
                                     noinline action: (View) -> Unit = {},
                                     textView: (TextView) -> Unit = {},
                                     snackBar: (Snackbar) -> Unit = {}) {
    when {
        messageId != null -> Snackbar.make(this, messageId, duration.type)
        else -> Snackbar.make(this, message, duration.type)
    }.apply {
        textView(view.findViewById(android.support.design.R.id.snackbar_text))
        snackBar(this)
        setAction(buttonTextId, action)
        show()
    }
}