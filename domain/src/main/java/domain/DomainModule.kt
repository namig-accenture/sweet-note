package domain

import domain.transformers.AndroidSchedulerTransformer
import domain.transformers.SchedulerTransformer
import domain.usecase.login.*
import org.koin.dsl.module.applicationContext

val general = applicationContext {
    bean { AndroidSchedulerTransformer(get(), get()) as SchedulerTransformer }
}

val useCase = applicationContext {
    factory { ValidateEmailUseCase() }
    factory { ValidatePasswordUseCase() }
    factory { RegisterUserUseCase(get()) }
    factory { LogUserInUseCase(get()) }
    factory { GetCurrentLoggedInUserUseCase(get()) }
    factory { EnterPinUseCase(get(), get()) }
    factory { ValidatePinUseCase(get()) }
}

val domainModule = listOf(general, useCase)