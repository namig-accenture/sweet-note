package app.extensions

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.view.View
import android.view.ViewAnimationUtils
import kotlin.math.sqrt

inline fun View.animateOn(crossinline doAnimation: View.() -> Unit) {
    addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
        override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
            v.removeOnLayoutChangeListener(this)
            doAnimation()
        }
    })
}

fun View.circularRevealOpen(startX: Int, startY: Int, duration: Long = 0L): Animator {
    val radius = sqrt((width * width + height * height).toFloat())
    return ViewAnimationUtils.createCircularReveal(this, startX, startY, 0f, radius).apply {
        interpolator = FastOutSlowInInterpolator()
        this.duration = duration
    }
}

fun View.circularRevealClose(endX: Int, endY: Int, endRadius: Float, duration: Long = 0L): Animator {
    val radius = Math.hypot(width.toDouble(), height.toDouble())
    return ViewAnimationUtils.createCircularReveal(this, endX, endY, radius.toFloat(), endRadius).apply {
        interpolator = FastOutSlowInInterpolator()
        this.duration = duration
    }
}

inline fun valueAnimation(startValue: Int, endValue: Int, duration: Long = 0L,
                          crossinline onValueChanged: (Int) -> Unit): Animator {
    return ValueAnimator.ofInt(startValue, endValue).apply {
        addUpdateListener {
            onValueChanged(it.animatedValue as Int)
        }
        setDuration(duration)
    }
}

operator fun AnimatorSet.plus(animator: Animator): AnimatorSet {
    play(animator)
    return this
}

operator fun AnimatorSet.plusAssign(animator: Animator){
    play(animator)
}

inline fun Animator.onAnimationEnd(crossinline onEnd: (Animator?) -> Unit) {
    val animationListener = object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationEnd(animation: Animator?) {
            onEnd(animation)
        }

        override fun onAnimationCancel(animation: Animator?) {
        }

        override fun onAnimationStart(animation: Animator?) {
        }
    }
    addListener(animationListener)
}