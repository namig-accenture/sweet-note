package app.views.homeactivity

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import app.ext.BaseViewModel
import domain.model.NoteModel
import domain.usecase.note.FetchUserNotesUseCase
import domain.usecase.note.GetNotesCountUseCase
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

internal class HomeViewModel(private val fetchUserNotesUseCase: FetchUserNotesUseCase,
                             private val getNotesCountUseCase: GetNotesCountUseCase) : BaseViewModel() {
    val refreshing by lazy { MutableLiveData<Boolean>().apply { value = false } }
    val listIsEmpty by lazy { MutableLiveData<Boolean>().apply { value = false } }
    val loadState: PublishSubject<NotesDataSource.LoadState> = PublishSubject.create<NotesDataSource.LoadState>()
    fun fetchUserNotes(dispose: (Disposable) -> Unit): LiveData<PagedList<NoteModel>> {
        val factory = NotesDataSourceFactory(fetchUserNotesUseCase, getNotesCountUseCase, dispose)
        factory.loadState.subscribe(loadState)
        val pagedListConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(PREFETCH_DISTANCE)
                .setInitialLoadSizeHint(PAGE_SIZE)
                .setPageSize(PAGE_SIZE)
                .build()
        return LivePagedListBuilder(factory, pagedListConfig).build()
    }

    companion object {
        const val PAGE_SIZE = 10
        const val PREFETCH_DISTANCE = 5
    }
}