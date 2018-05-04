package domain.usecase

import domain.extensions.asOptional
import domain.model.UserModel
import domain.repositories.UserRepository
import domain.transformers.SchedulerTransformer
import domain.usecase.login.RegisterUserUseCase
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
import domain.safeEq
import domain.transformers.DebugTransformer

class RegisterUserUseCaseTest : KoinTest {
    companion object {
        const val EMAIL = "a@b.com"
        const val PASSWORD = "abc"
    }

    private val user = UserModel(email = EMAIL, password = PASSWORD)

    private val registerUserUseCase: RegisterUserUseCase by inject()

    private lateinit var testScheduler: TestScheduler
    private lateinit var testObserver: TestObserver<Any>

    private val testModule: Module by lazy {
        applicationContext {
            bean { TestSchedulerTransformer(testScheduler, testScheduler) as SchedulerTransformer }
            bean { (TestDebugTransformer() as DebugTransformer).asOptional }
            bean {
                mock(UserRepository::class.java).apply {
                    `when`(registerUser(safeEq(user))).thenReturn(Single.just(user.copy(id = 1)))
                    `when`(saveCurrentUser(safeEq(user.copy(id = 1)))).thenReturn(Completable.complete())
                }
            }
            factory { RegisterUserUseCase(get()) }
        }
    }


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
    fun registerUserSucceed() {
        registerUserUseCase
                .get(UserModel(email = EMAIL, password = PASSWORD))
                .subscribe(testObserver)
        testScheduler.triggerActions()
        testObserver.assertComplete()
    }
}