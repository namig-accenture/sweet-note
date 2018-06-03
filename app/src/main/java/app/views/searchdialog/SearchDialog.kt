package app.views.searchdialog

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import app.ext.BaseActivity
import app.ext.BaseDialogFragment
import app.ext.log
import app.extensions.plusAssign
import app.parcelables.ShowDialogArgumentModel
import app.views.shownote.ShowNoteDialog
import com.example.namigtahmazli.sweetnote.R
import com.example.namigtahmazli.sweetnote.databinding.DialogSearchBinding
import domain.model.NoteModel
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.android.inject

internal class SearchDialog : BaseDialogFragment<DialogSearchBinding>() {
    private val presenter by inject<SearchPresenter> { mapOf(DIALOG to this) }
    val searchViewModel by viewModel<SearchViewModel>()
    private val searchResultAdapter by inject<SearchResultAdapter>()

    override val layoutRes: Int
        get() = R.layout.dialog_search

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchResultAdapter.itemClickObserver.observe(this, Observer(::handleListItemClick))
    }

    private fun handleListItemClick(note: NoteModel?) {
        (activity as? BaseActivity<*>)?.addDialogFragment(block = {
            ShowNoteDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(ShowNoteDialog.ARGUMENT, ShowDialogArgumentModel(note))
                }
            }
        })
    }

    override fun bindVariables(dataBinding: DialogSearchBinding) {
        dataBinding.recycleViewResults.adapter = searchResultAdapter
        dataBinding.presenter = presenter
        dataBinding.viewModel = searchViewModel
    }

    override fun addLifecycleObservers(lifecycle: Lifecycle) {
        lifecycle += presenter
    }

    fun handleQueryResults(notes: List<NoteModel>) {
        searchResultAdapter.submitList(notes)
    }

    fun handleQueringError(throwable: Throwable) {
        throwable.log<SearchPresenter>("while observing search query results.")
        showMessage(messageId = R.string.search_dialog_search_error_message)
    }

    fun onBackNavigationClicked(@Suppress("UNUSED_PARAMETER") view: View?) {
        this.dismiss()
    }

    companion object {
        const val DIALOG = "SearchDialog"
        fun getInstance(): SearchDialog {
            return SearchDialog().apply {
                setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_NoActionBar_White)
            }
        }
    }
}