package app.koin

import app.executor.UIThread
import app.views.launchactivity.launchActivityModule
import app.views.loginfragment.loginFragmentModule
import app.views.registerfragment.registerFragmentModule
import domain.executor.PostExecutionThread
import org.koin.dsl.module.applicationContext

val general = applicationContext {
    bean { UIThread() as PostExecutionThread }
}
val view = arrayOf(
        launchActivityModule,
        loginFragmentModule,
        registerFragmentModule
)
val appModule = listOf(general, *view)

