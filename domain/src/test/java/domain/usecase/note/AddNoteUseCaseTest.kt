package domain.usecase.note

import domain.extensions.asOptional
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
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when` as on

class AddNoteUseCaseTest : KoinTest {
    private lateinit var testScheduler: TestScheduler
    private lateinit var testObserver: TestObserver<NoteModel>

    private val testModule = applicationContext {
        bean { (TestDebugTransformer() as DebugTransformer).asOptional }
        bean { TestSchedulerTransformer(testScheduler, testScheduler) as SchedulerTransformer }
        bean { mock(UserRepository::class.java) as UserRepository }
        bean { mock(NoteRepository::class.java) as NoteRepository }
        factory { GetCurrentLoggedInUserUseCase(get()) }
        factory { AddNoteUseCase(get(), get()) }
    }

    private val userRepository by inject<UserRepository>()
    private val noteRepository by inject<NoteRepository>()
    private val addNoteUseCase by inject<AddNoteUseCase>()

    @Before
    fun setUp() {
        testScheduler = TestScheduler()
        testObserver = TestObserver()
        startKoin(listOf(testModule))
    }

    @After
    fun tareDown() {
        closeKoin()
    }

    @Test
    fun addForValidUserWillSucceed() {
        on(userRepository.currentUser).thenReturn(Single.just(
                UserModel(1, "a@b.com", "abc", "123").asOptional
        ))
        val note = NoteModel(
                userName = "abc",
                password = "abc",
                title = "abc"
        )
        on(noteRepository.addNote(safeEq(note.copy(userId = 1)))).thenReturn(Single.just(note.copy(id = 1, userId = 1)))
        addNoteUseCase.get(note)
                .subscribeOn(testScheduler)
                .subscribe(testObserver)
        testScheduler.triggerActions()
        testObserver.assertValue { it == note.copy(id = 1, userId = 1) }
    }
}