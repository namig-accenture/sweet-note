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
import java.lang.Exception

internal class NotesDataSource(private val fetchUserNotesUseCase: FetchUserNotesUseCase,
                               private val getNotesCountUseCase: GetNotesCountUseCase,
                               private val addDisposable: (Disposable) -> Unit)
    : PositionalDataSource<NoteModel>() {

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<NoteModel>) {
        loadData(InitialState.Range, FetchNotesRequestModel(params.loadSize, params.startPosition)) {
            callback.setResult(it)
        }
    }

    private fun getNotesCount(onNext: (Int) -> Unit) {
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
                if (it.size == size) {
                    callback.setResult(it, position, count)
                } else {
                    invalidate()
                }
            }
        }
    }

    private fun LoadInitialCallback<NoteModel>.setResult(list: List<NoteModel>, position: Int, count: Int) {
        try {
            onResult(list, position, count)
        } catch (ex: Exception) {
            invalidate()
        }
    }

    private fun LoadRangeCallback<NoteModel>.setResult(list: List<NoteModel>) {
        try {
            onResult(list)
        } catch (ex: Exception) {
            invalidate()
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
                                it.log<NotesDataSource>("While loading notes.")
                                loadState.onNext(LoadState.Error)
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