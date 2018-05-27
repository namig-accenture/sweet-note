package app.extensions

import android.content.Context
import android.support.annotation.RequiresApi

@RequiresApi(23)
inline fun <reified T> Context.systemService(): T {
    return getSystemService(T::class.java)
}

inline fun <reified T> Context.systemService(name: String): T {
    return getSystemService(name) as T
}

