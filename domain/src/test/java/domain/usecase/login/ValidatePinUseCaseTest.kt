package domain.usecase.login

import com.fernandocejas.arrow.optional.Optional
import domain.extensions.asOptional
import domain.model.UserModel
import domain.repositories.UserRepository
import domain.transformers.DebugTransformer
import domain.transformers.SchedulerTransformer
import domain.usecase.TestDebugTransformer
import domain.usecase.TestSchedulerTransformer
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.TestScheduler
import org.junit.After
import org.junit.Test
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import org.koin.test.KoinTest
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class ValidatePinUseCaseTest : KoinTest {
    private val testScheduler by inject<Scheduler>()
    private val testObserver by inject<TestObserver<Boolean>>()
    private val validatePinUseCase by inject<ValidatePinUseCase>()

    private val user = UserModel(1, "a@b.com", "abc", "1234")

    private inline fun provideTestModule(crossinline mockBlock: UserRepository.() -> Unit = {}): Module {
        return applicationContext {
            bean { TestScheduler() as Scheduler }
            bean { TestObserver<Boolean>() }
            bean { TestSchedulerTransformer(testScheduler, testScheduler) as SchedulerTransformer }
            bean { (TestDebugTransformer() as DebugTransformer).asOptional }
            bean {
                mock(UserRepository::class.java).apply {
                    `when`(currentUser).thenReturn(Single.just(user.asOptional))
                    mockBlock()
                } as UserRepository
            }
            factory { GetCurrentLoggedInUserUseCase(get()) }
            factory { ValidatePinUseCase(get()) }
        }
    }

    @After
    fun tareDown() {
        closeKoin()
    }

    @Test
    fun pinMatchesWithCurrentUserPin() {
        startKoin(listOf(provideTestModule()))
        validatePinUseCase.get("1234").subscribe(testObserver)
        (testScheduler as TestScheduler).triggerActions()
        testObserver.assertValue { it }
    }

    @Test
    fun ifNoCurrentUserFoundExceptionWillBeThrown() {
        startKoin(listOf(provideTestModule {
            `when`(currentUser).thenReturn(Single.just(Optional.absent()))
        }))
        validatePinUseCase.get("1234").subscribe(testObserver)
        (testScheduler as TestScheduler).triggerActions()
        testObserver.assertError { it is IllegalAccessException && it.message == "No current user found" }
    }
}