package app.ext

import android.databinding.BindingAdapter
import android.support.design.widget.TextInputLayout
import android.view.View
import app.customview.PinEditText

@BindingAdapter(value = ["android:visibility"])
fun setVisibility(view: View, isVisible: Boolean) {
    view.visibility = if (isVisible) View.VISIBLE else View.GONE
}

@BindingAdapter(value = ["app:pin_length"])
internal fun setPinLength(pinEditText: PinEditText, length: Int) {
    pinEditText.defaultPinValueLength = length
}

@BindingAdapter(value = ["app:error_text"])
internal fun setError(textInputLayout: TextInputLayout, message: String?) {
    textInputLayout.error = message
}