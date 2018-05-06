package app.views.pinactivity

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import android.view.View
import app.ext.BasePresenter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject

@Suppress("UNUSED_PARAMETER")
internal class PinPresenter(private val pinActivity: PinActivity) : BasePresenter() {
    private lateinit var disposables: CompositeDisposable

    private lateinit var pinObserver: PublishSubject<CharSequence>

    val viewModel by lazy { pinActivity.pinViewModel }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun createDisposable() {
        disposables = CompositeDisposable()
        pinObserver = PublishSubject.create()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun initObservers() {
        disposables += observePinChanges()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun clearDisposables() {
        disposables.clear()
    }

    fun onPinValueChanged(pin: CharSequence?, start: Int, before: Int, count: Int) {
        pin?.let { pinObserver.onNext(it) }
    }

    private fun observePinChanges(): Disposable {
        return pinObserver
                .map(CharSequence::toString)
                .distinctUntilChanged()
                .doOnNext(viewModel.pin::set)
                .map { it.length == viewModel.pinLength }
                .doOnNext(viewModel.isContinueButtonEnabled::set)
                .map { it && !viewModel.isContinueButtonVisible.get() }
                .subscribeBy(
                        onNext = {
                            println(it)
                            if (it) {
                                disposables += observeValidatingPin()
                            }
                        }
                )
    }

    fun onBtnContinueClicked(view: View) {
        disposables += viewModel.registerPin()
                .subscribeBy(
                        onComplete = pinActivity::handleRegisteringPin,
                        onError = pinActivity::handleRegisteringPinError
                )
    }

    private fun observeValidatingPin(): Disposable {
        return viewModel.validatePin()
                .subscribeBy(
                        onSuccess = pinActivity::handleValidatingPin,
                        onError = pinActivity::handleValidatingPinError
                )
    }
}