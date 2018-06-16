package domain.usecase.note

import domain.model.NoteModel
import domain.repositories.NoteRepository
import domain.usecase.SingleParametrisedUseCase
import io.reactivex.Single

class EditNoteUseCase(private val noteRepository: NoteRepository)
    : SingleParametrisedUseCase<NoteModel, Int>() {
    override fun build(param: NoteModel): Single<Int> {
        return noteRepository.updateNote(noteModel = param)
    }
}