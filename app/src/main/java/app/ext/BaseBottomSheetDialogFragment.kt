package app.ext

import android.app.Dialog
import android.graphics.Point
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.BottomSheetDialogFragment
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.accessibility.AccessibilityEvent
import android.widget.FrameLayout

internal abstract class BaseBottomSheetDialogFragment : BottomSheetDialogFragment() {
    val bottomSheetBehavior: BottomSheetBehavior<FrameLayout>? by lazy {
        (dialog as BottomSheetDialog).findViewById<FrameLayout>(android.support.design.R.id.design_bottom_sheet)?.let {
            BottomSheetBehavior.from(it)
        }
    }

    open fun onStateChanged(state: Int) {
    }

    open fun onSlideOffsetChanged(slideOffset: Float) {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                bottomSheetBehavior?.apply {
                    state = BottomSheetBehavior.STATE_EXPANDED
                    peekHeight = 0
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        bottomSheetBehavior?.apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
            val size = Point().apply {
                activity?.windowManager?.defaultDisplay?.getSize(this)
            }
            peekHeight = size.y
            setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    onSlideOffsetChanged(slideOffset)
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    onStateChanged(state = newState)
                }
            })
        }
    }

    private fun onUserInteraction() {
        activity?.onUserInteraction()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : BottomSheetDialog(context!!, theme) {
            override fun dispatchGenericMotionEvent(ev: MotionEvent?): Boolean {
                onUserInteraction()
                return super.dispatchGenericMotionEvent(ev)
            }

            override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
                onUserInteraction()
                return super.dispatchKeyEvent(event)
            }

            override fun dispatchKeyShortcutEvent(event: KeyEvent?): Boolean {
                onUserInteraction()
                return super.dispatchKeyShortcutEvent(event)
            }

            override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
                onUserInteraction()
                return super.dispatchTouchEvent(ev)
            }

            override fun dispatchTrackballEvent(ev: MotionEvent?): Boolean {
                onUserInteraction()
                return super.dispatchTrackballEvent(ev)
            }
        }
    }
}