package app.view

import android.app.Activity
import android.support.annotation.IdRes
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule

inline fun <reified A : Activity> provideActivityTestRule(initialTouchMode: Boolean = false,
                                                          launchActivity: Boolean = true): ActivityTestRule<A> {
    return ActivityTestRule(A::class.java, initialTouchMode, launchActivity)
}

inline fun <T : Any> Iterable<T>.typeText(@IdRes id: Int, block: (Int, Int, T) -> Unit) {
    forEachIndexed { index, t ->
        onView(withId(id)).perform(typeText(t.toString()))
        block(index, this.count() - 1, t)
    }
}