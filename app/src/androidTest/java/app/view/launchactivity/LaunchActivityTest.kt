package app.view.launchactivity

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.runner.AndroidJUnit4
import app.views.launchactivity.LaunchActivity
import com.example.namigtahmazli.sweetnote.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest

@RunWith(AndroidJUnit4::class)
internal class LaunchActivityTest : KoinTest {

    private val context: Context = InstrumentationRegistry.getTargetContext()

    @get:Rule
    val activityTestRule: IntentsTestRule<LaunchActivity> = IntentsTestRule(LaunchActivity::class.java, false, false)

    @Before
    fun setUp() {
        activityTestRule.launchActivity(LaunchActivity.getIntent(context))
    }

    @Test
    fun switchRadioButtonsWillChangeFragments() {
        onView(withId(R.id.group_register)).perform(click())
        onView(withId(R.id.btn_register)).check(matches(isDisplayed()))
        onView(withId(R.id.group_login)).perform(click())
        onView(withId(R.id.btn_login)).check(matches(isDisplayed()))
    }
}