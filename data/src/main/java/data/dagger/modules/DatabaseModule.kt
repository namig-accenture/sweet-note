package data.dagger.modules

import android.arch.persistence.room.Room
import android.content.Context
import dagger.Module
import dagger.Provides
import data.dagger.qualifiers.DatabaseName
import data.dagger.scopes.AppScope
import data.persistance.AppDatabase
import data.persistance.login.UserDao

@Module
class DatabaseModule {

    @AppScope
    @Provides
    fun provideAppDatabase(context: Context, @DatabaseName name: String): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, name).build()
    }

    @AppScope
    @Provides
    fun provideUserDao(appDatabase: AppDatabase): UserDao = appDatabase.userDao()
}