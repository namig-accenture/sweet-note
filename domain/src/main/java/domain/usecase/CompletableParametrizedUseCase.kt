package domain.usecase

import io.reactivex.Completable

abstract class CompletableParametrizedUseCase<in P> : BaseUseCase() {
    abstract fun build(param: P): Completable

    private fun make(param: P, wrap: Boolean): Completable {
        val build = build(param)
        val wrapped = if (wrap) wrap(build) else build
        return wrapDebug(wrapped)
    }

    private fun wrap(completable: Completable): Completable {
        return completable.compose(schedulerTransformer.applyCompletableTransformer())
    }

    private fun wrapDebug(completable: Completable): Completable {
        return if (debugTransformer.isPresent) {
            completable.compose(debugTransformer.get().completableDebugTransformer())
        } else {
            completable
        }
    }

    fun get(param: P): Completable {
        return make(param, true)
    }

    fun chain(param: P): Completable {
        return make(param, false)
    }
}