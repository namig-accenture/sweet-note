package app.ext

import app.views.launchactivity.LaunchActivity
import domain.logoutnotifier.LogoutNotifier
import timber.log.Timber

internal class LogoutObserver(private val activity: BaseActivity<*>) : LogoutNotifier() {
    override fun navigateToEnterPinScreen() {
        activity.startActivity(LaunchActivity.startNewSession(activity))
        activity.finish()
    }

    override fun logException(throwable: Throwable) {
        Timber.e(throwable)
    }
}
