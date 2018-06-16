package app.views.homeactivity

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.Observer
import android.arch.paging.PagedList
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.MainThread
import app.ext.BaseActivity
import app.extensions.plusAssign
import app.parcelables.ShowDialogArgumentModel
import app.views.addeditnotedialog.AddEditNoteDialog
import app.views.searchdialog.SearchDialog
import app.views.shownote.ShowNoteDialog
import com.example.namigtahmazli.sweetnote.R
import com.example.namigtahmazli.sweetnote.databinding.ActivityHomeBinding
import domain.model.NoteModel
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.android.inject
import timber.log.Timber

internal class HomeActivity : BaseActivity<ActivityHomeBinding>() {
    val homeViewModel by viewModel<HomeViewModel>()
    private val homePresenter by inject<HomePresenter> { mapOf(ACTIVITY to this) }
    private val notesPagedListAdapter by inject<NotesPagedListAdapter>()

    override val dataBinding: ActivityHomeBinding
        get() = provideDataBinding(R.layout.activity_home).apply {
            homeContent?.notesList?.let { it.adapter = notesPagedListAdapter }
            this.viewModel = homeViewModel
            this.presenter = homePresenter
            this.setLifecycleOwner(this@HomeActivity)
        }

    override fun addLifecycleObservers(lifecycle: Lifecycle) {
        lifecycle += homePresenter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notesPagedListAdapter.itemClickObserver.observe(this, Observer(::handleListItemClick))
    }

    fun openSearchDialog() {
        addDialogFragment(block = {
            SearchDialog.getInstance()
        })
    }

    private fun handleListItemClick(note: NoteModel?) {
        addDialogFragment(block = {
            ShowNoteDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(ShowNoteDialog.ARGUMENT, ShowDialogArgumentModel(note))
                }
            }
        })
    }

    fun handleLoadState(loadState: NotesDataSource.LoadState?) {
        when (loadState) {
            NotesDataSource.LoadState.Error -> {
                showMessage(message = "Could not load notes.")
                homeViewModel.refreshing.postValue(false)
            }
            NotesDataSource.LoadState.Loading -> homeViewModel.refreshing.postValue(true)
            NotesDataSource.LoadState.Loaded -> {
                homeViewModel.refreshing.postValue(false)
                homeViewModel.listIsEmpty.postValue(false)
            }
            NotesDataSource.LoadState.Empty -> {
                Timber.d("No notes found")
                homeViewModel.listIsEmpty.postValue(true)
                homeViewModel.refreshing.postValue(false)
            }
        }
    }

    fun updateNoteList(notes: PagedList<NoteModel>?) {
        notesPagedListAdapter.submitList(notes)
    }

    @MainThread
    fun openAddEditDialog() {
        addDialogFragment(block = { AddEditNoteDialog.instance })
    }

    companion object {
        const val ACTIVITY = "Activity"
        fun getIntent(context: Context): Intent = Intent(context, HomeActivity::class.java)
    }
}