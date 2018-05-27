package domain.usecase.login

import domain.extensions.asOptional
import domain.transformers.DebugTransformer
import domain.transformers.SchedulerTransformer
import domain.usecase.TestDebugTransformer
import domain.usecase.TestSchedulerTransformer
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.BehaviorSubject
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.closeKoin
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.test.KoinTest

class ValidateEmailUseCaseTest : KoinTest {
    companion object {
        const val validEmail = "namig@gmail.com"
    }

    private val invalidEmailCases: Array<String> = arrayOf(
            "nam", "na%m", "nam.",
            "nam.@", "nam@", "nam@.",
            "nam@co", "nam@co$.com",
            "nam@g.co.", "nam@g.c",
            "nam@_cs.com", "nam@x._co"
    )

    private lateinit var validateEmailUseCase: ValidateEmailUseCase
    private lateinit var testObserver: TestObserver<Boolean>
    private lateinit var testScheduler: TestScheduler

    @Before
    fun setUp() {
        testObserver = TestObserver()
        testScheduler = TestScheduler()
        val testModule = applicationContext {
            bean { TestSchedulerTransformer(testScheduler, testScheduler) as SchedulerTransformer }
            bean { (TestDebugTransformer() as DebugTransformer).asOptional }
        }
        startKoin(listOf(testModule))
        validateEmailUseCase = ValidateEmailUseCase()
    }

    @After
    fun tareDown() {
        closeKoin()
    }

    @Test
    fun testValidEmail() {
        runTest(validEmail)
        testObserver.assertValue { it }
    }

    @Test
    fun testAllInvalidEmailCases() {
        invalidEmailCases.forEach {
            runTest(it)
            testObserver.assertValue { !it }
            testObserver = TestObserver()
        }
    }

    private fun runTest(email: String) {
        val emailPublisher: BehaviorSubject<String> = BehaviorSubject.create()
        validateEmailUseCase.withUpstream(emailPublisher).subscribe(testObserver)
        emailPublisher.onNext(email)
        testScheduler.triggerActions()
    }
}