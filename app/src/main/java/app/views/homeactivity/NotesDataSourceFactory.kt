package app.views.homeactivity

import android.arch.paging.DataSource
import domain.model.NoteModel
import domain.usecase.note.FetchUserNotesUseCase
import domain.usecase.note.GetNotesCountUseCase
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

internal class NotesDataSourceFactory(private val fetchUserNotesUseCase: FetchUserNotesUseCase,
                                      private val getNotesCountUseCase: GetNotesCountUseCase,
                                      private val dispose: (Disposable) -> Unit) : DataSource.Factory<Int, NoteModel>() {
    val loadState: PublishSubject<NotesDataSource.LoadState> = PublishSubject.create<NotesDataSource.LoadState>()
    override fun create(): DataSource<Int, NoteModel> {
        return NotesDataSource(fetchUserNotesUseCase, getNotesCountUseCase, dispose).apply {
            loadState.subscribe(this@NotesDataSourceFactory.loadState)
        }
    }
}