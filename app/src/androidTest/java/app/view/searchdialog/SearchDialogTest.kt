package app.view.searchdialog

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.RootMatchers.isDialog
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.runner.AndroidJUnit4
import app.TestModule
import app.provideActivityTestRule
import app.testModule
import app.typeQuery
import app.views.homeactivity.HomeActivity
import com.example.namigtahmazli.sweetnote.R
import domain.model.FetchNotesRequestModel
import domain.model.NoteModel
import domain.repositories.NoteRepository
import domain.repositories.UserRepository
import domain.sleep
import io.reactivex.Observable
import org.hamcrest.CoreMatchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.mockito.Mockito.`when` as on

@RunWith(AndroidJUnit4::class)
internal class SearchDialogTest : KoinTest {

    @JvmField
    @Rule
    val rule = provideActivityTestRule<HomeActivity>(launchActivity = false)

    private val context = InstrumentationRegistry.getTargetContext()

    private val noteRepository by inject<NoteRepository>()
    private val userRepository by inject<UserRepository>()

    private val backButtonContentDescription by lazy {
        context.getString(R.string.search_dialog_back_button_content_description)
    }

    @Before
    fun setUp() {
        testModule {
            on(userRepository.findNotesForUser(user.copy(id = ID),
                    FetchNotesRequestModel(10, 0)))
                    .thenReturn(Observable.just(emptyList()))
        }
        rule.launchActivity(HomeActivity.getIntent(context))
    }

    private fun launchDialog(mockBlock: TestModule.Companion.() -> Unit = {}) {
        testModule { mockBlock() }
        onView(withContentDescription(context.getString(R.string.home_activity_menu_search_title))).perform(click())
    }

    @Test
    fun clickingSaveMenuWillOpenSearchDialog() {
        launchDialog()
        onView(withId(R.id.search_view)).check(matches(isDisplayed()))
    }

    @Test
    fun notesWillBeDisplayedAccordingToSearchQuery() {
        val title = "Face"
        launchDialog {
            val noteModelMe = NoteModel(id = 1,
                    userId = ID,
                    userName = "demoUsername",
                    password = "demoPassword",
                    title = "FacebookMe")

            val noteModelOther = NoteModel(id = 2,
                    userId = ID,
                    userName = "demoUsername",
                    password = "demoPassword",
                    title = "FacebookOther")
            on(noteRepository.findNotesByTitle(user.copy(id = ID), title = title))
                    .thenReturn(Observable.just(listOf(noteModelMe, noteModelOther)))
            on(noteRepository.findNotesByTitle(user.copy(id = ID), title = "${title}bookMe"))
                    .thenReturn(Observable.just(listOf(noteModelMe)))
        }

        onView(withId(R.id.search_view)).perform(typeQuery(title), closeSoftKeyboard())

        sleep(1000)

        onView(allOf(withId(R.id.tv_title), withText("FacebookMe"))).check(matches(isDisplayed()))
        onView(allOf(withId(R.id.tv_title), withText("FacebookOther"))).check(matches(isDisplayed()))

        onView(withId(R.id.search_view)).perform(typeQuery("${title}bookMe"), closeSoftKeyboard())

        sleep(1000)

        onView(allOf(withId(R.id.tv_title), withText("FacebookMe"))).check(matches(isDisplayed()))
    }

    @Test
    fun clickingBackButtonWillCloseSearchDialog() {
        launchDialog()
        onView(withContentDescription(backButtonContentDescription)).perform(click())
        onView(withId(R.id.btn_add_note)).check(matches(isDisplayed()))
    }

    @Test
    fun clickingListItemWillShowDetails() {
        val title = "Face"
        launchDialog {
            val noteModelMe = NoteModel(id = 1,
                    userId = ID,
                    userName = "demoUsername",
                    password = "demoPassword",
                    title = "FacebookMe")
            on(noteRepository.findNotesByTitle(user.copy(id = ID), title))
                    .thenReturn(Observable.just(listOf(noteModelMe)))
        }

        onView(withId(R.id.search_view)).perform(typeQuery(title), closeSoftKeyboard())
        sleep(500)
        onView(withId(R.id.note_card)).check(matches(isDisplayed()))
                .perform(click())

        onView(withId(R.id.tv_title)).inRoot(isDialog()).check(matches(isDisplayed()))
    }
}