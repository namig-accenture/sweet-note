package data

import android.arch.persistence.room.Room
import android.database.sqlite.SQLiteConstraintException
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import data.persistance.AppDatabase
import data.persistance.login.UserDao
import data.persistance.login.UserEntity
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserTableTest {
    companion object {
        const val email = "namig@gmail.com"
        const val password = "1234"
    }

    private lateinit var userDao: UserDao
    private lateinit var database: AppDatabase

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getTargetContext()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        userDao = database.userDao()
    }

    @After
    fun tareDown() {
        database.close()
    }

    private inline fun createUser(block: UserEntity.() -> Unit = {}): UserEntity {
        return UserEntity(email = email, password = password).apply { block() }
    }

    private inline fun createAndInsert(block: UserEntity.() -> Unit = {}): UserEntity {
        return createUser(block).also {
            it.id = userDao.insert(it)
        }
    }

    @Test
    fun insertUserIntoDbSuccessful() {
        val user = createUser()
        val id = userDao.insert(user)
        val userFromDb = userDao.findUserByEmailAndPassword(email, password)
        assertThat("Could not insert user", id > 0 && id == userFromDb?.id)
    }

    @Test
    fun insertSameOrSameEmailedUserWillFail() {
        val user = createUser()
        userDao.insert(user)
        try {
            userDao.insert(user.apply { password = "Demo" })
            assert(false)
        } catch (ex: SQLiteConstraintException) {
            assertThat("Assertion Failed", ex.message?.contains("UNIQUE constraint") == true)
        }

        try {
            userDao.insert(user)
            assert(false)
        } catch (ex: SQLiteConstraintException) {
            assertThat("Assertion Failed", ex.message?.contains("UNIQUE constraint") == true)
        }
    }

    @Test
    fun selectUserByIdIsSucceed() {
        val user = createAndInsert()
        val userFromDb = userDao.findUserById(user.id!!)
        assertThat("Assertion Failed", user == userFromDb)
    }

    @Test
    fun selectUserByEmailPasswordIsSucceed() {
        val user = createAndInsert()
        val userFromDb = userDao.findUserByEmailAndPassword(user.email, user.password)
        assertThat("Assertion Failed", user == userFromDb)
    }

    @Test
    fun deleteUserFromDbSucceed() {
        val user = createAndInsert()
        val count = userDao.delete(user)
        assertThat("Assertion Failed", count == 1)
    }

    @Test
    fun updateUserSucceed() {
        val user = createAndInsert()
        val count = userDao.update(user.apply { email = "elif@gmail.com" })
        assertThat("Assertion Failed", count == 1)
    }
}