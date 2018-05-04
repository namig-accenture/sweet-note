package data

import android.arch.persistence.room.Room
import com.squareup.moshi.Moshi
import data.executor.JobExecutor
import data.persistance.AppDatabase
import data.sharedpreference.PreferenceWrapper
import data.transformer.DebugTransformerImpl
import domain.executor.ThreadExecutor
import domain.extensions.asOptional
import domain.services.PreferenceService
import domain.transformers.DebugTransformer
import org.koin.dsl.module.applicationContext

val general = applicationContext {
    bean { JobExecutor() as ThreadExecutor }
    bean { Moshi.Builder().build() as Moshi }
    bean { PreferenceWrapper(get(), get()) as PreferenceService }
    factory {
        val debugTransformer: DebugTransformer? = getProperty<Boolean>("Debug").let {
            if (it) {
                DebugTransformerImpl()
            } else {
                null
            }
        }
        return@factory debugTransformer.asOptional
    }
}

val database = applicationContext {
    bean { Room.databaseBuilder(get(), AppDatabase::class.java, getProperty("dbName")).build() }
    bean { get<AppDatabase>().userDao() }
}

val dataModule = listOf(general, database)