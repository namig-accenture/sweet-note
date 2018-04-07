package domain.usecase

import io.reactivex.Observable

abstract class FlowableUseCase<U, D> : BaseUseCase() {
    private var disposed: Boolean = false
    var upstream: Observable<U> = Observable.empty()
        get() = field.doOnDispose { disposed = true }

    fun withUpstream(upstream: Observable<U>): Observable<D> {
        this.upstream = upstream
        return make()
    }

    abstract fun process(upstream: Observable<U>): Observable<D>
    private fun make(): Observable<D> {
        return upstream.compose(::process).compose(schedulerTransformer.applyObservableTransformer())
    }
}