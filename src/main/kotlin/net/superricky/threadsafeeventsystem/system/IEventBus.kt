package net.superricky.threadsafeeventsystem.system

import kotlinx.coroutines.Job

/**
 * We use an interface for our EventBus, instead of a direct reference to the EventBus to make our code more decoupled.
 */
interface IEventBus {
    fun <T : Event> register(eventType: Class<T>, listener: IEventListener<T>)
    fun <T : Event> unregister(eventType: Class<T>, listener: IEventListener<T>)
    fun <T : Event> getRegisteredEventsOfType(eventType: Class<T>): MutableSet<IEventListener<out Event>>?
    fun <T : Event> dispatch(event: T): Job
    fun <T : Event> dispatchBlocking(event: T): Unit?
}