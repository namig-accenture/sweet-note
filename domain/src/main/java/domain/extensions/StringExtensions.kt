package domain.extensions

val String?.isValid get() = this != null && !isEmpty() && !isBlank()