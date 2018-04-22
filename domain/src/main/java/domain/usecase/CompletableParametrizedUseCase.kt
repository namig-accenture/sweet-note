package domain.usecase

import io.reactivex.Completable

abstract class CompletableParametrizedUseCase<in P> : BaseUseCase() {
    abstract fun build(param: P): Completable
    fun get(param: P): Completable {
        return build(param).compose(schedulerTransformer.applyCompletableTransformer())
    }

    fun chain(param: P): Completable {
        return build(param)
    }
}