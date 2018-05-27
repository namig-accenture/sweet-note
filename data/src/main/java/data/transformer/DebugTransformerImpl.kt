package data.transformer

import domain.transformers.DebugTransformer
import io.reactivex.CompletableTransformer
import io.reactivex.FlowableTransformer
import io.reactivex.ObservableTransformer
import io.reactivex.SingleTransformer
import timber.log.Timber

class DebugTransformerImpl : DebugTransformer {
    override fun <T> observableDebugTransformer(): ObservableTransformer<T, T> {
        return ObservableTransformer {
            it.doOnNext { Timber.v("onNext [Value: %s] [Thread: %s]", it, Thread.currentThread()) }
                    .doOnSubscribe { Timber.v("onSubscribe [Thread: %s]", Thread.currentThread()) }
                    .doOnError { Timber.v("onError [Error: %s] [Thread: %s]", it.toString(), Thread.currentThread()) }
        }
    }

    override fun <T> singleDebugTransformer(): SingleTransformer<T, T> {
        return SingleTransformer {
            it.doOnSuccess { Timber.v("onSuccess [Value: %s] [Thread: %s]", it, Thread.currentThread()) }
                    .doOnSubscribe { Timber.v("onSubscribe [Thread: %s]", Thread.currentThread()) }
                    .doOnError { Timber.v("onError [Error: %s] [Thread: %s]", it.toString(), Thread.currentThread()) }
        }
    }

    override fun completableDebugTransformer(): CompletableTransformer {
        return CompletableTransformer {
            it.doOnComplete { Timber.v("onComplete [Value: %s] [Thread: %s]", it, Thread.currentThread()) }
                    .doOnSubscribe { Timber.v("onSubscribe [Thread: %s]", Thread.currentThread()) }
                    .doOnError { Timber.v("onError [Error: %s] [Thread: %s]", it.toString(), Thread.currentThread()) }
        }
    }

    override fun <T> flowableDebugTransformer(): FlowableTransformer<T, T> {
        return FlowableTransformer {
            it.doOnSubscribe { Timber.v("onSubscribe [Thread: %s]", Thread.currentThread()) }
                    .doOnNext { Timber.v("onNext [Value: %s] [Thread: %s]", it, Thread.currentThread()) }
                    .doOnError { Timber.v("onError [Error: %s] [Thread: %s]", it.toString(), Thread.currentThread()) }
        }
    }
}