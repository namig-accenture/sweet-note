package app.customview

import android.content.Context
import android.util.AttributeSet
import android.widget.ProgressBar

class ProgressBar : ProgressBar {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defaultStyleAttr: Int) : super(context, attributeSet, defaultStyleAttr)
}