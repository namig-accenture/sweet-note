package data

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import data.persistance.AppDatabase
import data.persistance.login.UserDao
import data.persistance.login.UserEntity
import data.persistance.note.NoteDao
import data.persistance.note.NoteEntity
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subscribers.TestSubscriber
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NotesTableTest {

    private lateinit var noteDao: NoteDao
    private lateinit var userDao: UserDao
    private lateinit var testScheduler: TestScheduler
    private lateinit var testObserver: TestObserver<NoteEntity>

    @get:Rule
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        val database = AppDatabase.create(InstrumentationRegistry.getTargetContext(), inMemory = true, name = "")
        noteDao = database.noteDao()
        userDao = database.userDao()
        testScheduler = TestScheduler()
        testObserver = TestObserver()
    }

    private fun insertUser(): Long {
        return userDao.insert(UserEntity(
                email = "a@b.com",
                password = "123abc"
        ))
    }

    @Test
    fun insertNoteWillSucceed() {
        val note = NoteEntity(
                userId = insertUser(),
                userName = "namiq",
                password = "abcd",
                title = "Gmail"
        )
        val id = noteDao.insert(note)
        noteDao.findNoteById(noteId = id)
                .subscribeOn(testScheduler)
                .subscribe(testObserver)
        testScheduler.triggerActions()
        testObserver.assertValue {
            it == note.copy(id = it.id)
        }
    }

    @Test
    fun selectNotesPageByPageSucceed() {
        val testSubscriber: TestSubscriber<List<NoteEntity>> = object : TestSubscriber<List<NoteEntity>>() {
            override fun onNext(list: List<NoteEntity>?) {
                assertTrue(list != null && list.size == 20 && list.first().id == 90L && list.last().id == 90 - 19L)
            }
        }
        val userId = insertUser()
        (1..90).forEach {
            noteDao.insert(NoteEntity(
                    userId = userId,
                    userName = "namig$it",
                    password = "123$it",
                    title = "$it"
            ))
        }

        noteDao.findNotesByUserId(userId, 20,0)
                .subscribeOn(testScheduler)
                .subscribe(testSubscriber)
        testScheduler.triggerActions()
    }

    @Test
    fun selectNotesPageByPageFromIdSucceed() {
        val testSubscriber: TestSubscriber<List<NoteEntity>> = object : TestSubscriber<List<NoteEntity>>() {
            override fun onNext(list: List<NoteEntity>?) {
                assertTrue(list != null && list.size == 20 && list.first().id == 69L && list.last().id == 69 - 19L)
            }
        }
        val userId = insertUser()
        (1..90).forEach {
            noteDao.insert(NoteEntity(
                    userId = userId,
                    userName = "namig$it",
                    password = "123$it",
                    title = "$it"
            ))
        }

        noteDao.findNotesByUserId(userId, 20, 70)
                .subscribeOn(testScheduler)
                .subscribe(testSubscriber)
        testScheduler.triggerActions()
    }

    @Test
    fun ifNoNoteFoundForIdWillReturnNothing() {
        val testObserver = TestObserver<NoteEntity>()
        noteDao.findNoteById(1)
                .subscribeOn(testScheduler)
                .subscribe(testObserver)
        testScheduler.triggerActions()
        testObserver.assertComplete()
        testObserver.assertNoErrors()
        testObserver.assertValueCount(0)
    }

    @Test
    fun ifNoNoteFoundForUserNotingWillBeReturned() {
        val testSubscriber = object : TestSubscriber<List<NoteEntity>>() {
            override fun onComplete() {
                assertTrue(true)
            }

            override fun onNext(t: List<NoteEntity>?) {
                assertTrue(t != null && t.isEmpty())
            }

            override fun onError(t: Throwable?) {
                assertTrue(false)
            }
        }
        noteDao.findNotesByUserId(1, 20,0)
                .subscribeOn(testScheduler)
                .subscribe(testSubscriber)
        testScheduler.triggerActions()
    }

    @Test
    fun insertingNewLineWillBeObserver() {
        val userId = insertUser()
        val note = NoteEntity(
                userId = userId,
                userName = "namig",
                password = "123",
                title = "Demo"
        )

        val testSubscriber = object : TestSubscriber<List<NoteEntity>>() {
            override fun onNext(t: List<NoteEntity>?) {
                assertTrue(t != null && t.isNotEmpty() && t.size == 1 && t.first() == note)
            }
        }

        noteDao.findNotesByUserId(userId, 20,0)
                .subscribeOn(testScheduler)
                .subscribe(testSubscriber)
        noteDao.insert(note)
        testScheduler.triggerActions()
    }
}