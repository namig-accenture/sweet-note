package domain.usecase

import io.reactivex.Completable

abstract class CompletableUseCase : BaseUseCase() {
    abstract fun build(): Completable

    private fun make(wrap: Boolean): Completable {
        val build = build()
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

    fun get(): Completable {
        return make(true)
    }

    fun chain(): Completable {
        return make(false)
    }
}