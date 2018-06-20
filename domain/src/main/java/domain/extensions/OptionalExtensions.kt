package domain.extensions

import com.fernandocejas.arrow.optional.Optional

val <T> T?.asOptional: Optional<T>
    get() = if (this == null)
        Optional.absent()
    else
        Optional.fromNullable(this)

val <T> Optional<T>.fromOptional: T? get() = if (this.isPresent) get() else null

inline fun <T, R> Optional<T>.fromOptional(block: (T) -> R?): R? {
    return if (this.isPresent) {
        block(get())
    } else {
        null
    }
}