package app.views.shownote

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.MenuRes
import app.ext.BaseViewModel
import com.example.namigtahmazli.sweetnote.R
import domain.exceptions.NoteNotFoundException
import domain.model.NoteModel
import domain.usecase.note.DeleteNoteUseCase
import io.reactivex.Single

internal class ShowNoteViewModel(private val deleteNoteUseCase: DeleteNoteUseCase) : BaseViewModel() {
    val note = MutableLiveData<NoteModel>()
    @MenuRes
    val menuRes = R.menu.menu_show_note_dialog

    fun observeDelete(): Single<Int> {
        return note.value?.let {
            deleteNoteUseCase.get(it)
        } ?: Single.error(NoteNotFoundException())
    }
}