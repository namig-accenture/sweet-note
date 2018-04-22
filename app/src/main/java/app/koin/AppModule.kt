package app.koin

import android.content.Context
import app.executor.UIThread
import app.views.launchactivity.launchActivityModule
import app.views.loginfragment.loginFragmentModule
import app.views.registerfragment.registerFragmentModule
import data.repositories.UserRepositoryImpl
import domain.executor.PostExecutionThread
import domain.repositories.UserRepository
import org.koin.dsl.module.applicationContext

open class AppModule {
    private val general = applicationContext {
        bean { UIThread() as PostExecutionThread }
        bean { get<Context>().getSharedPreferences(getProperty("prefName"), Context.MODE_PRIVATE) }
    }
    private val view = arrayOf(
            launchActivityModule,
            loginFragmentModule,
            registerFragmentModule
    )

    open val repository = applicationContext {
        bean { UserRepositoryImpl(get(), get()) as UserRepository }
    }

    val appModule get() = listOf(general, *view, repository)
}


