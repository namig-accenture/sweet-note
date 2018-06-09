package app.views.searchdialog

import android.arch.lifecycle.MutableLiveData
import app.ext.BaseViewModel
import domain.model.NoteModel
import domain.usecase.note.QueryNotesByTitleUseCase
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

internal class SearchViewModel(private val queryNotesByTitleUseCase: QueryNotesByTitleUseCase)
    : BaseViewModel() {
    val queryTextObserver: BehaviorSubject<CharSequence> = BehaviorSubject.create()
    val lastQuery: MutableLiveData<String> = MutableLiveData()

    inline fun observeQueryResults(crossinline onNext: (List<NoteModel>) -> Unit,
                                   crossinline onError: (Throwable) -> Unit): Disposable {
        return queryTextObserver
                .map(CharSequence::toString)
                .doOnNext(lastQuery::postValue)
                .filter(CharSequence::isNotEmpty)
                .debounce(TYPE_SPEED_LIMIT, TIME_UNIT)
                .distinctUntilChanged()
                .flatMap(::queryByTitle)
                .subscribeBy(
                        onNext = { onNext(it) },
                        onError = { onError(it) }
                )
    }

    private fun queryByTitle(title: String): Observable<List<NoteModel>> {
        return queryNotesByTitleUseCase.get(title)
    }

    companion object {
        const val TYPE_SPEED_LIMIT = 500L
        val TIME_UNIT = TimeUnit.MILLISECONDS
    }
}