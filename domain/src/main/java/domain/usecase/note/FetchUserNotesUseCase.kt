package domain.usecase.note

import domain.exceptions.UserNotFoundException
import domain.extensions.fromOptional
import domain.model.FetchNotesRequestModel
import domain.model.NoteModel
import domain.repositories.UserRepository
import domain.usecase.ObservableParametrisedUseCase
import domain.usecase.login.GetCurrentLoggedInUserUseCase
import io.reactivex.Observable

class FetchUserNotesUseCase(private val userRepository: UserRepository,
                            private val getCurrentLoggedInUserUseCase: GetCurrentLoggedInUserUseCase)
    : ObservableParametrisedUseCase<FetchNotesRequestModel, List<NoteModel>>() {
    override fun build(param: FetchNotesRequestModel): Observable<List<NoteModel>> {
        return getCurrentLoggedInUserUseCase.chain()
                .flatMapObservable {
                    it.fromOptional { userModel ->
                        userRepository.findNotesForUser(userModel, param)
                    } ?: throw UserNotFoundException("No current user found")
                }
    }
}