package domain.services

interface PreferenceService {
    fun saveUserId(id: Long)
    val userId: Long
}