package app.views.shownote

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import android.support.v7.widget.Toolbar
import app.customview.DrawableClickableTextView
import app.ext.BasePresenter
import com.example.namigtahmazli.sweetnote.R
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy

internal class ShowNoteDialogPresenter(private val dialog: ShowNoteDialog) : BasePresenter() {
    private val disposables by lazy { CompositeDisposable() }
    private val viewModel by lazy { dialog.showNoteViewModel }
    val onUsernameDrawableClicked: (DrawableClickableTextView.DrawablePosition) -> Unit = dialog::handleUsernameDrawableClick
    val onPasswordDrawableClicked: (DrawableClickableTextView.DrawablePosition) -> Unit = dialog::handlePasswordDrawableClick
    val onMenuItemClickListener by lazy {
        Toolbar.OnMenuItemClickListener {
            return@OnMenuItemClickListener when (it.itemId) {
                R.id.menu_edit -> {
                    dialog.handleEdit()
                    true
                }
                R.id.menu_delete -> {
                    disposables += observeDeletingNote()
                    true
                }
                else -> false
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun clearDisposables() {
        disposables.clear()
    }

    private fun observeDeletingNote(): Disposable {
        return viewModel.observeDelete()
                .subscribeBy(
                        onSuccess = dialog::handleDeletingNote,
                        onError = dialog::handleDeletingNoteError
                )
    }
}