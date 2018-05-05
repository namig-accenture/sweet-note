package app.views.pinactivity

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import android.view.View
import app.ext.BasePresenter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy

@Suppress("UNUSED_PARAMETER")
internal class PinPresenter(private val pinActivity: PinActivity) : BasePresenter() {
    private lateinit var disposables: CompositeDisposable

    val viewModel by lazy { pinActivity.pinViewModel }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun createDisposable() {
        disposables = CompositeDisposable()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun clearDisposables() {
        disposables.clear()
    }

    fun onPinValueChanged(pin: CharSequence?, start: Int, before: Int, count: Int) {
        pin?.toString()?.let {
            viewModel.run {
                this.pin.run {
                    if (it == get()) {
                        return
                    }
                    set(it)
                }
                (it.length == pinLength).let {
                    isContinueButtonEnabled.set(it)
                    if (it && !isContinueButtonVisible.get()) {
                        disposables += observeValidatingPin()
                    }
                }
            }
        }
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