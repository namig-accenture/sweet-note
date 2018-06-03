package data.repositories

import data.extensions.asEntity
import data.persistance.note.NoteDao
import domain.model.NoteModel
import domain.model.UserModel
import domain.repositories.NoteRepository
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

class NoteRepositoryImpl(private val noteDao: NoteDao) : NoteRepository {
    override fun addNote(noteModel: NoteModel): Single<NoteModel> {
        return Single.just(noteModel)
                .map { noteModel.asEntity }
                .map { noteDao.insert(it) }
                .map { noteModel.copy(id = it) }
    }

    override fun updateNote(noteModel: NoteModel): Single<Int> {
        return Single.fromCallable { noteDao.update(noteModel.asEntity) }
    }

    override fun deleteNote(noteModel: NoteModel): Single<Int> {
        return Single.fromCallable { noteDao.delete(noteModel.asEntity) }
    }

    override fun findNoteById(id: Long): Maybe<NoteModel> {
        return noteDao.findNoteById(id)
                .map { it.noteModel }
    }

    override fun findNotesByTitle(userModel: UserModel, title: String): Observable<List<NoteModel>> {
        return noteDao.findNotesByTitle(userModel.id!!, "%$title%")
                .flatMapSingle {
                    Flowable.fromIterable(it)
                            .map { it.noteModel }
                            .toList()
                }.toObservable()
    }
}