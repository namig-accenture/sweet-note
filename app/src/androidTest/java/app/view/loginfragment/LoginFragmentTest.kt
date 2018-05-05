package app.view.loginfragment

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.runner.AndroidJUnit4
import app.TestModule
import app.assertButtonEnabled
import app.provideIntentTestRule
import app.views.launchactivity.LaunchActivity
import app.views.pinactivity.PinActivity
import com.example.namigtahmazli.sweetnote.R
import com.fernandocejas.arrow.optional.Optional
import domain.repositories.UserRepository
import io.reactivex.Single
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
internal class LoginFragmentTest : KoinTest {
    @Suppress("MemberVisibilityCanBePrivate")
    @get:Rule
    val activityTestRule = provideIntentTestRule<LaunchActivity>(launchActivity = false)

    private val context = InstrumentationRegistry.getTargetContext()

    private val userRepository: UserRepository by inject()

    private fun launchLoginFragment(mock: UserRepository.() -> Unit = {}) {
        userRepository.mock()
        activityTestRule.launchActivity(LaunchActivity.getIntent(context))
        onView(withId(R.id.group_login)).perform(click())
    }

    @Test
    fun doNotTypeEmailAndPasswordLoginButtonNotEnabled() {
        launchLoginFragment {
            `when`(currentUser).thenReturn(Single.just(Optional.absent()))
        }
        assertButtonEnabled(R.id.btn_login, enabled = false)
    }

    @Test
    fun ifOneOfEmailOrPasswordIsMissedButtonWillNotEnabled() {
        launchLoginFragment {
            `when`(currentUser).thenReturn(Single.just(Optional.absent()))
        }
        onView(withId(R.id.et_email)).perform(typeText("a@b.com"))
        assertButtonEnabled(R.id.btn_login, enabled = false)
        onView(withId(R.id.et_password)).perform(typeText("abc"))
        assertButtonEnabled(R.id.btn_login, enabled = true)
    }

    @Test
    fun loginInWithValidCredentials() {
        launchLoginFragment {
            `when`(currentUser).thenReturn(Single.just(Optional.absent()))
        }
        onView(withId(R.id.et_email)).perform(typeText(TestModule.VALID_EMAIL))
        onView(withId(R.id.et_password)).perform(typeText(TestModule.VALID_PASSWORD), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())
        intended(hasComponent(PinActivity::class.java.name))
    }
}