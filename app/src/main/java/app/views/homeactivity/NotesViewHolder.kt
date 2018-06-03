package app.views.homeactivity

import android.arch.lifecycle.MutableLiveData
import android.support.v7.widget.RecyclerView
import com.example.namigtahmazli.sweetnote.databinding.NoteListItemViewBinding
import domain.model.NoteModel

class NotesViewHolder(private val dataBinding: NoteListItemViewBinding,
                      private val itemClickObserver: MutableLiveData<NoteModel>) : RecyclerView.ViewHolder(dataBinding.root) {
    fun bindTo(noteModel: NoteModel) {
        dataBinding.note = noteModel
        dataBinding.viewHolder = this
    }

    fun onItemClicked(noteModel: NoteModel) {
        itemClickObserver.value = noteModel
    }
}