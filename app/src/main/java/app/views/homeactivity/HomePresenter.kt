package app.views.homeactivity

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.Observer
import android.arch.lifecycle.OnLifecycleEvent
import android.support.design.widget.NavigationView
import android.support.v7.widget.Toolbar
import android.view.View
import app.ext.BasePresenter
import app.ext.log
import com.example.namigtahmazli.sweetnote.R
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy

internal class HomePresenter(private val homeActivity: HomeActivity) : BasePresenter() {
    private lateinit var disposables: CompositeDisposable

    private val viewModel by lazy { homeActivity.homeViewModel }

    val onMenuItemClickListener = Toolbar.OnMenuItemClickListener {
        when (it.itemId) {
            R.id.menu_search -> {
                homeActivity.openSearchDialog()
                true
            }
            else -> false
        }
    }

    val menuButtonClickListener: View.OnClickListener = View.OnClickListener {
        homeActivity.openDrawer()
    }

    val navigationItemSelectedListener = NavigationView.OnNavigationItemSelectedListener {
        when (it.itemId) {
            R.id.menu_log_out -> {
                disposables += observeLogginUserOut()
                true
            }
            else -> false
        }
    }

    private fun observeLogginUserOut(): Disposable {
        return viewModel.logUserOut().subscribeBy(
                onComplete = homeActivity::handleLoggingOut,
                onError = homeActivity::handleLoggingOutError
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun createDisposables() {
        disposables = CompositeDisposable()
        disposables += viewModel.getCurrentUser(homeActivity::handleGettingCurrentLoggedInUser)
        viewModel.userNotesObserver
                .observe(homeActivity, Observer(homeActivity::updateNoteList))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun initObservables() {
        disposables += viewModel.loadState.subscribeBy(
                onNext = homeActivity::handleLoadState,
                onError = { it.log<HomePresenter>("While observing load state") }
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun clearDisposables() {
        disposables.clear()
    }

    fun onAddButtonClicked(view: View) {
        homeActivity.openAddEditDialog()
    }
}