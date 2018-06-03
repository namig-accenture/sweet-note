package domain.usecase

import io.reactivex.Flowable

abstract class FlowableParametrisedUseCase<PARAM, RETURN> : BaseUseCase() {
    fun withUpstream(upstream: Flowable<PARAM>): Flowable<RETURN> {
        return upstream
                .onBackpressureLatest()
                .compose(::process)
                .wrap()
                .wrapDebug()
    }

    private fun Flowable<RETURN>.wrap(): Flowable<RETURN> {
        return compose(schedulerTransformer.applyFlowableTransformer())
    }

    private fun Flowable<RETURN>.wrapDebug(): Flowable<RETURN> {
        return if (debugTransformer.isPresent) {
            compose(debugTransformer.get().flowableDebugTransformer())
        } else {
            this
        }
    }

    abstract fun process(param: Flowable<PARAM>): Flowable<RETURN>
}