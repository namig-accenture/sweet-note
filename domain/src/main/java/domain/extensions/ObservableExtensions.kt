package domain.extensions

import io.reactivex.ObservableSource
import io.reactivex.SingleSource
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver

fun <T> ObservableSource<T>.subscribeAndDispose(
        onNext: (T) -> Unit = {},
        onComplete: () -> Unit = {},
        onError: (Throwable) -> Unit = {}
) {
    val disposableObserver = object : DisposableObserver<T>() {
        override fun onComplete() {
            onComplete()
            dispose()
        }

        override fun onNext(t: T) {
            onNext(t)
            dispose()
        }

        override fun onError(e: Throwable) {
            onError(e)
            dispose()
        }
    }
    subscribe(disposableObserver)
}

fun <T> SingleSource<T>.subscribeAndDispose(
        onSuccess: (T) -> Unit = {},
        onError: (Throwable) -> Unit = {}
) {
    val disposableObserver = object : DisposableSingleObserver<T>() {
        override fun onSuccess(t: T) {
            onSuccess(t)
            dispose()
        }

        override fun onError(e: Throwable) {
            onError(e)
            dispose()
        }
    }
    subscribe(disposableObserver)
}
