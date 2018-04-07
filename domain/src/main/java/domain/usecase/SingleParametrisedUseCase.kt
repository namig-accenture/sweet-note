package domain.usecase

import io.reactivex.Single

abstract class SingleParametrisedUseCase<in P, R> : BaseUseCase() {
    abstract fun build(param: P): Single<R>
    private fun make(param: P, wrap: Boolean): Single<R> {
        fun wrap(): Single<R> {
            return build(param).compose(schedulerTransformer.applySingleTransformer())
        }
        return if (wrap) wrap() else build(param)
    }

    fun get(param: P): Single<R> = make(param, true)
    fun chain(param: P): Single<R> = make(param, false)
}