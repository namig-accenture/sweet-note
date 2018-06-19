package app.koin

import android.content.Context
import app.executor.UIThread
import app.ext.BaseActivity
import app.ext.LogoutObserver
import app.views.addeditnotedialog.addEditDialogModule
import app.views.homeactivity.homeActivityModule
import app.views.launchactivity.launchActivityModule
import app.views.loginfragment.loginFragmentModule
import app.views.pinactivity.pinActivityModule
import app.views.registerfragment.registerFragmentModule
import app.views.searchdialog.searchModule
import app.views.shownote.showNoteModule
import data.repositories.NoteRepositoryImpl
import data.repositories.UserRepositoryImpl
import domain.executor.PostExecutionThread
import domain.repositories.NoteRepository
import domain.repositories.UserRepository
import org.koin.dsl.module.applicationContext

open class AppModule {
    private val general = applicationContext {
        bean { UIThread() as PostExecutionThread }
        bean { get<Context>().getSharedPreferences(getProperty("prefName"), Context.MODE_PRIVATE) }
        factory { param -> LogoutObserver(param[BaseActivity.EACH_ACTIVITY]) }
    }
    private val view = arrayOf(
            launchActivityModule,
            loginFragmentModule,
            registerFragmentModule,
            pinActivityModule,
            homeActivityModule,
            showNoteModule,
            addEditDialogModule,
            searchModule
    )

    open val repository = applicationContext {
        bean { UserRepositoryImpl(get(), get(), get()) as UserRepository }
        bean { NoteRepositoryImpl(get()) as NoteRepository }
    }

    val appModule get() = listOf(general, *view, repository)
}


