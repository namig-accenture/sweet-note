package data.persistance

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import data.persistance.login.UserDao
import data.persistance.login.UserEntity
import data.persistance.note.NoteDao
import data.persistance.note.NoteEntity

@Database(entities = [
    UserEntity::class,
    NoteEntity::class
], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun noteDao(): NoteDao

    companion object {
        fun create(context: Context, inMemory: Boolean = false, name: String): AppDatabase {
            val database = if (inMemory) {
                Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).allowMainThreadQueries()
            } else {
                Room.databaseBuilder(context, AppDatabase::class.java, name)
            }

            return database.fallbackToDestructiveMigration().build()
        }
    }
}