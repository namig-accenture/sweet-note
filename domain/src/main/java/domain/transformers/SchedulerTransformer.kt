package domain.transformers

import io.reactivex.*

interface SchedulerTransformer {
    fun <T> applyObservableTransformer(): ObservableTransformer<T, T>
    fun <T> applySingleTransformer(): SingleTransformer<T, T>
    fun applyCompletableTransformer(): CompletableTransformer
    fun <T> applyFlowableTransformer(): FlowableTransformer<T, T>
    fun <T> applySubscribeScheduler(observable: Observable<T>): Observable<T>
    fun <T> applySubscribeScheduler(single: Single<T>): Single<T>
    fun applySubscribeScheduler(completable: Completable): Completable
    fun <T> applySubscribeScheduler(flowable: Flowable<T>): Flowable<T>
}