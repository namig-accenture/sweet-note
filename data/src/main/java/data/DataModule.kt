package data

import android.arch.persistence.room.Room
import data.executor.JobExecutor
import data.persistance.AppDatabase
import data.repositories.UserRepositoryImpl
import domain.executor.ThreadExecutor
import domain.repositories.UserRepository
import org.koin.dsl.module.applicationContext

val general = applicationContext {
    bean { JobExecutor() as ThreadExecutor }
}

val database = applicationContext {
    bean { Room.databaseBuilder(get(), AppDatabase::class.java, getProperty("dbName")).build() }
    bean { get<AppDatabase>().userDao() }
}

val repository = applicationContext {
    bean { UserRepositoryImpl(get()) as UserRepository }
}

val dataModule = listOf(general, database, repository)