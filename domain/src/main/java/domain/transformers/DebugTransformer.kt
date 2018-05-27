package domain.transformers

import io.reactivex.CompletableTransformer
import io.reactivex.FlowableTransformer
import io.reactivex.ObservableTransformer
import io.reactivex.SingleTransformer

interface DebugTransformer {
    fun <T> observableDebugTransformer(): ObservableTransformer<T, T>
    fun <T> singleDebugTransformer(): SingleTransformer<T, T>
    fun completableDebugTransformer(): CompletableTransformer
    fun <T> flowableDebugTransformer(): FlowableTransformer<T, T>
}