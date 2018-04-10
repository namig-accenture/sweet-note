package data.persistance

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import data.persistance.login.UserDao
import data.persistance.login.UserEntity

@Database(entities = [UserEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}