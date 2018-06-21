package app.views.addeditnotedialog

import android.arch.lifecycle.MutableLiveData
import app.ext.BaseViewModel
import domain.exceptions.FieldNotDefinedException
import domain.extensions.isValid
import domain.model.NoteModel
import domain.usecase.note.AddNoteUseCase
import domain.usecase.note.EditNoteUseCase
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject

internal class AddEditNoteViewModel(private val addNoteUseCase: AddNoteUseCase,
                                    private val editNoteUseCase: EditNoteUseCase) : BaseViewModel() {
    val titleTextObserver: PublishSubject<CharSequence> = PublishSubject.create()
    val usernameTextObserver: PublishSubject<CharSequence> = PublishSubject.create()
    val passwordTextObserver: PublishSubject<CharSequence> = PublishSubject.create()

    val note = MutableLiveData<NoteModel>()
    val title = MutableLiveData<String>()
    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val titleError = MutableLiveData<String>()
    val usernameError = MutableLiveData<String>()
    val passwordError = MutableLiveData<String>()
    val saveButtonEnabled = MutableLiveData<Boolean>().apply {
        if (value == null) {
            postValue(false)
        }
    }

    fun addNote(): Single<NoteModel> {
        return validateFields()
                .flatMap {
                    addNoteUseCase.get(it)
                }
    }

    fun editNote(): Single<Int> {
        return validateFields()
                .flatMap {
                    editNoteUseCase.get(it)
                }
    }

    private fun validateFields(): Single<NoteModel> = Single.create { emitter ->
        password.value?.let { password ->
            username.value?.let { username ->
                title.value?.let { title ->
                    emitter.onSuccess(
                            note.value?.copy(
                                    userName = username,
                                    password = password,
                                    title = title
                            ) ?: NoteModel(
                                    userName = username,
                                    password = password,
                                    title = title
                            )
                    )
                } ?: emitter.onError(FieldNotDefinedException(name = "title"))
            } ?: emitter.onError(FieldNotDefinedException(name = "username"))
        } ?: emitter.onError(FieldNotDefinedException(name = "password"))
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

    private fun updateSaveButtonState() {
        saveButtonEnabled.postValue(
                title.value.isValid
                        &&
                        username.value.isValid
                        &&
                        password.value.isValid
        )
    }

    fun doesHaveChanges(): Single<Boolean> {
        return Single.fromCallable {
            note.value?.let {
                it.userName != username.value || it.password != password.value || it.title != title.value
            } ?: run {
                username.value.isValid || password.value.isValid || title.value.isValid
            }
        }
    }
}