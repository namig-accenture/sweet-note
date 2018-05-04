package data.extensions

import com.fernandocejas.arrow.optional.Optional
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import domain.extensions.asOptional

inline fun <reified T : Any> T.toJson(moshi: Moshi): String {
    val jsonAdapter: JsonAdapter<T> = moshi.adapter(T::class.java)
    return jsonAdapter.toJson(this)
}

inline fun <reified T : Any> String?.fromJsonAsObject(moshi: Moshi): Optional<T> {
    return this?.let {
        val jsonAdapter: JsonAdapter<T> = moshi.adapter(T::class.java)
        jsonAdapter.fromJson(it)
    }.asOptional
}

inline fun <reified T : Any> List<T>.toJson(moshi: Moshi): String {
    val type = Types.newParameterizedType(List::class.java, T::class.java)
    val jsonAdapter: JsonAdapter<List<T>> = moshi.adapter(type)
    return jsonAdapter.toJson(this)
}

inline fun <reified T : Any> String?.fromJsonAsList(moshi: Moshi): Optional<List<T>> {
    return this?.let {
        val type = Types.newParameterizedType(List::class.java, T::class.java)
        val jsonAdapter: JsonAdapter<List<T>> = moshi.adapter(type)
        jsonAdapter.fromJson(this)
    }.asOptional
}