package app.extensions

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.content.res.TypedArray
import android.databinding.ViewDataBinding
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

operator fun <L : Lifecycle> L.plusAssign(observer: LifecycleObserver) {
    addObserver(observer)
}

operator fun <DB : ViewDataBinding> DB.set(id: Int, any: Any?) {
    setVariable(id, any)
}

inline fun TypedArray.use(block: TypedArray.() -> Unit) {
    try {
        block()
    } finally {
        recycle()
    }
}