package data.repository

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.support.test.InstrumentationRegistry
import data.persistance.AppDatabase
import data.persistance.login.UserDao
import data.persistance.login.UserEntity
import data.persistance.note.NoteDao
import data.persistance.note.NoteEntity
import data.repositories.NoteRepositoryImpl
import domain.model.NoteModel
import domain.model.UserModel
import domain.repositories.NoteRepository
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest

class NoteRepositoryTest : KoinTest {
    private lateinit var noteRepository: NoteRepository
    private lateinit var noteDao: NoteDao
    private lateinit var userDao: UserDao

    @JvmField
    @Rule
    val instantTaskExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        val database = AppDatabase.create(InstrumentationRegistry.getTargetContext(),
                inMemory = true, name = "")
        noteDao = database.noteDao()
        userDao = database.userDao()
        noteRepository = NoteRepositoryImpl(noteDao)
    }

    @Test
    fun addingNoteWillSucceed() {
        val note = NoteModel(
                userId = addUser(),
                password = "demo",
                userName = "demo",
                title = "demo")
        noteRepository.addNote(note)
                .test()
                .assertValue { it == note.copy(id = it.id) }
    }

    private fun addUser(): Long {
        return userDao.insert(
                UserEntity(
                        email = "demo@demo.com",
                        password = "demo"
                )
        )
    }

    @Test
    fun getNotesByTitleWillSucceed() {
        val userId = addUser()
        (0..5).forEach {
            val note = NoteEntity(
                    userId = userId,
                    userName = "username$it",
                    password = "password$it",
                    title = "${it}Title$it"
            )
            noteDao.insert(note)
        }

        val user = UserModel(id = userId, email = "demo@demo.com", password = "demo")

        noteRepository.findNotesByTitle(user, "title")
                .test()
                .assertValue { it.size == 6 }

        noteRepository.findNotesByTitle(user, "title1")
                .test()
                .assertValue { it.size == 1 && it.first().id == 2L }

        noteRepository.findNotesByTitle(user, "a")
                .test()
                .assertValue { it.isEmpty() }
    }
}