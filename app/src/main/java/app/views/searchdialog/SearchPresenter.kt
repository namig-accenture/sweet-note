package app.views.searchdialog

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import android.support.v7.widget.SearchView
import android.view.View
import app.ext.BasePresenter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign

internal class SearchPresenter(private val dialog: SearchDialog) : BasePresenter() {
    private lateinit var disposables: CompositeDisposable
    private val viewModel by lazy { dialog.searchViewModel }

    val onQueryTextChanged = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            query?.let { viewModel.queryTextObserver.onNext(it) }
            return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            newText?.let { viewModel.queryTextObserver.onNext(it) }
            return false
        }
    }

    val backNavigationClick = View.OnClickListener {
        dialog.onBackNavigationClicked(it)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun createDisposables() {
        disposables = CompositeDisposable()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun initDisposables() {
        disposables += viewModel.observeQueryResults(
                onNext = dialog::handleQueryResults,
                onError = dialog::handleQueringError
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun clearDisposables() {
        disposables.clear()
    }
}