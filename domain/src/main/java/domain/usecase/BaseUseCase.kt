package domain.usecase

import domain.transformers.SchedulerTransformer
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

abstract class BaseUseCase {
    @Inject
    lateinit var schedulerTransformer: SchedulerTransformer

    fun <T> Observable<T>.applySubscribeScheduler(): Observable<T> {
        return schedulerTransformer.applySubscribeScheduler(this)
    }

    fun <T> Single<T>.applySubscribeScheduler(): Single<T> {
        return schedulerTransformer.applySubscribeScheduler(this)
    }

    fun Completable.applySubscribeScheduler(): Completable {
        return schedulerTransformer.applySubscribeScheduler(this)
    }
}