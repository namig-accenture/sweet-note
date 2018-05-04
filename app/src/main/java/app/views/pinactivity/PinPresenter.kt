package app.views.pinactivity

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import app.ext.BasePresenter
import app.ext.log
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy

internal class PinPresenter(private val pinActivity: PinActivity) : BasePresenter() {
    private lateinit var disposables: CompositeDisposable

    val dataBinding by lazy { pinActivity.dataBinding }
    val viewModel by lazy { pinActivity.pinViewModel }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun createDisposable() {
        disposables = CompositeDisposable()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun initDisposables() {
        disposables += observePinChanges()
        disposables += observeContinueButtonClicks()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun clearDisposables() {
        disposables.clear()
    }

    private fun observePinChanges(): Disposable {
        return RxTextView.textChanges(dataBinding.pin)
                .filter(CharSequence::isNotEmpty)
                .map(CharSequence::toString)
                .doOnNext(viewModel.pin::set)
                .map { it.length == viewModel.pinLength }
                .doOnNext(viewModel.isContinueButtonEnabled::set)
                .map { it && !viewModel.isContinueButtonVisible.get() }
                .subscribeBy(
                        onNext = {
                            if (it) {
                                disposables += observeValidatingPin()
                            }
                        },
                        onError = { it.log<PinPresenter>("While observing pin changes.") }
                )
    }

    private fun observeContinueButtonClicks(): Disposable {
        return RxView.clicks(dataBinding.btnContinue)
                .firstElement()
                .subscribeBy(
                        onSuccess = { disposables += observeRegisteringPin() },
                        onError = { it.log<PinPresenter>("While observing continue button clicks.") }
                )
    }

    private fun observeRegisteringPin(): Disposable {
        return viewModel.registerPin()
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