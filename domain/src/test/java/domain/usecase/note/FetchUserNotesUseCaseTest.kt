package domain.usecase.note

import com.fernandocejas.arrow.optional.Optional
import domain.exceptions.UserNotFoundException
import domain.extensions.asOptional
import domain.model.FetchNotesRequestModel
import domain.model.NoteModel
import domain.model.UserModel
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class FetchUserNotesUseCaseTest : KoinTest {
    private lateinit var testScheduler: TestScheduler
    private lateinit var testObserver: TestObserver<List<NoteModel>>

    private val testModule = applicationContext {
        bean { (TestDebugTransformer() as DebugTransformer).asOptional }
        bean { TestSchedulerTransformer(testScheduler, testScheduler) as SchedulerTransformer }
        bean { mock(UserRepository::class.java) as UserRepository }
        factory { GetCurrentLoggedInUserUseCase(get()) }
        factory { FetchUserNotesUseCase(get(), get()) }
    }

    private val fetchUserNotesUseCase by inject<FetchUserNotesUseCase>()
    private val userRepository by inject<UserRepository>()

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
    fun ifNoCurrentUserFoundWillThrowException() {
        `when`(userRepository.currentUser).thenReturn(Single.just(Optional.absent()))
        fetchUserNotesUseCase.chain(FetchNotesRequestModel(20,0))
                .subscribeOn(testScheduler)
                .subscribe(testObserver)
        testScheduler.triggerActions()
        testObserver.assertError { it is UserNotFoundException }
    }

    @Test
    fun ifThereIsNoNoteForUserWillReturnEmptyList() {
        val testObserver = object : TestObserver<List<NoteModel>>() {
            override fun onComplete() {
                assertTrue(true)
            }

            override fun onNext(t: List<NoteModel>) {
                assertTrue(t.isEmpty())
            }

            override fun onError(t: Throwable) {
                assertTrue(false)
            }
        }
        `when`(userRepository.currentUser).thenReturn(
                Single.just(UserModel(1, "a@b.com", "abc", "1234").asOptional)
        )
        `when`(userRepository.findNotesForUser(
                safeEq(UserModel(1, "a@b.com", "abc", "1234")),
                safeEq(FetchNotesRequestModel(20,0))
        )).thenReturn(Observable.just(emptyList()))

        fetchUserNotesUseCase.get(FetchNotesRequestModel(20,0))
                .subscribeOn(testScheduler)
                .subscribe(testObserver)
        testScheduler.triggerActions()
    }

    @Test
    fun ifNotesFoundForUserWillReturnThem() {
        val testObserver = object : TestObserver<List<NoteModel>>() {
            override fun onNext(t: List<NoteModel>) {
                assertTrue(t.size == 5 && t.first().id == 1L && t.last().id == 5L)
            }
        }

        `when`(userRepository.currentUser).thenReturn(
                Single.just(UserModel(1, "a@b.com", "abc", "1234").asOptional)
        )
        `when`(userRepository.findNotesForUser(
                safeEq(UserModel(1, "a@b.com", "abc", "1234")),
                safeEq(FetchNotesRequestModel(20,0))
        )).thenReturn(Observable.just(
                listOf(
                        NoteModel(1, 1, "a@b.com", "abc", "demo"),
                        NoteModel(2, 1, "a@b.com", "abc", "demo"),
                        NoteModel(3, 1, "a@b.com", "abc", "demo"),
                        NoteModel(4, 1, "a@b.com", "abc", "demo"),
                        NoteModel(5, 1, "a@b.com", "abc", "demo")
                )
        ))

        fetchUserNotesUseCase.get(FetchNotesRequestModel(20,0))
                .subscribeOn(testScheduler)
                .subscribe(testObserver)
        testScheduler.triggerActions()
    }
}