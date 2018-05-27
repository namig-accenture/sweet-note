package app.views.shownote

import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.applicationContext

val showNoteModule = applicationContext {
    viewModel { ShowNoteViewModel() }
    factory {param -> ShowNoteDialogPresenter(param[ShowNoteDialog.DIALOG]) }
}