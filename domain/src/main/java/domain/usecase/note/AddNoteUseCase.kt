package domain.usecase.note

import domain.exceptions.UserNotFoundException
import domain.extensions.fromOptional
import domain.model.NoteModel
import domain.repositories.NoteRepository
import domain.usecase.SingleParametrisedUseCase
import domain.usecase.login.GetCurrentLoggedInUserUseCase
import io.reactivex.Single

class AddNoteUseCase(private val getCurrentLoggedInUserUseCase: GetCurrentLoggedInUserUseCase,
                     private val noteRepository: NoteRepository)
    : SingleParametrisedUseCase<NoteModel, NoteModel>() {
    override fun build(param: NoteModel): Single<NoteModel> {
        return getCurrentLoggedInUserUseCase.chain()
                .flatMap {optionalUser->
                    return@flatMap optionalUser.fromOptional {
                        noteRepository.addNote(param.copy(userId = it.id))
                    } ?: throw UserNotFoundException("No current logged in user found")
                }
    }
}