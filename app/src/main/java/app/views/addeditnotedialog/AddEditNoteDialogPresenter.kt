package app.views.addeditnotedialog

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import android.view.View
import app.ext.BasePresenter
import app.ext.log
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy

@Suppress("UNUSED_PARAMETER")
internal class AddEditNoteDialogPresenter(private val dialog: AddEditNoteDialog) : BasePresenter() {
    val backNavigationClick: View.OnClickListener = View.OnClickListener {
        dialog.handleBackButtonClick(it)
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
        disposables += viewModel.addNote()
                .subscribeBy(
                        onSuccess = { dialog.handleAddingNoteResult() },
                        onError = dialog::handleAddingNoteError
                )
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
}