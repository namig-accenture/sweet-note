package app.views.homeactivity

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.namigtahmazli.sweetnote.databinding.NoteListItemViewBinding
import domain.model.NoteModel

internal class NotesPagedListAdapter : PagedListAdapter<NoteModel, NotesPagedListAdapter.ViewHolder>(object : DiffUtil.ItemCallback<NoteModel>() {
    override fun areItemsTheSame(oldItem: NoteModel, newItem: NoteModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: NoteModel, newItem: NoteModel): Boolean {
        return oldItem == newItem
    }

}) {

    val itemClickObserver: MutableLiveData<NoteModel> by lazy { MutableLiveData<NoteModel>() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesPagedListAdapter.ViewHolder {
        val noteListItemViewBinding = NoteListItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(noteListItemViewBinding)
    }

    override fun onBindViewHolder(holder: NotesPagedListAdapter.ViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bindTo(it)
        }
    }

    inner class ViewHolder(private val noteListItemViewBinding: NoteListItemViewBinding) : RecyclerView.ViewHolder(noteListItemViewBinding.root) {
        fun bindTo(noteModel: NoteModel) {
            noteListItemViewBinding.note = noteModel
            noteListItemViewBinding.viewHolder = this
        }

        fun onItemClicked(noteModel: NoteModel) {
            itemClickObserver.value = noteModel
        }
    }
}