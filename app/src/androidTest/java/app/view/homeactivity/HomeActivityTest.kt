package app.view.homeactivity

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.runner.AndroidJUnit4
import app.provideActivityTestRule
import app.testModule
import app.views.homeactivity.HomeActivity
import com.example.namigtahmazli.sweetnote.R
import domain.extensions.asOptional
import domain.model.FetchNotesRequestModel
import domain.model.NoteModel
import domain.model.UserModel
import domain.repositories.UserRepository
import domain.safeEq
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import org.hamcrest.CoreMatchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.mockito.Mockito.`when` as on

@RunWith(AndroidJUnit4::class)
internal class HomeActivityTest : KoinTest {
    @get:Rule
    val activityTestRule = provideActivityTestRule<HomeActivity>(launchActivity = false)
    private val userRepository by inject<UserRepository>()
    private val context = InstrumentationRegistry.getTargetContext()
    private val user: UserModel by lazy { testModule { user.copy(id = ID, pin = PIN) } }

    @Before
    fun setUp() {
        on(userRepository.currentUser).thenReturn(Single.just(user.asOptional))
    }

    private inline fun launchActivity(mockBlock: () -> Unit = {}) {
        mockBlock()
        activityTestRule.launchActivity(HomeActivity.getIntent(context))
    }

    @Test
    fun ifNoNoteFoundForUserWillShowTextToMentionIt() {
        val listEmitter = PublishSubject.create<List<NoteModel>>()
        launchActivity {
            provideMock(0, 0, listEmitter)
        }
        listEmitter.onNext(emptyList())
        onView(withId(R.id.tv_no_notes_found_message)).check(matches(isDisplayed()))
    }

    @Test
    fun ifNoteFoundForUserWillBeDisplayed() {
        val listEmitter = PublishSubject.create<List<NoteModel>>()
        launchActivity {
            provideMock(1, 1, listEmitter)
        }
        listEmitter.onNext(listOf(
                NoteModel(
                        userId = user.id,
                        password = "demo",
                        userName = "demo",
                        title = "demo"
                )
        ))

        onView(CoreMatchers.allOf(withId(R.id.tv_title), ViewMatchers.withText("demo"))).check(matches(isDisplayed()))
        onView(CoreMatchers.allOf(withId(R.id.tv_username), ViewMatchers.withText("demo"))).check(matches(isDisplayed()))
        onView(CoreMatchers.allOf(withId(R.id.tv_password), ViewMatchers.withText("demo"))).check(matches(isDisplayed()))
    }

    private fun provideMock(count: Int, pageSize: Int, listEmitter: PublishSubject<List<NoteModel>>) {
        on(userRepository.findNotesCountByUserId(safeEq(user)))
                .thenReturn(Single.just(count))
        on(userRepository.findNotesForUser(
                safeEq(user),
                safeEq(FetchNotesRequestModel(pageSize, 0))
        )).thenReturn(listEmitter)
    }
}