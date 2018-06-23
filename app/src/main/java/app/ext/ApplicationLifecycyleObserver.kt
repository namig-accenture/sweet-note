package app.ext

import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import domain.lifecycle.ApplicationLifecycyle
import domain.lifecycle.Lifecycle
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class ApplicationLifecycyleObserver : ApplicationLifecycyle, LifecycleObserver {
    private val applicationLifecycle = BehaviorSubject.create<Lifecycle>()
    override val lifecycle: Observable<Lifecycle>
        get() = applicationLifecycle

    @OnLifecycleEvent(android.arch.lifecycle.Lifecycle.Event.ON_START)
    fun onStart() {
        applicationLifecycle.onNext(Lifecycle.Foregrounded)
    }

    @OnLifecycleEvent(android.arch.lifecycle.Lifecycle.Event.ON_STOP)
    fun onStop() {
        applicationLifecycle.onNext(Lifecycle.Backgrounded)
    }
}