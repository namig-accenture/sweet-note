package app

import android.support.annotation.IdRes
import android.support.design.widget.TextInputLayout
import android.support.test.espresso.Espresso
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers
import android.view.View
import org.hamcrest.CoreMatchers
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

fun hasError(error: String): Matcher<View> {
    return object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description?) {
            description?.appendText("Has Error")
        }

        override fun matchesSafely(item: View?): Boolean {
            val testInputLayout = item as? TextInputLayout
                    ?: throw IllegalAccessException("Not TextInputLayout")
            return testInputLayout.error == error
        }

    }
}

fun assertButtonEnabled(@IdRes id: Int, enabled: Boolean) {
    val matcher = if (enabled) {
        ViewMatchers.isEnabled()
    } else {
        CoreMatchers.not(ViewMatchers.isEnabled())
    }
    Espresso.onView(ViewMatchers.withId(id)).check(ViewAssertions.matches(matcher))
}