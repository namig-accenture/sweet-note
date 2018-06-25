package app.views.homeactivity

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.Observer
import android.arch.paging.PagedList
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.MainThread
import app.ext.BaseActivity
import app.ext.log
import app.extensions.plusAssign
import app.parcelables.ShowDialogArgumentModel
import app.views.addeditnotedialog.AddEditNoteDialog
import app.views.launchactivity.LaunchActivity
import app.views.searchdialog.SearchDialog
import app.views.shownote.ShowNoteDialog
import com.example.namigtahmazli.sweetnote.R
import com.example.namigtahmazli.sweetnote.databinding.ActivityHomeBinding
import com.example.namigtahmazli.sweetnote.databinding.ActivityMainNavigationViewHeaderBinding
import domain.model.NoteModel
import domain.model.UserModel
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.android.inject
import timber.log.Timber

internal class HomeActivity : BaseActivity<ActivityHomeBinding>() {
    val homeViewModel by viewModel<HomeViewModel>()
    private val homePresenter by inject<HomePresenter> { mapOf(ACTIVITY to this) }
    private val notesPagedListAdapter by inject<NotesPagedListAdapter>()
    private lateinit var navigationBinding: ActivityMainNavigationViewHeaderBinding

    override val dataBinding: ActivityHomeBinding
        get() = provideDataBinding(R.layout.activity_home).apply {
            homeContent.notesList?.let { it.adapter = notesPagedListAdapter }
            this.viewModel = homeViewModel
            this.presenter = homePresenter
            navigationMenu.apply {
                navigationBinding = ActivityMainNavigationViewHeaderBinding.bind(getHeaderView(0))
            }
            this.setLifecycleOwner(this@HomeActivity)
        }

    override fun addLifecycleObservers(lifecycle: Lifecycle) {
        lifecycle += homePresenter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notesPagedListAdapter.itemClickObserver.observe(this, Observer(::handleListItemClick))
        homeViewModel.currentUser.observe(this, Observer(::handleCurrentUser))
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

    private fun handleCurrentUser(user: UserModel?) {
        navigationBinding.user = user
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

    fun handleGettingCurrentLoggedInUser(throwable: Throwable) {
        throwable.log<HomePresenter>("while getting current logged in user.")
        showMessage(message = "Could not get current logged in user")
    }

    fun updateNoteList(notes: PagedList<NoteModel>?) {
        notesPagedListAdapter.submitList(notes)
    }

    @MainThread
    fun openAddEditDialog() {
        addDialogFragment(block = { AddEditNoteDialog.instance })
    }

    fun handleLoggingOut() {
        startActivity(LaunchActivity.getIntent(this))
        finish()
    }

    fun handleLoggingOutError(throwable: Throwable) {
        throwable.log<HomePresenter>("while logging user out")
        showMessage(message = "Could not log user out.")
    }

    companion object {
        const val ACTIVITY = "Activity"
        fun getIntent(context: Context): Intent = Intent(context, HomeActivity::class.java)
    }
}