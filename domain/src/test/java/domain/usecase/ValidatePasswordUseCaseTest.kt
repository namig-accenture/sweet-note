package domain.usecase

import domain.transformers.SchedulerTransformer
import domain.usecase.login.ValidatePasswordUseCase
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.TestScheduler
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.test.KoinTest

class ValidatePasswordUseCaseTest : KoinTest {

    companion object {
        const val VALID_PASSWORD = "abcAbc123@#$"
    }

    private lateinit var validatePasswordUseCase: ValidatePasswordUseCase
    private lateinit var testObserver: TestObserver<Boolean>
    private lateinit var testScheduler: TestScheduler

    @Before
    fun setUp() {
        testScheduler = TestScheduler()
        testObserver = TestObserver()
        val testModule = applicationContext {
            bean { TestSchedulerTransformer(testScheduler, testScheduler) as SchedulerTransformer }
        }
        startKoin(listOf(testModule))
        validatePasswordUseCase = ValidatePasswordUseCase()
    }

    @After
    fun tareDown() {
        closeKoin()
    }

    @Test
    fun testValidPassword() {
        run(VALID_PASSWORD) { it }
    }

    @Test
    fun passwordShouldContainsAtLeast8Chars() {
        run("abAB12@") { !it }
    }

    @Test
    fun passwordShouldContainAtLeast1LowercaseChar() {
        run("ABC1234@#$%") { !it }
    }

    @Test
    fun passwordShouldContainAtLeast1UppercaseChar() {
        run("abcd123@#$") { !it }
    }

    @Test
    fun passwordShouldContainAtLeast1Digit() {
        run("abcABC@#$") { !it }
    }

    @Test
    fun passwordShouldContainAtLeast1SpecialChars() {
        run("abcABC123") { !it }
    }
    
    private fun run(password: String, block: (Boolean) -> Boolean) {
        validatePasswordUseCase.withUpstream(Observable.just(password))
                .subscribe(testObserver)
        testScheduler.triggerActions()
        testObserver.assertValue(block)
    }
}