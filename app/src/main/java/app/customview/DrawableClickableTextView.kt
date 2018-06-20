package app.customview

import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.view.MotionEvent
import app.extensions.contains

internal open class DrawableClickableTextView : AppCompatTextView {
    enum class DrawablePosition(val position: Int) {
        Top(1), Left(0), Right(2), Bottom(3);
    }

    private var onDrawableClick: ((DrawablePosition) -> Unit)? = null
    private val thisViewLocationOnScreen = Rect()
    private val leftDrawableApproximateArea = Rect()
    private val rightDrawableApproximateArea = Rect()
    private val topDrawableApproximateArea = Rect()
    private val bottomDrawableApproximateArea = Rect()
    private val cordinatesOfThisViewOnScreen = IntArray(2)

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defaultAttrStyle: Int) : super(context, attributeSet, defaultAttrStyle)

    fun setOnDrawableTouchListener(action: (DrawablePosition) -> Unit) {
        onDrawableClick = action
    }

    private fun calculateApproximateCordinates() {
        getLocationOnScreen(cordinatesOfThisViewOnScreen)
        val left = cordinatesOfThisViewOnScreen[0] + paddingLeft
        val right = left + measuredWidth - paddingEnd
        val top = cordinatesOfThisViewOnScreen[1] + paddingTop
        val bottom = top + measuredHeight - paddingBottom

        thisViewLocationOnScreen.set(left, top, right, bottom)

        compoundDrawablesRelative[DrawablePosition.Left.position]?.let {
            leftDrawableApproximateArea.set(
                    left,
                    top + (bottom - top - it.intrinsicHeight) / 2,
                    left + it.intrinsicWidth,
                    bottom - (bottom - top - it.intrinsicHeight) / 2
            )
        }

        compoundDrawablesRelative[DrawablePosition.Right.position]?.let {
            rightDrawableApproximateArea.set(
                    right - it.intrinsicWidth,
                    top + (bottom - top - it.intrinsicHeight) / 2,
                    right,
                    bottom - (bottom - top - it.intrinsicHeight) / 2
            )
        }

        compoundDrawablesRelative[DrawablePosition.Top.position]?.let {
            topDrawableApproximateArea.set(
                    left + (right - left - it.intrinsicWidth) / 2,
                    top,
                    right - (right - left - it.intrinsicWidth) / 2,
                    top + it.intrinsicHeight
            )
        }

        compoundDrawablesRelative[DrawablePosition.Bottom.position]?.let {
            bottomDrawableApproximateArea.set(
                    left + (right - left - it.intrinsicWidth) / 2,
                    bottom - it.intrinsicHeight,
                    right - (right - left - it.intrinsicWidth) / 2,
                    bottom
            )
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculateApproximateCordinates()
    }

    override fun performClick(): Boolean {
        super.performClick()
        return false
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        performClick()
        if (event?.action == MotionEvent.ACTION_DOWN) {
            onDrawableClick ?: return false
            calculateApproximateCordinates()
            val touchX = event.rawX
            val touchY = event.rawY
            val touchPoint = Point(touchX.toInt(), touchY.toInt())
            when (touchPoint) {
                in leftDrawableApproximateArea -> onDrawableClick?.invoke(DrawablePosition.Left)
                in rightDrawableApproximateArea -> onDrawableClick?.invoke(DrawablePosition.Right)
                in topDrawableApproximateArea -> onDrawableClick?.invoke(DrawablePosition.Top)
                in bottomDrawableApproximateArea -> onDrawableClick?.invoke(DrawablePosition.Bottom)
                else -> return false
            }
            return true
        }
        return super.onTouchEvent(event)
    }
}
