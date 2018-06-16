package app.views.homeactivity

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import android.support.annotation.MenuRes
import app.ext.BaseViewModel
import com.example.namigtahmazli.sweetnote.R
import domain.model.NoteModel
import domain.usecase.note.FetchUserNotesUseCase
import domain.usecase.note.GetNotesCountUseCase
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.PublishSubject

internal class HomeViewModel(private val fetchUserNotesUseCase: FetchUserNotesUseCase,
                             private val getNotesCountUseCase: GetNotesCountUseCase) : BaseViewModel() {
    @MenuRes
    val menuRes = R.menu.activity_home_menu
    val refreshing by lazy { MutableLiveData<Boolean>().apply { value = false } }
    val listIsEmpty by lazy { MutableLiveData<Boolean>().apply { value = false } }
    val loadState: PublishSubject<NotesDataSource.LoadState> = PublishSubject.create<NotesDataSource.LoadState>()
    val disposables by lazy { CompositeDisposable() }
    val userNotesObserver: LiveData<PagedList<NoteModel>> by lazy {
        val factory = NotesDataSourceFactory(fetchUserNotesUseCase, getNotesCountUseCase) {
            disposables += it
        }
        factory.loadState.subscribe(loadState)
        val pagedListConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(PREFETCH_DISTANCE)
                .setInitialLoadSizeHint(PAGE_SIZE)
                .setPageSize(PAGE_SIZE)
                .build()
        LivePagedListBuilder(factory, pagedListConfig).build()
    }

    companion object {
        const val PAGE_SIZE = 10
        const val PREFETCH_DISTANCE = 5
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}