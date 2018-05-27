package data

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
    bean { AppDatabase.create(get(), name = getProperty("dbName")) }
    bean { get<AppDatabase>().userDao() }
    bean { get<AppDatabase>().noteDao() }
}

val dataModule = listOf(general, database)