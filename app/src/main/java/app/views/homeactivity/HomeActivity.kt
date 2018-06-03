package app.views.homeactivity

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.Observer
import android.arch.paging.PagedList
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.MainThread
import android.support.v4.app.DialogFragment
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
        setSupportActionBar(dataBinding.toolbar)
        notesPagedListAdapter.itemClickObserver.observe(this, Observer(::handleListItemClick))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.apply {
            when (itemId) {
                R.id.menu_search -> {
                    openSearchDialog()
                    return true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openSearchDialog() {
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
    fun openAddEditDialog(view: View) {
        addDialogFragment(block = {
            AddEditNoteDialog().apply {
                setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_NoActionBar)
                arguments = Bundle().apply {
                    putInt(AddEditNoteDialog.REVEAL_START_X, view.run { right - (width) / 2 })
                    putInt(AddEditNoteDialog.REVEAL_START_Y, view.run { bottom - (height) / 2 })
                    putInt(AddEditNoteDialog.FAB_DIAMETHER, view.width)
                }
            }
        })
    }

    companion object {
        const val ACTIVITY = "Activity"
        fun getIntent(context: Context): Intent = Intent(context, HomeActivity::class.java)
    }
}