package app.views.addeditnotedialog

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import android.support.v7.app.AlertDialog
import android.view.View
import app.ext.BasePresenter
import app.ext.log
import com.example.namigtahmazli.sweetnote.R
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy

@Suppress("UNUSED_PARAMETER")
internal class AddEditNoteDialogPresenter(private val dialog: AddEditNoteDialog) : BasePresenter() {
    val backNavigationClick: View.OnClickListener = View.OnClickListener {
        observeIfHasChanges()
    }

    private lateinit var disposables: CompositeDisposable

    private val viewModel by lazy { dialog.dialogViewModel }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun createDisposables() {
        disposables = CompositeDisposable()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun initDisposables() {
        disposables += viewModel.observeTitleTextChanges(
                onNext = dialog::handleTitleTextChanges,
                onError = { it.log<AddEditNoteDialogPresenter>("While observing title text changes.") }
        )
        disposables += viewModel.observeUsernameTextChanges(
                onNext = dialog::handleUsernameTextChanges,
                onError = { it.log<AddEditNoteDialogPresenter>("While observing username text changes.") }
        )
        disposables += viewModel.observePasswordTextChanges(
                onNext = dialog::handlePasswordTextChanges,
                onError = { it.log<AddEditNoteDialogPresenter>("While observing password text changes.") }
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun clearDisposables() {
        disposables.clear()
    }

    fun onSaveButtonClicked(view: View) {
        disposables += viewModel.run {
            return@run note.value?.let {
                editNote().subscribeBy(
                        onSuccess = dialog::handleEditingNoteResult,
                        onError = dialog::handleEditingNoteError
                )
            } ?: addNote().subscribeBy(
                    onSuccess = dialog::handleAddingNoteResult,
                    onError = dialog::handleAddingNoteError
            )
        }
    }

    fun onTitleTextChanged(title: CharSequence?, start: Int, before: Int, count: Int) {
        title?.let { viewModel.titleTextObserver.onNext(it) }
    }

    fun onUsernameTextChanged(username: CharSequence?, start: Int, before: Int, count: Int) {
        username?.let { viewModel.usernameTextObserver.onNext(it) }
    }

    fun onPasswordTextChanged(password: CharSequence?, start: Int, before: Int, count: Int) {
        password?.let { viewModel.passwordTextObserver.onNext(it) }
    }

    fun observeIfHasChanges() {
        disposables += viewModel.doesHaveChanges()
                .subscribeBy(
                        onSuccess = { doHaveChanges ->
                            if (doHaveChanges) {
                                disposables += observeDiscardChangesWarningDialogAction()
                            } else {
                                dialog.handleBackButtonClick()
                            }
                        },
                        onError = { it.log<AddEditNoteDialogPresenter>("while observing changes.") }
                )
    }

    private fun observeDiscardChangesWarningDialogAction(): Disposable {
        return showDiscardChangesWarningDialog()
                .subscribeBy(
                        onSuccess = { doDiscard ->
                            if (doDiscard) {
                                dialog.handleBackButtonClick()
                            }
                        },
                        onError = { it.log<AddEditNoteDialogPresenter>("while observing discard changes warning dialog actions.") }
                )
    }

    private fun showDiscardChangesWarningDialog(): Single<Boolean> {
        return Single.create { emitter ->
            dialog.context?.let {
                AlertDialog.Builder(it)
                        .setTitle(R.string.add_edit_not_dialog_discard_changes_warning_dialog_title)
                        .setMessage(R.string.add_edit_not_dialog_discard_changes_warning_dialog_message)
                        .setPositiveButton(R.string.add_edit_not_dialog_discard_changes_warning_dialog_positive_action) { _, _ -> emitter.onSuccess(true) }
                        .setNegativeButton(R.string.add_edit_not_dialog_discard_changes_warning_dialog_negative_action) { _, _ -> emitter.onSuccess(false) }
                        .show()
            }
        }
    }
}