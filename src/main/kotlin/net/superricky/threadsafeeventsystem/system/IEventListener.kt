package net.superricky.threadsafeeventsystem.system

interface IEventListener<T : Event> {
    suspend fun onEvent(event: T)
}