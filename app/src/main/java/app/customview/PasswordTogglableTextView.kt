package app.customview

import android.content.Context
import android.support.v4.content.res.ResourcesCompat
import android.text.InputType
import android.util.AttributeSet
import com.example.namigtahmazli.sweetnote.R

internal class PasswordTogglableTextView : DrawableClickableTextView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defaultAttrStyle: Int) : super(context, attributeSet, defaultAttrStyle)

    private val isPasswordShowing get() = inputType != InputType.TYPE_TEXT_VARIATION_PASSWORD

    init {
        setOnDrawableTouchListener {
            if (it == DrawablePosition.Right) {
                togglePassword()
            }
        }
    }

    private fun togglePassword() {
        inputType = if (isPasswordShowing) {
            setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.design_password_eye, 0)
            typeface = ResourcesCompat.getFont(context, R.font.aclonica)
            InputType.TYPE_TEXT_VARIATION_PASSWORD
        } else {
            setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_remove_red_eye_black_24dp, 0)
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
    }
}