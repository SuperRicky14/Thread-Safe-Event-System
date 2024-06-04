package net.superricky.threadsafeeventsystem.system

interface EventListener<T : Event> {
    suspend fun onEvent(event: T)
}