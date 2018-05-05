package app.view.registerfragment

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.isEnabled
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.runner.AndroidJUnit4
import app.TestModule
import app.hasError
import app.provideIntentTestRule
import app.typeText
import app.views.launchactivity.LaunchActivity
import app.views.pinactivity.PinActivity
import com.example.namigtahmazli.sweetnote.R
import com.fernandocejas.arrow.optional.Optional
import domain.repositories.UserRepository
import io.reactivex.Single
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.mockito.Mockito

@RunWith(AndroidJUnit4::class)
internal class RegisterFragmentTest : KoinTest {
    @Suppress("MemberVisibilityCanBePrivate")
    @get:Rule
    val activityTestRule = provideIntentTestRule<LaunchActivity>(launchActivity = false)

    private val userRepository by inject<UserRepository>()

    private val context = InstrumentationRegistry.getTargetContext()

    private val emailError: String by lazy { context.getString(R.string.register_fragment_invalid_email_message) }

    private val passwordError: String by lazy { context.getString(R.string.register_fragment_invalid_password_message) }

    @Before
    fun setUp() {
        userRepository.apply {
            Mockito.`when`(currentUser).thenReturn(Single.just(Optional.absent()))
        }
        activityTestRule.launchActivity(LaunchActivity.getIntent(context))
        onView(withId(R.id.group_register)).perform(click())
    }

    @Test
    fun typeInvalidEmailErrorWillBeShown() {
        "abc".asIterable().typeText(R.id.et_email) { _, _, _ ->
            onView(withId(R.id.email_layout)).check(matches(hasError(emailError)))
        }
    }

    @Test
    fun typeValidEmailErrorWillBeGone() {
        "a@b.co".asIterable().typeText(R.id.et_email) { index, lastIndex, _ ->
            val matcher = if (index == lastIndex) {
                not(hasError(emailError))
            } else {
                hasError(emailError)
            }
            onView(withId(R.id.email_layout)).check(matches(matcher))
        }
    }

    @Test
    fun typeInvalidPasswordErrorWillBeShown() {
        "abcnd1234".asIterable().typeText(R.id.et_password) { _, _, _ ->
            onView(withId(R.id.password_layout)).check(matches(hasError(passwordError)))
        }
    }

    @Test
    fun typeValidPasswordErrorWillBeGone() {
        TestModule.VALID_PASSWORD.asIterable().typeText(R.id.et_password) { index, lastIndex, _ ->
            val matcher = if (index == lastIndex) {
                not(hasError(passwordError))
            } else {
                hasError(passwordError)
            }
            onView(withId(R.id.password_layout)).check(matches(matcher))
        }
    }

    @Test
    fun whenEmailAndPasswordAreValidRegisterButtonWillBeEnabled() {
        onView(withId(R.id.et_email)).perform(typeText(TestModule.VALID_EMAIL), closeSoftKeyboard())
        assertButtonEnabled(false)
        onView(withId(R.id.et_password)).perform(typeText(TestModule.VALID_PASSWORD), closeSoftKeyboard())
        assertButtonEnabled(true)
    }

    @Test
    fun enterValidEmailAndPasswordPressRegisterSucceed() {
        onView(withId(R.id.et_email)).perform(typeText(TestModule.VALID_EMAIL), closeSoftKeyboard())
        onView(withId(R.id.et_password)).perform(typeText(TestModule.VALID_PASSWORD), closeSoftKeyboard())
        onView(withId(R.id.btn_register)).check(matches(isEnabled())).perform(click())
        intended(hasComponent(PinActivity::class.java.name))
    }

    private fun assertButtonEnabled(isEnabled: Boolean) {
        val matcher = if (isEnabled) ViewMatchers.isEnabled() else not(ViewMatchers.isEnabled())
        onView(withId(R.id.btn_register)).check(matches(matcher))
    }
}