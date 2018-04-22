package data

import android.arch.persistence.room.Room
import data.executor.JobExecutor
import data.persistance.AppDatabase
import data.repositories.UserRepositoryImpl
import data.sharedpreference.PreferenceWrapper
import domain.executor.ThreadExecutor
import domain.repositories.UserRepository
import domain.services.PreferenceService
import org.koin.dsl.module.applicationContext

val general = applicationContext {
    bean { JobExecutor() as ThreadExecutor }
    bean { PreferenceWrapper(get()) as PreferenceService }
}

val database = applicationContext {
    bean { Room.databaseBuilder(get(), AppDatabase::class.java, getProperty("dbName")).build() }
    bean { get<AppDatabase>().userDao() }
}

val dataModule = listOf(general, database)