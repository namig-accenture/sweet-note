package domain.lifecycle

import io.reactivex.Observable

enum class Lifecycle {
    Backgrounded,
    Foregrounded
}

interface ApplicationLifecycyle {
    val lifecycle: Observable<Lifecycle>
}