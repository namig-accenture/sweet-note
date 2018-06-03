package app.views.searchdialog

import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.applicationContext

val searchModule = applicationContext {
    factory { SearchResultAdapter() }
    viewModel { SearchViewModel(get()) }
    factory { param -> SearchPresenter(param[SearchDialog.DIALOG]) }
}