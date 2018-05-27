package app.view.addeditnotedialog

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.runner.AndroidJUnit4
import app.assertButtonEnabled
import app.assertHasError
import app.provideIntentTestRule
import app.testModule
import app.views.homeactivity.HomeActivity
import com.example.namigtahmazli.sweetnote.R
import domain.extensions.asOptional
import domain.model.FetchNotesRequestModel
import domain.model.NoteModel
import domain.repositories.NoteRepository
import domain.repositories.UserRepository
import domain.safeEq
import domain.sleep
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import org.hamcrest.CoreMatchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.mockito.Mockito.`when` as on

@RunWith(AndroidJUnit4::class)
internal class AddEditNoteDialogTest : KoinTest {
    @get:Rule
    val activityRule = provideIntentTestRule<HomeActivity>(launchActivity = false)

    private val context = InstrumentationRegistry.getTargetContext()
    private val titleError by lazy { context.getString(R.string.add_edit_note_dialog_title_not_defined_error) }
    private val usernameError by lazy { context.getString(R.string.add_edit_note_dialog_username_not_defined_error) }
    private val passwordError by lazy { context.getString(R.string.add_edit_note_dialog_password_not_defined_error) }
    private val homeAsUpContentDescription by lazy { context.getString(R.string.add_edit_note_dialog_home_button_content_description) }
    private val noteRepository by inject<NoteRepository>()
    private val userRepository by inject<UserRepository>()

    private inline fun openAddEditNoteDialog(mockBlock: () -> Unit = {}) {
        mockBlock()
        activityRule.launchActivity(HomeActivity.getIntent(context))
        onView(withId(R.id.btn_add_note)).perform(click())
    }

    @Test
    fun saveButtonIsDisabled() {
        openAddEditNoteDialog()
        assertButtonEnabled(R.id.btn_save_note, false)
    }

    @Test
    fun saveButtonWillBeDisabledUntilAllFieldsFilledIn() {
        openAddEditNoteDialog()
        assertButtonEnabled(R.id.btn_save_note, false)
        onView(withId(R.id.et_title)).perform(typeText("Demo"), closeSoftKeyboard())
        assertButtonEnabled(R.id.btn_save_note, false)
        onView(withId(R.id.et_username)).perform(typeText("demo"), closeSoftKeyboard())
        assertButtonEnabled(R.id.btn_save_note, false)
        onView(withId(R.id.et_password)).perform(typeText("demo"), closeSoftKeyboard())
        assertButtonEnabled(R.id.btn_save_note, true)
        onView(withId(R.id.et_password)).perform(clearText(), closeSoftKeyboard())
        assertButtonEnabled(R.id.btn_save_note, false)
    }

    @Test
    fun secondAttemptToClearTextWillShowErrorForField() {
        openAddEditNoteDialog()

        onView(withId(R.id.et_title)).perform(typeText("demo"), clearText(), closeSoftKeyboard())
        assertHasError(R.id.title_container, titleError, true)

        onView(withId(R.id.et_username)).perform(typeText("demo"), clearText(), closeSoftKeyboard())
        assertHasError(R.id.username_container, usernameError, true)

        onView(withId(R.id.et_password)).perform(typeText("demo"), clearText(), closeSoftKeyboard())
        assertHasError(R.id.password_container, passwordError, true)

        onView(withId(R.id.et_title)).perform(typeText("demo"), closeSoftKeyboard())
        assertHasError(R.id.title_container, titleError, false)

        onView(withId(R.id.et_username)).perform(typeText("demo"), closeSoftKeyboard())
        assertHasError(R.id.title_container, titleError, false)

        onView(withId(R.id.et_password)).perform(typeText("demo"), closeSoftKeyboard())
        assertHasError(R.id.password_container, passwordError, false)
    }

    @Test
    fun clickingSaveButtonWillSaveNote() {
        val listEmitter = PublishSubject.create<List<NoteModel>>()
        var note: NoteModel? = null
        openAddEditNoteDialog {
            testModule {
                note = NoteModel(
                        userId = ID,
                        userName = "demo",
                        password = "demo",
                        title = "demo"
                )
                val user = user.copy(id = ID, pin = PIN)
                on(userRepository.currentUser).thenReturn(Single.just(user.asOptional))
                on(noteRepository.addNote(safeEq(note!!)))
                        .thenReturn(Single.just(note!!.copy(id = ID)))
                on(userRepository.findNotesForUser(safeEq(user),
                        safeEq(FetchNotesRequestModel(1, 0))))
                        .thenReturn(listEmitter)
                on(userRepository.findNotesCountByUserId(safeEq(user)))
                        .thenReturn(Single.just(1))
            }
        }

        onView(withId(R.id.et_title)).perform(typeText("demo"), closeSoftKeyboard())
        onView(withId(R.id.et_username)).perform(typeText("demo"), closeSoftKeyboard())
        onView(withId(R.id.et_password)).perform(typeText("demo"), closeSoftKeyboard())
        onView(withId(R.id.btn_save_note)).perform(click())

        note?.let {
            listEmitter.onNext(listOf(it))
        }

        sleep(1000)

        onView(allOf(withId(R.id.tv_title), withText("demo"))).check(matches(isDisplayed()))
        onView(allOf(withId(R.id.tv_username), withText("demo"))).check(matches(isDisplayed()))
        onView(allOf(withId(R.id.tv_password), withText("demo"))).check(matches(isDisplayed()))
    }

    @Test
    fun backButtonClosedWillCloseDialogAndTakeUserToHomeScreen() {
        openAddEditNoteDialog()
        onView(withContentDescription(homeAsUpContentDescription)).perform(click())
        onView(withId(R.id.btn_add_note)).check(matches(isDisplayed()))
    }
}