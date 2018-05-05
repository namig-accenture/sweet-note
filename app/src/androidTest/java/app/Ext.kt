package app

import android.app.Activity
import android.support.annotation.IdRes
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import domain.sleep

inline fun <reified A : Activity> provideActivityTestRule(initialTouchMode: Boolean = false,
                                                          launchActivity: Boolean = true): ActivityTestRule<A> {
    return ActivityTestRule(A::class.java, initialTouchMode, launchActivity)
}

inline fun <reified A : Activity> provideIntentTestRule(initialTouchMode: Boolean = false,
                                                        launchActivity: Boolean = true): IntentsTestRule<A> {
    return IntentsTestRule(A::class.java, initialTouchMode, launchActivity)
}

inline fun <T : Any> Iterable<T>.typeText(@IdRes id: Int, block: (Int, Int, T) -> Unit) {
    forEachIndexed { index, t ->
        onView(withId(id)).perform(typeText(t.toString()))
        sleep(100)
        block(index, this.count() - 1, t)
    }
}

inline fun <R> testModule(block: TestModule.Companion.() -> R): R = TestModule.block()