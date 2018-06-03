package app.views.searchdialog

import android.arch.lifecycle.MutableLiveData
import android.support.v7.recyclerview.extensions.ListAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import app.views.homeactivity.NotesViewHolder
import com.example.namigtahmazli.sweetnote.databinding.NoteListItemViewBinding
import domain.model.NoteModel

internal class SearchResultAdapter : ListAdapter<NoteModel, NotesViewHolder>(DiffUtil) {

    val itemClickObserver: MutableLiveData<NoteModel> by lazy { MutableLiveData<NoteModel>() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(NoteListItemViewBinding.inflate(LayoutInflater.from(parent.context),
                parent, false), itemClickObserver)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bindTo(it)
        }
    }

    object DiffUtil : android.support.v7.util.DiffUtil.ItemCallback<NoteModel>() {
        override fun areItemsTheSame(oldItem: NoteModel?, newItem: NoteModel?): Boolean {
            return oldItem?.id == newItem?.id
        }

        override fun areContentsTheSame(oldItem: NoteModel?, newItem: NoteModel?): Boolean {
            return oldItem == newItem
        }
    }
}