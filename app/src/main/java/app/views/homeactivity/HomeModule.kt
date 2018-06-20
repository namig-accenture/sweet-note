package app.views.homeactivity

import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.applicationContext

val homeActivityModule = applicationContext {
    viewModel { HomeViewModel(get(), get(), get(),get()) }
    factory { param -> HomePresenter(param[HomeActivity.ACTIVITY]) }
    factory { NotesPagedListAdapter() }
}