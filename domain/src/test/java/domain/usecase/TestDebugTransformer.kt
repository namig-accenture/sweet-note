package domain.usecase

import domain.transformers.DebugTransformer
import io.reactivex.CompletableTransformer
import io.reactivex.ObservableTransformer
import io.reactivex.SingleTransformer

class TestDebugTransformer : DebugTransformer {
    override fun <T> observableDebugTransformer(): ObservableTransformer<T, T> {
        return ObservableTransformer {
            it.doOnNext { println("onNext [Value: $it] [Thread: ${Thread.currentThread()}]") }
                    .doOnSubscribe { println("onSubscribe [Thread: ${Thread.currentThread()}]") }
                    .doOnError { println("onError [Error: $it] [Thread: ${Thread.currentThread()}]") }
        }
    }

    override fun <T> singleDebugTransformer(): SingleTransformer<T, T> {
        return SingleTransformer {
            it.doOnSuccess { println("onNext [Value: $it] [Thread: ${Thread.currentThread()}]") }
                    .doOnSubscribe { println("onSubscribe [Thread: ${Thread.currentThread()}]") }
                    .doOnError { println("onError [Error: $it] [Thread: ${Thread.currentThread()}]") }
        }
    }

    override fun completableDebugTransformer(): CompletableTransformer {
        return CompletableTransformer {
            it.doOnComplete { println("onNext [Value: $it] [Thread: ${Thread.currentThread()}]") }
                    .doOnSubscribe { println("onSubscribe [Thread: ${Thread.currentThread()}]") }
                    .doOnError { println("onError [Error: $it] [Thread: ${Thread.currentThread()}]") }
        }
    }
}