package data.persistance

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import data.persistance.login.UserDao
import data.persistance.login.UserEntity
import data.persistance.note.NoteDao
import data.persistance.note.NoteEntity
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.TestScheduler
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

    @JvmField
    @Rule
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
                .test()
                .assertValue { it == note.copy(id = it.id) }
    }

    @Test
    fun selectNotesPageByPageSucceed() {
        val userId = insertUser()
        (1..90).forEach {
            noteDao.insert(NoteEntity(
                    userId = userId,
                    userName = "namig$it",
                    password = "123$it",
                    title = "$it"
            ))
        }

        noteDao.findNotesByUserId(userId, 20, 0)
                .test()
                .assertValue { list -> list.size == 20 && list.first().id == 90L && list.last().id == 71L }
    }

    @Test
    fun selectNotesPageByPageFromIdSucceed() {
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
                .test()
                .assertValue { list -> list.size == 20 && list.first().id == 20L && list.last().id == 1L }
    }

    @Test
    fun ifNoNoteFoundForIdWillReturnNothing() {
        noteDao.findNoteById(1)
                .test()
                .assertComplete()
                .assertNoErrors()
                .assertNoValues()

    }

    @Test
    fun ifNoNoteFoundForUserNotingWillBeReturned() {
        noteDao.findNotesByUserId(1, 20, 0)
                .test()
                .assertNotComplete()
                .assertValue { it.isEmpty() }
                .assertNoErrors()
    }

    @Test
    fun insertingNewLineWillBeObserved() {
        val userId = insertUser()
        val note = NoteEntity(
                userId = userId,
                userName = "namig",
                password = "123",
                title = "Demo"
        )
        noteDao.insert(note)
        noteDao.findNotesByUserId(userId, 20, 0)
                .test()
                .assertValue { it.isNotEmpty() && it.size == 1 && it.first() == note.copy(id = 1) }
    }

    @Test
    fun queryNotesByTitleSucceed() {
        val userId = insertUser()
        (0..5).forEach {
            val note = NoteEntity(
                    userId = userId,
                    userName = "username$it",
                    password = "password$it",
                    title = "${it}Title$it"
            )
            noteDao.insert(note)
        }

        noteDao.findNotesByTitle(userId, "%title%")
                .test()
                .assertValue { it.size == 6 }

        noteDao.findNotesByTitle(userId, "%title1%")
                .test()
                .assertValue { it.size == 1 && it.first().id == 2L }

        noteDao.findNotesByTitle(userId,"a")
                .test()
                .assertValue { it.isEmpty() }
    }
}