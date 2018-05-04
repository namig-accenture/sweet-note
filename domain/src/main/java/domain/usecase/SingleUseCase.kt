package domain.usecase

import io.reactivex.Single

abstract class SingleUseCase<R> : BaseUseCase() {
    abstract fun build(): Single<R>
    private fun make(wrap: Boolean): Single<R> {
        val build = build()
        val wrapped = if (wrap) wrap(build) else build
        return wrapDebug(wrapped)
    }

    private fun wrap(single: Single<R>): Single<R> {
        return single.compose(schedulerTransformer.applySingleTransformer())
    }

    private fun wrapDebug(single: Single<R>): Single<R> {
        return if (debugTransformer.isPresent) {
            single.compose(debugTransformer.get().singleDebugTransformer())
        } else {
            single
        }
    }

    fun get(): Single<R> = make(true)
    fun chain(): Single<R> = make(false)
}