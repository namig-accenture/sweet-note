package domain.usecase.note

import domain.exceptions.UserNotFoundException
import domain.extensions.fromOptional
import domain.model.NoteModel
import domain.repositories.NoteRepository
import domain.usecase.ObservableParametrisedUseCase
import domain.usecase.login.GetCurrentLoggedInUserUseCase
import io.reactivex.Observable

class QueryNotesByTitleUseCase(private val getCurrentLoggedInUserUseCase: GetCurrentLoggedInUserUseCase,
                               private val noteRepository: NoteRepository) : ObservableParametrisedUseCase<String, List<NoteModel>>() {
    override fun build(param: String): Observable<List<NoteModel>> {
        return getCurrentLoggedInUserUseCase.chain()
                .flatMapObservable {
                    it.fromOptional {
                        noteRepository.findNotesByTitle(it, param)
                    } ?: throw UserNotFoundException("No currently logged in user found.")
                }
    }
}