package domain.usecase

import domain.extensions.fromOptional
import io.reactivex.Observable

abstract class ObservableParametrisedUseCase<in PARAM, RETURN> : BaseUseCase() {
    abstract fun build(param: PARAM): Observable<RETURN>
    private fun make(param: PARAM, wrap: Boolean): Observable<RETURN> {
        val built = build(param)
        val wrapped = if (wrap) wrap(built) else built
        return wrapDebug(wrapped)
    }

    private fun wrap(flowable: Observable<RETURN>): Observable<RETURN> {
        return flowable.compose(schedulerTransformer.applyObservableTransformer())
    }

    private fun wrapDebug(flowable: Observable<RETURN>): Observable<RETURN> {
        return debugTransformer.fromOptional {
            flowable.compose(it.observableDebugTransformer())
        } ?: flowable
    }

    fun get(param: PARAM): Observable<RETURN> = make(param, true)
    fun chain(param: PARAM): Observable<RETURN> = make(param, false)
}