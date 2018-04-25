package app.view

import android.support.design.widget.TextInputLayout
import android.view.View
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