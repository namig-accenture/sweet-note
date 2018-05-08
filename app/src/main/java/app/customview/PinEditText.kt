package app.customview

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.support.design.widget.TextInputEditText
import android.support.v4.content.ContextCompat
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet
import app.extensions.use
import com.example.namigtahmazli.sweetnote.R

internal class PinEditText : TextInputEditText {

    private lateinit var rectanglePaint: Paint
    private lateinit var dotPaint: Paint
    private lateinit var selectedPinPaint: Paint
    private val density: Float by lazy { context.resources.displayMetrics.density }
    private val defaultStrokeWidth = 3 * density
    private val preferredSpacing = 8 * density
    private val preferredPinWidth = 48 * density
    private val defaultPadding = 3 * density
    private var preferredRadiusForDot = 0f
    private val rectangles: MutableList<Rect> = mutableListOf()
    var defaultPinValueLength = 4

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init(context, attributeSet)
    }

    constructor(context: Context, attributeSet: AttributeSet, defaultAttrStyle: Int) : super(context, attributeSet, defaultAttrStyle) {
        init(context, attributeSet, defaultAttrStyle)
    }

    private fun init(context: Context, attributeSet: AttributeSet? = null, defaultAttrStyle: Int = 0) {

        context.theme.obtainStyledAttributes(
                attributeSet,
                R.styleable.PinEditText,
                defaultAttrStyle,
                0).use {
            defaultPinValueLength = getInteger(R.styleable.PinEditText_pin_length, 4)
        }

        isCursorVisible = false
        filters = arrayOf(InputFilter.LengthFilter(defaultPinValueLength))
        inputType = InputType.TYPE_NUMBER_VARIATION_PASSWORD or InputType.TYPE_CLASS_NUMBER
        background = ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent))
        setPadding(defaultPadding.toInt(), defaultPadding.toInt(), defaultPadding.toInt(), defaultPadding.toInt())

        rectanglePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ContextCompat.getColor(context, R.color.colorPrimary)
            style = Paint.Style.STROKE
            strokeWidth = defaultStrokeWidth
        }

        selectedPinPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ContextCompat.getColor(context, R.color.colorAccent)
            style = Paint.Style.STROKE
            strokeWidth = defaultStrokeWidth
        }

        dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ContextCompat.getColor(context, R.color.colorPrimary)
            style = Paint.Style.FILL
        }

        (1..defaultPinValueLength).forEach {
            rectangles.add(Rect())
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = preferredPinWidth * defaultPinValueLength + (defaultPinValueLength - 1) * preferredSpacing + paddingLeft + paddingRight
        val height = preferredPinWidth + paddingTop + paddingBottom

        preferredRadiusForDot = preferredPinWidth / 4

        rectangles.forEachIndexed { index, rect ->
            val left = if (index == 0) {
                paddingLeft
            } else {
                rectangles[index - 1].right + preferredSpacing.toInt()
            }
            val top = paddingTop
            val right = left + preferredPinWidth.toInt() - if (index == rectangles.lastIndex) paddingRight else 0
            val bottom = top + preferredPinWidth.toInt() - paddingBottom
            rect.set(left, top, right, bottom)
        }

        setMeasuredDimension(width.toInt(), height.toInt())
    }

    override fun onDraw(canvas: Canvas?) {
        canvas ?: return
        rectangles.forEach {
            canvas.drawRect(it, rectanglePaint)
        }

        text?.toString()?.forEachIndexed { index, _ ->
            val cx = rectangles[index].left + rectangles[index].width() / 2
            val cy = rectangles[index].top + rectangles[index].height() / 2
            canvas.drawCircle(cx.toFloat(), cy.toFloat(), preferredRadiusForDot, dotPaint)
        }

        val currentIndex = length() - 1
        val lastIndex = defaultPinValueLength - 1
        if (currentIndex + 1 <= lastIndex) {
            canvas.drawRect(rectangles[currentIndex + 1], selectedPinPaint)
        }

        if ((text == null || text.isEmpty()) && hasFocus()) {
            canvas.drawRect(rectangles[0], selectedPinPaint)
        }
    }

    fun animateOnInValidPin() {
        val objectAnimator = ObjectAnimator.ofFloat(this, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f)
        objectAnimator.duration = 1000L
        objectAnimator.start()
    }
}