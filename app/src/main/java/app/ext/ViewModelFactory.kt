package app.ext

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import app.dagger.AppScope
import javax.inject.Inject
import javax.inject.Provider

@AppScope
internal class ViewModelFactory @Inject constructor
(private val creators: MutableMap<Class<out ViewModel>, Provider<ViewModel>>) : ViewModelProvider.Factory {

    inline fun <reified T : ViewModel> create(): T {
        return create(T::class.java)
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val creator: Provider<ViewModel>? = creators[modelClass] ?: kotlin.run {
            creators.filter { it.key.isAssignableFrom(modelClass) }.values.first()
        }
        @Suppress("UNCHECKED_CAST")
        return (creator?.get() as? T)
                ?: throw IllegalAccessException("ViewModel could not be found")
    }
}