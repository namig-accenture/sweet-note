package domain

import io.reactivex.Scheduler

interface PostExecutionThread {
    val scheduler: Scheduler
}