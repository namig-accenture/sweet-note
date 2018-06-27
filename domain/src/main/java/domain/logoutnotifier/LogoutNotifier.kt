package domain.logoutnotifier

import domain.lifecycle.ApplicationLifecycyle
import domain.lifecycle.Lifecycle
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import java.util.concurrent.TimeUnit

abstract class LogoutNotifier : KoinComponent {
    private val lifecycle: ApplicationLifecycyle by inject()
    private val touchDisposables by lazy { CompositeDisposable() }
    private val sessionDisposable by lazy { CompositeDisposable() }
    private val backgroundSessionDisposable by lazy { CompositeDisposable() }
    val touchObserver: PublishSubject<Boolean> = PublishSubject.create<Boolean>()
    private var hasCurrentSession = false
    private var hasRegisteredTouchObserver = false
    private var hasBeenForegrounded = false
    private var backgroundSessionEnded = false

    init {
        lifecycle.lifecycle.subscribeBy(
                onNext = {
                    when (it) {
                        Lifecycle.Foregrounded -> {
                            if (backgroundSessionEnded) {
                                navigateToEnterPinScreen()
                            } else {
                                endBackgroundSession()
                            }
                            hasBeenForegrounded = true
                            startNewSession()
                            registerTouchObserver()
                        }
                        Lifecycle.Backgrounded -> {
                            endCurrentSession()
                            unregisterTouchObserver()
                            startBackgroundSession()
                        }
                    }
                }
        )
    }

    private fun startNewSession() {
        if (!hasCurrentSession) {
            sessionDisposable += Observable.interval(SESSION_TIME, TIME_UNIT)
                    .subscribeBy(
                            onNext = { onSessionEnded() },
                            onError = { logException(it) }
                    )
            hasCurrentSession = true
        }
    }

    private fun startBackgroundSession() {
        if (hasBeenForegrounded && backgroundSessionEnded) {
            backgroundSessionDisposable += Observable.interval(SESSION_TIME, TIME_UNIT)
                    .doOnSubscribe { backgroundSessionEnded = false }
                    .subscribeBy(
                            onNext = { endBackgroundSession() },
                            onError = { logException(it) }
                    )
        }
    }

    private fun endBackgroundSession() {
        backgroundSessionEnded = true
        backgroundSessionDisposable.clear()
    }

    private fun onSessionEnded() {
        endCurrentSession()
        navigateToEnterPinScreen()
    }

    abstract fun navigateToEnterPinScreen()

    abstract fun logException(throwable: Throwable)

    private fun registerTouchObserver() {
        if (!hasRegisteredTouchObserver) {
            touchDisposables += touchObserver
                    .debounce(TOUCH_SPEED, TOUCH_TIME_UNIT)
                    .subscribeBy(
                            onNext = { resetSession() },
                            onError = { logException(it) }
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
        const val SESSION_TIME = 5L
        const val TOUCH_SPEED = 500L
        val TIME_UNIT = TimeUnit.MINUTES
        val TOUCH_TIME_UNIT = TimeUnit.MILLISECONDS
    }
}