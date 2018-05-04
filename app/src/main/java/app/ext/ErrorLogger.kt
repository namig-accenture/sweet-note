package app.ext

import timber.log.Timber

inline fun <reified T> Throwable.log(message: String) {
    Timber.e(this, T::class.java.name, message)
}