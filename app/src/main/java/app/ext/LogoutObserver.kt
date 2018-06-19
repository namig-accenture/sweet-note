package app.ext

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import app.views.launchactivity.LaunchActivity
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit

internal class LogoutObserver(private val activity: BaseActivity<*>) : LifecycleObserver {
    private val touchDisposables by lazy { CompositeDisposable() }
    private val sessionDisposable by lazy { CompositeDisposable() }
    val touchObserver: PublishSubject<Boolean> = PublishSubject.create<Boolean>()
    private var hasCurrentSession = false
    private var hasRegisteredTouchObserver = false

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onStart() {
        startNewSession()
        registerTouchObserver()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onStop() {
        endCurrentSession()
        unregisterTouchObserver()
    }

    private fun startNewSession() {
        if (!hasCurrentSession) {
            sessionDisposable += Observable.interval(SESSION_TIME, TIME_UNIT)
                    .subscribeBy(
                            onNext = { onSessionEnded() },
                            onError = { Timber.e(it) }
                    )
            hasCurrentSession = true
        }
    }

    private fun onSessionEnded() {
        endCurrentSession()
        navigateToEnterPinScreen()
    }

    private fun navigateToEnterPinScreen() {
        activity.startActivity(LaunchActivity.getIntent(activity))
        activity.finish()
    }

    private fun registerTouchObserver() {
        if (!hasRegisteredTouchObserver) {
            touchDisposables += touchObserver
                    .debounce(TOUCH_SPEED, TOUCH_TIME_UNIT)
                    .subscribeBy(
                            onNext = { resetSession() },
                            onError = { Timber.e(it) }
                    )
            hasRegisteredTouchObserver = true
        }
    }

    private fun resetSession() {
        endCurrentSession()
        startNewSession()
    }

    private fun endCurrentSession() {
        if (hasCurrentSession) {
            sessionDisposable.clear()
            hasCurrentSession = false
        }
    }

    private fun unregisterTouchObserver() {
        if (hasRegisteredTouchObserver) {
            touchDisposables.clear()
            hasRegisteredTouchObserver = false
        }
    }

    companion object {
        const val SESSION_TIME = 1L
        const val TOUCH_SPEED = 500L
        val TIME_UNIT = TimeUnit.MINUTES
        val TOUCH_TIME_UNIT = TimeUnit.MILLISECONDS
    }
}
