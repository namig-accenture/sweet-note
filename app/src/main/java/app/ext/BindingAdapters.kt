package app.ext

import android.databinding.BindingAdapter
import android.view.View

@BindingAdapter(value = ["android:enabled"])
fun isEnabled(view: View, isEnabled: Boolean) {
    view.isEnabled = isEnabled
    println(isEnabled)
}