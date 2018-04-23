package tests.domain.usecase

import com.fernandocejas.arrow.optional.Optional
import domain.extensions.asOptional
import domain.model.UserModel
import domain.repositories.UserRepository
import domain.transformers.SchedulerTransformer
import domain.usecase.login.LogUserInUseCase
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
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class LogUserInUseCaseTest : KoinTest {
    companion object {
        const val EMAIL = "a@b.com"
        const val PASSWORD = "abcABC123@#$"
    }

    private val userModel by lazy {
        Single.just(UserModel(1, EMAIL, PASSWORD).asOptional)
    }

    private lateinit var testScheduler: TestScheduler
    private lateinit var testObserver: TestObserver<Any>
    private fun provideTestModule(mockBlock: UserRepository.() -> Unit = {}): Module {
        return applicationContext {
            bean { TestSchedulerTransformer(testScheduler, testScheduler) as SchedulerTransformer }
            bean {
                mock(UserRepository::class.java).apply {
                    Mockito.`when`(logUserIn(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                            .thenReturn(userModel)
                    Mockito.`when`(saveLoggedInUserId(ArgumentMatchers.anyLong())).thenReturn(Completable.complete())
                    mockBlock()
                } as UserRepository
            }
            factory { LogUserInUseCase(get()) }
        }
    }

    private val logUserInUseCase: LogUserInUseCase by inject()

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
    fun logUserInSucceed() {
        startKoin(listOf(provideTestModule()))
        logUserInUseCase.get(UserModel(email = EMAIL, password = PASSWORD))
                .subscribe(testObserver)
        testScheduler.triggerActions()
        testObserver.assertComplete()
    }

    @Test
    fun logUserInFailed() {
        startKoin(listOf(provideTestModule {
            `when`(logUserIn(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                    .thenReturn(Single.just(Optional.absent()))
        }))
        logUserInUseCase.get(UserModel(email = EMAIL, password = PASSWORD))
                .subscribe(testObserver)
        testScheduler.triggerActions()
        testObserver.assertError { it is IllegalAccessException }
    }
}