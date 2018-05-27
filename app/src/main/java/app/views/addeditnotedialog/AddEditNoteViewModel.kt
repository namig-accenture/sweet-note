package app.views.addeditnotedialog

import android.arch.lifecycle.MutableLiveData
import app.ext.BaseViewModel
import domain.exceptions.FieldNotDefinedException
import domain.model.NoteModel
import domain.usecase.note.AddNoteUseCase
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject

internal class AddEditNoteViewModel(private val addNoteUseCase: AddNoteUseCase) : BaseViewModel() {
    val titleTextObserver: PublishSubject<CharSequence> = PublishSubject.create()
    val usernameTextObserver: PublishSubject<CharSequence> = PublishSubject.create()
    val passwordTextObserver: PublishSubject<CharSequence> = PublishSubject.create()

    val title: MutableLiveData<String> = MutableLiveData()
    val username: MutableLiveData<String> = MutableLiveData()
    val password: MutableLiveData<String> = MutableLiveData()
    val titleError: MutableLiveData<String> = MutableLiveData()
    val usernameError: MutableLiveData<String> = MutableLiveData()
    val passwordError: MutableLiveData<String> = MutableLiveData()
    val saveButtonEnabled: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply {
        if (value == null) {
            postValue(false)
        }
    }

    fun addNote(): Single<NoteModel> {
        return password.value?.let { password ->
            username.value?.let { username ->
                title.value?.let { title ->
                    addNoteUseCase.get(NoteModel(
                            userName = username,
                            password = password,
                            title = title
                    ))
                } ?: Single.error(FieldNotDefinedException(name = "title"))
            } ?: Single.error(FieldNotDefinedException(name = "username"))
        } ?: Single.error(FieldNotDefinedException(name = "password"))
    }

    inline fun observeTitleTextChanges(crossinline onNext: (Boolean) -> Unit,
                                       crossinline onError: (Throwable) -> Unit): Disposable {
        return titleTextObserver
                .map(CharSequence::toString)
                .distinctUntilChanged()
                .doOnNext {
                    title.value = it
                    updateSaveButtonState()
                }
                .skip(1)
                .map { it.isEmpty() || it.isBlank() }
                .subscribeBy(
                        onNext = { onNext(it) },
                        onError = { onError(it) }
                )
    }

    inline fun observeUsernameTextChanges(crossinline onNext: (Boolean) -> Unit,
                                          crossinline onError: (Throwable) -> Unit): Disposable {
        return usernameTextObserver
                .map(CharSequence::toString)
                .distinctUntilChanged()
                .doOnNext {
                    username.value = it
                    updateSaveButtonState()
                }
                .skip(1)
                .map { it.isEmpty() || it.isBlank() }
                .subscribeBy(
                        onNext = { onNext(it) },
                        onError = { onError(it) }
                )
    }

    inline fun observePasswordTextChanges(crossinline onNext: (Boolean) -> Unit,
                                          crossinline onError: (Throwable) -> Unit): Disposable {
        return passwordTextObserver
                .map(CharSequence::toString)
                .distinctUntilChanged()
                .doOnNext {
                    password.value = it
                    updateSaveButtonState()
                }
                .skip(1)
                .map { it.isEmpty() || it.isBlank() }
                .subscribeBy(
                        onNext = { onNext(it) },
                        onError = { onError(it) }
                )
    }

    private val String?.isValid get() = this != null && !isEmpty() && !isBlank()

    private fun updateSaveButtonState() {
        saveButtonEnabled.postValue(
                title.value.isValid
                        &&
                        username.value.isValid
                        &&
                        password.value.isValid
        )
    }
}