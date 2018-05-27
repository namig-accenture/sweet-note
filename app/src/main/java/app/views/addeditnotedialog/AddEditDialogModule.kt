package app.views.addeditnotedialog

import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.applicationContext

val addEditDialogModule = applicationContext {
    viewModel { AddEditNoteViewModel(get()) }
    factory { param -> AddEditNoteDialogPresenter(param[AddEditNoteDialog.DIALOG]) }
}