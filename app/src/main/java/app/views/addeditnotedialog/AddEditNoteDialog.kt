package app.views.addeditnotedialog

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.annotation.MainThread
import android.support.v4.app.DialogFragment
import app.ext.BaseDialogFragment
import app.ext.log
import app.extensions.plusAssign
import app.parcelables.AddEditDialogArgumentModel
import com.example.namigtahmazli.sweetnote.R
import com.example.namigtahmazli.sweetnote.databinding.DialogAddEditNoteBinding
import domain.model.NoteModel
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.android.inject

internal class AddEditNoteDialog : BaseDialogFragment<DialogAddEditNoteBinding>() {

    val dialogViewModel by viewModel<AddEditNoteViewModel>()
    private val addEditNoteDialogPresenter by inject<AddEditNoteDialogPresenter> { mapOf(DIALOG to this) }

    override val layoutRes: Int
        get() = R.layout.dialog_add_edit_note

    override fun bindVariables(dataBinding: DialogAddEditNoteBinding) {
        dataBinding.presenter = addEditNoteDialogPresenter
        dataBinding.viewModel = dialogViewModel
        dataBinding.setLifecycleOwner(this)
    }

    override fun addLifecycleObservers(lifecycle: Lifecycle) {
        lifecycle += addEditNoteDialogPresenter
    }

    override fun onBackPressed() {
        addEditNoteDialogPresenter.observeIfHasChanges()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        dialogViewModel.note.observe(this, Observer {
            dialogViewModel.title.postValue(it?.title)
            dialogViewModel.username.postValue(it?.userName)
            dialogViewModel.password.postValue(it?.password)
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getParcelable<AddEditDialogArgumentModel>(NOTE)?.let {
            dialogViewModel.note.value = it.noteModel
        }
    }

    @MainThread
    fun handleBackButtonClick() {
        dismiss()
    }

    fun handleTitleTextChanges(isNotDefined: Boolean) {
        dialogViewModel.titleError.value = if (isNotDefined) {
            getString(R.string.add_edit_note_dialog_title_not_defined_error)
        } else {
            null
        }
    }

    fun handleUsernameTextChanges(isNotDefined: Boolean) {
        dialogViewModel.usernameError.value = if (isNotDefined) {
            getString(R.string.add_edit_note_dialog_username_not_defined_error)
        } else {
            null
        }
    }

    fun handlePasswordTextChanges(isNotDefined: Boolean) {
        dialogViewModel.passwordError.value = if (isNotDefined) {
            getString(R.string.add_edit_note_dialog_password_not_defined_error)
        } else {
            null
        }
    }

    fun handleAddingNoteResult(note: NoteModel) {
        note.id?.let {
            this.dismiss()
        } ?: showMessage(message = "Could not add note.")
    }

    fun handleEditingNoteResult(updatedRowCount: Int) {
        if (updatedRowCount == 1) {
            this.dismiss()
        } else {
            showMessage(message = "Could not update note.")
        }
    }

    fun handleAddingNoteError(throwable: Throwable) {
        throwable.log<AddEditNoteDialogPresenter>("While adding note.")
        showMessage(message = "Failed")
    }

    fun handleEditingNoteError(throwable: Throwable) {
        throwable.log<AddEditNoteDialogPresenter>("While adding note.")
        showMessage(message = "Failed")
    }

    companion object {
        const val DIALOG = "AddEditNoteDialog"
        const val NOTE = "note"

        val instance
            get() = AddEditNoteDialog().apply {
                setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_NoActionBar_Dialog)
            }
    }
}