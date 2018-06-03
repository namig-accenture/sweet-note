package domain.usecase.note

import com.fernandocejas.arrow.optional.Optional
import domain.exceptions.UserNotFoundException
import domain.extensions.asOptional
import domain.mock
import domain.model.NoteModel
import domain.model.UserModel
import domain.repositories.NoteRepository
import domain.repositories.UserRepository
import domain.safeEq
import domain.transformers.DebugTransformer
import domain.transformers.SchedulerTransformer
import domain.usecase.TestDebugTransformer
import domain.usecase.TestSchedulerTransformer
import domain.usecase.login.GetCurrentLoggedInUserUseCase
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.TestScheduler
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.`when` as on

class QueryNotesByTitleUseCaseTest : KoinTest {
    private lateinit var testScheduler: TestScheduler
    private lateinit var testObserver: TestObserver<List<NoteModel>>

    val testModule = applicationContext {
        bean { TestSchedulerTransformer(testScheduler, testScheduler) as SchedulerTransformer }
        bean { (TestDebugTransformer() as DebugTransformer).asOptional }
        bean { mock<UserRepository>() }
        bean { mock<NoteRepository>() }
        factory { GetCurrentLoggedInUserUseCase(get()) }
        factory { QueryNotesByTitleUseCase(get(), get()) }
    }

    private val userRepository by inject<UserRepository>()
    private val noteRepository by inject<NoteRepository>()
    private val queryNotesByTitleUseCase by inject<QueryNotesByTitleUseCase>()

    private val user by lazy { UserModel(id = 1, password = "demo", email = "demo@demo.com", pin = "1234") }
    private val note by lazy { NoteModel(id = 1, title = "Demo", userName = "DemoUser", password = "DemoPass", userId = user.id) }

    @Before
    fun setUp() {
        testScheduler = TestScheduler()
        startKoin(listOf(testModule))
    }

    @After
    fun tareDown() {
        closeKoin()
    }

    @Test
    fun ifNoCurrentUserFoundWillThrowException() {
        on(userRepository.currentUser).thenReturn(Single.just(Optional.absent()))
        testObserver = TestObserver()
        queryNotesByTitleUseCase.get("demo")
                .subscribeOn(testScheduler)
                .subscribe(testObserver)
        testScheduler.triggerActions()
        testObserver.assertError { it is UserNotFoundException }
    }

    @Test
    fun ifNoNotesFoundEmptyListWillBeReturned() {
        on(userRepository.currentUser).thenReturn(Single.just(user.asOptional))
        on(noteRepository.findNotesByTitle(safeEq(user), ArgumentMatchers.anyString()))
                .thenReturn(Observable.just(emptyList()))
        testObserver = TestObserver()
        queryNotesByTitleUseCase.get("demo")
                .subscribeOn(testScheduler)
                .subscribe(testObserver)
        testScheduler.triggerActions()
        testObserver.assertValue { it.isEmpty() }
    }

    @Test
    fun ifNotesFoundWillBeReturned() {
        on(userRepository.currentUser).thenReturn(Single.just(user.asOptional))
        on(noteRepository.findNotesByTitle(safeEq(user), ArgumentMatchers.anyString()))
                .thenReturn(Observable.just(listOf(note)))
        testObserver = TestObserver()
        queryNotesByTitleUseCase.get("demo")
                .subscribeOn(testScheduler)
                .subscribe(testObserver)
        testScheduler.triggerActions()
        testObserver.assertValue { it.size == 1 && it.first() == note }
    }
}