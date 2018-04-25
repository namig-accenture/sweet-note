package app.view.loginfragment

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import app.view.TestModule
import app.views.launchactivity.LaunchActivity
import com.example.namigtahmazli.sweetnote.R
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest

@RunWith(AndroidJUnit4::class)
internal class LoginFragmentTest : KoinTest {
    @Suppress("MemberVisibilityCanBePrivate")
    @get:Rule
    val activityTestRule = ActivityTestRule(LaunchActivity::class.java, false, false)

    private val context = InstrumentationRegistry.getTargetContext()

    private inline fun launchLoginFragment(mockBlock: () -> Unit = {}) {
        mockBlock()
        activityTestRule.launchActivity(LaunchActivity.getIntent(context))
        onView(withId(R.id.group_login)).perform(click())
    }

    @Test
    fun doNotTypeEmailAndPasswordLoginButtonNotEnabled() {
        launchLoginFragment()
        assertButtonEnabled(false)
    }

    @Test
    fun ifOneOfEmailOrPasswordIsMissedButtonWillNotEnabled() {
        launchLoginFragment()
        onView(withId(R.id.et_email)).perform(typeText("a@b.com"))
        assertButtonEnabled(false)
        onView(withId(R.id.et_password)).perform(typeText("abc"))
        assertButtonEnabled(true)
    }

    @Test
    fun loginInWithValidCredentials() {
        launchLoginFragment()
        onView(withId(R.id.et_email)).perform(typeText(TestModule.VALID_EMAIL))
        onView(withId(R.id.et_password)).perform(typeText(TestModule.VALID_PASSWORD), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())
        onView(withId(android.support.design.R.id.snackbar_text))
                .check(matches(allOf(
                        withText("Logged in"),
                        isDisplayed()
                )))
    }

    private fun assertButtonEnabled(enabled: Boolean) {
        val matcher = if (enabled) {
            isEnabled()
        } else {
            not(isEnabled())
        }
        onView(withId(R.id.btn_login)).check(matches(matcher))
    }
}