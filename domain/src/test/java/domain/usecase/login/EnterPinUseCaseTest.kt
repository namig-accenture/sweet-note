package domain.usecase.login

import com.fernandocejas.arrow.optional.Optional
import domain.extensions.asOptional
import domain.model.UserModel
import domain.repositories.UserRepository
import domain.safeEq
import domain.transformers.DebugTransformer
import domain.transformers.SchedulerTransformer
import domain.usecase.TestDebugTransformer
import domain.usecase.TestSchedulerTransformer
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.TestScheduler
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class EnterPinUseCaseTest : KoinTest {
    private lateinit var testScheduler: TestScheduler
    private lateinit var testObserver: TestObserver<Any>

    private val user = UserModel(1, "a@b.com", "abc")

    private inline fun provideTestModule(crossinline mock: UserRepository.() -> Unit = {}): Module {
        return applicationContext {
            bean { TestSchedulerTransformer(testScheduler, testScheduler) as SchedulerTransformer }
            bean { (TestDebugTransformer() as DebugTransformer).asOptional }
            bean {
                mock(UserRepository::class.java).apply {
                    `when`(currentUser).thenReturn(Single.just(user.asOptional))
                    `when`(saveCurrentUser(safeEq(user.copy(pin = "1234")))).thenReturn(Completable.complete())
                    mock()
                } as UserRepository
            }
            bean { GetCurrentLoggedInUserUseCase(get()) }
            bean { EnterPinUseCase(get(), get()) }
        }
    }

    private val enterPinUseCase by inject<EnterPinUseCase>()

    @Before
    fun setUp() {
        testScheduler = TestScheduler()
        testObserver = TestObserver()
    }

    @After
    fun tareDown() {
        closeKoin()
    }

    @Test
    fun updateCurrentUserWithValidPinSucceed() {
        startKoin(listOf(provideTestModule()))
        enterPinUseCase.get("1234")
                .subscribe(testObserver)
        testScheduler.triggerActions()
        testObserver.assertComplete()
    }

    @Test
    fun ifThereIsNotCurrentUserWillThrowException() {
        startKoin(listOf(provideTestModule {
            `when`(currentUser).thenReturn(Single.just(Optional.absent()))
        }))
        enterPinUseCase.get("1234").subscribe(testObserver)
        testScheduler.triggerActions()
        testObserver.assertError { it is IllegalAccessException && it.message == "No current user found" }
    }
}