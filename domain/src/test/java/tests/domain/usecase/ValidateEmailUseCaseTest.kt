package tests.domain.usecase

import domain.usecase.login.ValidateEmailUseCase
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.BehaviorSubject
import org.junit.Before
import org.junit.Test

class ValidateEmailUseCaseTest {
    companion object {
        const val validEmail = "namig@gmail.com"
    }

    private val invalidEmailCases: Array<String> = arrayOf(
            "nam", "na%m", "nam.",
            "nam.@", "nam@", "nam@.",
            "nam@co", "nam@co$.com",
            "nam@g.co.", "nam@g.c",
            "nam@_cs.com","nam@x._co"
    )

    private lateinit var validateEmailUseCase: ValidateEmailUseCase
    private lateinit var testObserver: TestObserver<Boolean>
    private lateinit var testScheduler: TestScheduler

    @Before
    fun setUp() {
        testObserver = TestObserver()
        testScheduler = TestScheduler()
        validateEmailUseCase = ValidateEmailUseCase().apply {
            schedulerTransformer = TestSchedulerTransformer(testScheduler, testScheduler)
        }
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