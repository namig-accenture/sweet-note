package app.views.shownote

import app.customview.DrawableClickableTextView
import app.ext.BasePresenter

internal class ShowNoteDialogPresenter(dialog: ShowNoteDialog) :BasePresenter(){
    val onUsernameDrawableClicked: (DrawableClickableTextView.DrawablePosition) -> Unit = dialog::handleUsernameDrawableClick
    val onPasswordDrawableClicked: (DrawableClickableTextView.DrawablePosition) -> Unit = dialog::handlePasswordDrawableClick
}