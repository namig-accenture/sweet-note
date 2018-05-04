package domain.usecase

import com.fernandocejas.arrow.optional.Optional
import domain.transformers.DebugTransformer
import domain.transformers.SchedulerTransformer
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

abstract class BaseUseCase : KoinComponent {
    val schedulerTransformer: SchedulerTransformer by inject()
    val debugTransformer: Optional<DebugTransformer> by inject()

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