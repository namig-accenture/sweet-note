package app.view

import org.mockito.Mockito

fun <T : Any> safeEq(value: T): T = Mockito.eq(value) ?: value