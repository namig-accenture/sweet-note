package domain.transformers

import domain.executor.PostExecutionThread
import domain.executor.ThreadExecutor
import io.reactivex.*
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AndroidSchedulerTransformer @Inject constructor(threadExecutor: ThreadExecutor,
                                                      postExecutionThread: PostExecutionThread) : SchedulerTransformer {
    private val subscribeScheduler: Scheduler = Schedulers.from(threadExecutor)
    private val observeScheduler: Scheduler = postExecutionThread.scheduler

    override fun <T> applyObservableTransformer(): ObservableTransformer<T, T> {
        return ObservableTransformer { it.subscribeOn(subscribeScheduler).observeOn(observeScheduler) }
    }

    override fun <T> applySingleTransformer(): SingleTransformer<T, T> {
        return SingleTransformer { it.subscribeOn(subscribeScheduler).observeOn(observeScheduler) }
    }

    override fun applyCompletableTransformer(): CompletableTransformer {
        return CompletableTransformer { it.subscribeOn(subscribeScheduler).observeOn(observeScheduler) }
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
}
