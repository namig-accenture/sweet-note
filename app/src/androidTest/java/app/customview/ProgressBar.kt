package app.customview

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.widget.ProgressBar

class ProgressBar : ProgressBar {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defaultStyleAttr: Int) : super(context, attributeSet, defaultStyleAttr)

    private val testedByLauncActivity
        get() = try {
            Class.forName("app.view.launchactivity.LaunchActivityTest")
            true
        } catch (ex: Exception) {
            false
        }

    init {
        if (testedByLauncActivity) {
            indeterminateDrawable = ColorDrawable(Color.CYAN)
        }
    }
}