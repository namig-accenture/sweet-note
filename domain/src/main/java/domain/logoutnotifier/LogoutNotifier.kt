package domain.logoutnotifier

import io.reactivex.subjects.PublishSubject

interface LogoutNotifier {
    val touchObserver: PublishSubject<Boolean>
    fun startNewSession()
    fun endCurrentSession()
    fun registerTouchObserver()
    fun unregisterTouchObserver()
    fun onSessionEnded()
    fun resetSession()
    fun navigateToEnterPinScreen()
}