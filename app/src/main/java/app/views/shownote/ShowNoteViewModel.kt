package app.views.shownote

import android.arch.lifecycle.MutableLiveData
import app.ext.BaseViewModel
import domain.model.NoteModel

internal class ShowNoteViewModel : BaseViewModel() {
    val note = MutableLiveData<NoteModel>()
}