package data.executor

import data.dagger.scopes.AppScope
import domain.executor.ThreadExecutor
import java.util.concurrent.*
import javax.inject.Inject

@AppScope
class JobExecutor @Inject constructor() : ThreadExecutor {
    private val queue: BlockingDeque<Runnable> = LinkedBlockingDeque()
    private val threadPoolExecutor: ThreadPoolExecutor = ThreadPoolExecutor(INITIAL_COUNT, MAX_COUNT, ALIVE_TIME, ALIVE_TIME_UNIT, queue, JobThreadFactory)
    override fun execute(command: Runnable?) {
        threadPoolExecutor.execute(command)
    }

    object JobThreadFactory : ThreadFactory {
        private var count = 0
        override fun newThread(r: Runnable?): Thread {
            return Thread(r, "android_${count++}")
        }
    }

    companion object {
        const val INITIAL_COUNT = 3
        const val MAX_COUNT = 5
        const val ALIVE_TIME = 10L
        val ALIVE_TIME_UNIT = TimeUnit.SECONDS
    }
}