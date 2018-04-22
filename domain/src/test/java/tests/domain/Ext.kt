package tests.domain

import org.mockito.Mockito

fun <T : Any> safeEq(value: T): T = Mockito.eq(value) ?: value