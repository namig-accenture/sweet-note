package domain.usecase

import domain.extensions.fromOptional
import io.reactivex.Observable

abstract class ObservableUseCase<RETURN> : BaseUseCase() {
    abstract fun build(): Observable<RETURN>
    private fun make(wrap: Boolean): Observable<RETURN> {
        val built = build()
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

    fun get(): Observable<RETURN> = make(true)
    fun chain(): Observable<RETURN> = make(false)
}