package domain

import domain.transformers.AndroidSchedulerTransformer
import domain.transformers.SchedulerTransformer
import domain.usecase.login.ValidateEmailUseCase
import domain.usecase.login.ValidatePasswordUseCase
import org.koin.dsl.module.applicationContext

val general = applicationContext {
    bean { AndroidSchedulerTransformer(get(), get()) as SchedulerTransformer }
}

val useCase = applicationContext {
    bean { ValidateEmailUseCase() }
    bean { ValidatePasswordUseCase() }
}

val domainModule = listOf(general, useCase)