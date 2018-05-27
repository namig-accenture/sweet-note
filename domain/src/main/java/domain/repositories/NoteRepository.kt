package domain.repositories

import domain.model.NoteModel
import io.reactivex.Maybe
import io.reactivex.Single

interface NoteRepository {
    fun addNote(noteModel: NoteModel): Single<NoteModel>
    fun updateNote(noteModel: NoteModel): Single<Int>
    fun deleteNote(noteModel: NoteModel): Single<Int>
    fun findNoteById(id: Long): Maybe<NoteModel>
}