package domain.exceptions

class CurrentUserNotFoundException(message: String? = null) : IllegalAccessException(message)
class UserNotFoundException(message: String? = null) : IllegalAccessException(message)
class PinNotDefinedException(message: String? = null) : IllegalAccessException(message)
class InvalidEmailException(message: String? = null) : IllegalAccessException(message)
class InvalidPasswordException(message: String? = null) : IllegalAccessException(message)