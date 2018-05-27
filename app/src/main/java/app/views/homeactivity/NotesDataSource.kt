package app.views.homeactivity

import android.arch.paging.PositionalDataSource
import app.ext.log
import domain.model.FetchNotesRequestModel
import domain.model.NoteModel
import domain.usecase.note.FetchUserNotesUseCase
import domain.usecase.note.GetNotesCountUseCase
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject

internal class NotesDataSource(private val fetchUserNotesUseCase: FetchUserNotesUseCase,
                               private val getNotesCountUseCase: GetNotesCountUseCase,
                               private val addDisposable: (Disposable) -> Unit)
    : PositionalDataSource<NoteModel>() {

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<NoteModel>) {
        loadData(InitialState.Range, FetchNotesRequestModel(params.loadSize, params.startPosition)) {
            callback.onResult(it)
        }
    }

    private inline fun getNotesCount(crossinline onNext: (Int) -> Unit) {
        dispose {
            getNotesCountUseCase.get()
                    .subscribeBy(
                            onSuccess = { onNext(it) },
                            onError = { it.log<NotesDataSource>("While getting notes count") }
                    )
        }
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<NoteModel>) {
        getNotesCount { count ->
            val position = computeInitialLoadPosition(params, count)
            val size = computeInitialLoadSize(params, position, count)
            loadData(InitialState.Initial, FetchNotesRequestModel(size, position)) {
                callback.onResult(it, position, count)
            }
        }
    }

    val loadState: PublishSubject<LoadState> = PublishSubject.create<LoadState>()

    private fun loadData(initialState: InitialState,
                         fetchNotesRequestModel: FetchNotesRequestModel,
                         onNext: (List<NoteModel>) -> Unit) {
        dispose {
            fetchUserNotesUseCase.get(fetchNotesRequestModel)
                    .doOnSubscribe { loadState.onNext(LoadState.Loading) }
                    .subscribeBy(
                            onNext = {
                                onNext(it)
                                val value = if (it.isEmpty() && initialState == InitialState.Initial) {
                                    LoadState.Empty
                                } else {
                                    LoadState.Loaded
                                }
                                loadState.onNext(value)
                            },
                            onError = {
                                if (it is IllegalStateException) {
                                    invalidate()
                                } else {
                                    it.log<NotesDataSource>("While loading notes.")
                                    loadState.onNext(LoadState.Error)
                                }
                            }
                    )
        }
    }

    private inline fun dispose(block: () -> Disposable) {
        this.addDisposable(block())
    }

    enum class LoadState {
        Loading,
        Loaded,
        Empty,
        Error
    }

    enum class InitialState {
        Initial,
        Range
    }
}