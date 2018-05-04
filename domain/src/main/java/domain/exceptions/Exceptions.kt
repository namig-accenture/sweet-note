package domain.exceptions

class CurrentUserNotFoundException(message: String) : IllegalAccessException(message)
class UserNotFoundException(message: String) : IllegalAccessException(message)
class PinNotDefinedException(message: String) : IllegalAccessException(message)