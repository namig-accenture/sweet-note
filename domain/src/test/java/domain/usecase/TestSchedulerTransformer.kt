package domain.usecase

import domain.transformers.SchedulerTransformer
import io.reactivex.*

class TestSchedulerTransformer(private val subscribeScheduler: Scheduler,
                               private val observeScheduler: Scheduler) : SchedulerTransformer {

    override fun <T> applyObservableTransformer(): ObservableTransformer<T, T> {
        return ObservableTransformer { it.subscribeOn(subscribeScheduler).observeOn(observeScheduler) }
    }

    override fun <T> applySingleTransformer(): SingleTransformer<T, T> {
        return SingleTransformer { it.subscribeOn(subscribeScheduler).observeOn(observeScheduler) }
    }

    override fun applyCompletableTransformer(): CompletableTransformer {
        return CompletableTransformer { it.subscribeOn(subscribeScheduler).observeOn(observeScheduler) }
    }

    override fun <T> applyFlowableTransformer(): FlowableTransformer<T, T> {
        return FlowableTransformer { it.subscribeOn(subscribeScheduler).observeOn(observeScheduler) }
    }

    override fun <T> applySubscribeScheduler(observable: Observable<T>): Observable<T> {
        return observable.subscribeOn(subscribeScheduler)
    }

    override fun <T> applySubscribeScheduler(single: Single<T>): Single<T> {
        return single.subscribeOn(subscribeScheduler)
    }

    override fun applySubscribeScheduler(completable: Completable): Completable {
        return completable.subscribeOn(subscribeScheduler)
    }

    override fun <T> applySubscribeScheduler(flowable: Flowable<T>): Flowable<T> {
        return flowable.subscribeOn(subscribeScheduler)
    }
}