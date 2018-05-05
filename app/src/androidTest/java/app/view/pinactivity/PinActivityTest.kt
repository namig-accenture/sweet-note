package app.view.pinactivity

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.runner.AndroidJUnit4
import app.*
import app.parcelables.PinActivityIntentModel
import app.views.pinactivity.PinActivity
import com.example.namigtahmazli.sweetnote.R
import com.fernandocejas.arrow.optional.Optional
import domain.extensions.asOptional
import domain.model.EnterPinType
import domain.repositories.UserRepository
import domain.sleep
import io.reactivex.Single
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
internal class PinActivityTest : KoinTest {

    @Suppress("MemberVisibilityCanBePrivate")
    @get:Rule
    val activityTestRule = provideActivityTestRule<PinActivity>(launchActivity = false)

    private val context = InstrumentationRegistry.getTargetContext()

    private val userRepository: UserRepository by inject()

    private fun launchActivity(type: EnterPinType, mock: UserRepository.() -> Unit = {}) {
        userRepository.mock()
        activityTestRule.launchActivity(PinActivity.provideIntent(context, PinActivityIntentModel(type)))
    }

    @Test
    fun launchPinActivityForRegisteringPinContinueButtonIsVisible() {
        launchActivity(EnterPinType.Register)
        onView(withId(R.id.btn_continue)).check(matches(isDisplayed()))
    }

    @Test
    fun launchPinActivityForRegisteringPinContinueButtonIsDisabled() {
        launchActivity(EnterPinType.Register)
        onView(withId(R.id.btn_continue)).check(matches(not(isEnabled())))
    }

    @Test
    fun launchPinActivityForRegisteringPinContinueButtonIsEnabledAfterEnteringPin() {
        launchActivity(EnterPinType.Register)
        assertButtonEnabled(R.id.btn_continue, false)
        (1..4).typeText(R.id.pin) { i, li, _ ->
            assertButtonEnabled(R.id.btn_continue, i == li)
        }
    }

    @Test
    fun launchPinActivityForRegisteringPinEnterPinClickContinueButtonSucceed() {
        launchActivity(EnterPinType.Register) {
            `when`(currentUser).thenReturn(Single.just(testModule { user.copy(id = ID).asOptional }))
        }
        onView(withId(R.id.pin)).perform(typeText(TestModule.PIN), closeSoftKeyboard())
        onView(withId(R.id.btn_continue)).check(matches(isEnabled())).perform(click())
        onView(withId(android.support.design.R.id.snackbar_text)).check(matches(
                allOf(
                        withText("Registered pin."),
                        isDisplayed()
                )))
    }

    @Test
    fun launchPinActivityForRegisteringPinIfNoCurrentLoggedInUserFoundWillShowError() {
        launchActivity(EnterPinType.Register) {
            `when`(currentUser).thenReturn(Single.just(Optional.absent()))
        }
        onView(withId(R.id.pin)).perform(typeText(TestModule.PIN), closeSoftKeyboard())
        onView(withId(R.id.btn_continue)).check(matches(isEnabled())).perform(click())
        onView(withId(android.support.design.R.id.snackbar_text)).check(matches(
                allOf(
                        withText(R.string.pin_activity_current_user_not_found_error_message),
                        isDisplayed()
                )))
    }

    @Test
    fun launchPinActivityForLoggingWithPinContinueButtonNotShown() {
        launchActivity(EnterPinType.Login)
        onView(withId(R.id.btn_continue)).check(matches(not(isDisplayed())))
    }

    @Test
    fun launchPinActivityForLoggingWithPinEnterValidPinWillSucceed() {
        launchActivity(EnterPinType.Login) {
            `when`(currentUser).thenReturn(Single.just(testModule { user.copy(id = ID, pin = PIN).asOptional }))
        }
        TestModule.PIN.asIterable().typeText(R.id.pin) { i, li, _ ->
            if (i == li) {
                sleep(1000)
                onView(withId(android.support.design.R.id.snackbar_text)).check(matches(
                        allOf(
                                withText("Valid pin"),
                                isDisplayed()
                        )))
            }
        }
    }

    @Test
    fun launchPinActivityForLoggingWithPinEnterInvalidPinWillShowError() {
        launchActivity(EnterPinType.Login) {
            `when`(currentUser).thenReturn(Single.just(testModule { user.copy(id = ID, pin = PIN).asOptional }))
        }
        (4..7).typeText(R.id.pin) { index, lastIndex, _ ->
            if (index == lastIndex) {
                sleep(1000)
                onView(withId(android.support.design.R.id.snackbar_text)).check(matches(
                        allOf(
                                withText("Invalid pin"),
                                isDisplayed()
                        )))
            }
        }
    }

    @Test
    fun launchPinActivityForLoggingWithPinIfCurrentUserNotFoundWillShowError() {
        launchActivity(EnterPinType.Login) {
            `when`(currentUser).thenReturn(Single.just(Optional.absent()))
        }
        TestModule.PIN.asIterable().typeText(R.id.pin) { index, lastIndex, _ ->
            if (index == lastIndex) {
                onView(withId(R.id.pin)).perform(closeSoftKeyboard())
                sleep(1000)
                onView(withId(android.support.design.R.id.snackbar_text)).check(matches(
                        allOf(
                                withText(R.string.pin_activity_current_user_not_found_error_message),
                                isDisplayed()
                        )))
            }
        }
    }
}