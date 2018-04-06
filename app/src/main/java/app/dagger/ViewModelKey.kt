package app.dagger

import android.arch.lifecycle.ViewModel
import dagger.MapKey
import kotlin.reflect.KClass

@MustBeDocumented
@MapKey
@Retention(AnnotationRetention.RUNTIME)
internal annotation class ViewModelKey(val clazz: KClass<out ViewModel>)