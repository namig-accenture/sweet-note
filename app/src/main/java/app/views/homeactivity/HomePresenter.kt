package app.views.homeactivity

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.Observer
import android.arch.lifecycle.OnLifecycleEvent
import android.support.design.widget.NavigationView
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.view.View
import app.ext.BasePresenter
import app.ext.log
import com.example.namigtahmazli.sweetnote.R
import io.reactivex.Single
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
        viewModel.drawerToggler.postValue(true)
    }

    val navigationItemSelectedListener = NavigationView.OnNavigationItemSelectedListener {
        when (it.itemId) {
            R.id.menu_log_out -> {
                disposables += observeLogoutWarningDialogAction()
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

    fun onAddButtonClicked(@Suppress("UNUSED_PARAMETER") view: View) {
        homeActivity.openAddEditDialog()
    }

    private fun observeLogoutWarningDialogAction(): Disposable {
        return showLogoutWarningDialog()
                .subscribeBy(
                        onSuccess = { doLogout ->
                            if (doLogout) {
                                disposables += observeLogginUserOut()
                            }
                        },
                        onError = { it.log<HomePresenter>("while observing logging out warning dialog action.") }
                )
    }

    private fun showLogoutWarningDialog(): Single<Boolean> {
        return Single.create<Boolean> {
            AlertDialog.Builder(homeActivity)
                    .setTitle(R.string.home_activity_logout_warning_dialog_title)
                    .setMessage(R.string.home_activity_logout_warning_dialog_message)
                    .setPositiveButton(R.string.home_activity_logout_warning_dialog_positive_action) { _, _ -> it.onSuccess(true) }
                    .setNegativeButton(R.string.home_activity_logout_warning_dialog_negative_action) { _, _ -> it.onSuccess(false) }
                    .show()
        }
    }
}