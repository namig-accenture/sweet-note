package app.view.launchactivity

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.runner.AndroidJUnit4
import app.provideIntentTestRule
import app.views.launchactivity.LaunchActivity
import com.example.namigtahmazli.sweetnote.R
import com.fernandocejas.arrow.optional.Optional
import domain.repositories.UserRepository
import domain.sleep
import io.reactivex.Single
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.mockito.Mockito.`when`

@Suppress("MemberVisibilityCanBePrivate")
@RunWith(AndroidJUnit4::class)
internal class LaunchActivityTest : KoinTest {

    private val context: Context = InstrumentationRegistry.getTargetContext()

    @get:Rule
    val activityTestRule = provideIntentTestRule<LaunchActivity>(launchActivity = false)

    private val userRepository by inject<UserRepository>()

    private fun launchActivity(mock: UserRepository.() -> Unit = {}) {
        userRepository.mock()
        activityTestRule.launchActivity(LaunchActivity.getIntent(context))
    }

    @Test
    fun ifCurrentlyLoggedInUserAvailableWillLaunchPinActivity() {
        launchActivity()
        onView(withId(R.id.pin)).check(matches(isDisplayed()))
    }

    @Test
    fun ifCurrentlyLoggedInUserNotAvailableWillShowLoginScreen() {
        launchActivity {
            `when`(currentUser).thenReturn(Single.just(Optional.absent()))
        }
        onView(withId(R.id.btn_login)).check(matches(isDisplayed()))
    }

    @Test
    fun switchRadioButtonsWillChangeFragments() {
        launchActivity {
            `when`(currentUser).thenReturn(Single.just(Optional.absent()))
        }
        onView(withId(R.id.group_register)).perform(click())
        onView(withId(R.id.btn_register)).check(matches(isDisplayed()))
        sleep(1000)
        onView(withId(R.id.group_login)).perform(click())
        onView(withId(R.id.btn_login)).check(matches(isDisplayed()))
    }
}