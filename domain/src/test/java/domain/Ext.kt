package domain

import org.mockito.Mockito

fun <T : Any> safeEq(value: T): T = Mockito.eq(value) ?: value

fun sleep(duration: Long) {
    Thread.sleep(duration)
}

inline fun <reified T> mock(whenBlock: T.() -> Unit = {}): T {
    return Mockito.mock(T::class.java).apply {
        whenBlock()
    }
}