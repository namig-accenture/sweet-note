package app.view.pinactivity

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.runner.AndroidJUnit4
import app.parcelables.PinActivityIntentModel
import app.view.TestModule
import app.view.assertButtonEnabled
import app.view.provideActivityTestRule
import app.view.typeText
import app.views.pinactivity.PinActivity
import com.example.namigtahmazli.sweetnote.R
import com.fernandocejas.arrow.optional.Optional
import domain.model.EnterPinType
import domain.sleep
import io.reactivex.Single
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
internal class PinActivityTest {

    @Suppress("MemberVisibilityCanBePrivate")
    @get:Rule
    val activityTestRule = provideActivityTestRule<PinActivity>(launchActivity = false)

    private val context = InstrumentationRegistry.getTargetContext()

    private fun launchActivity(type: EnterPinType) {
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
        launchActivity(EnterPinType.Register)
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
        TestModule.additionalUserRepositoryMock = {
            `when`(currentUser).thenReturn(Single.just(Optional.absent()))
        }
        launchActivity(EnterPinType.Register)
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
        launchActivity(EnterPinType.Login)
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
        launchActivity(EnterPinType.Login)
        (4..8).typeText(R.id.pin) { index, lastIndex, _ ->
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
        TestModule.additionalUserRepositoryMock = {
            `when`(currentUser).thenReturn(Single.just(Optional.absent()))
        }
        launchActivity(EnterPinType.Login)
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

    @After
    fun tareDown() {
        TestModule.additionalUserRepositoryMock = {}
    }
}