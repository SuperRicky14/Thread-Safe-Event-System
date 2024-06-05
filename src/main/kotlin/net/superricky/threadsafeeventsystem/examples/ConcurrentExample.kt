package net.superricky.threadsafeeventsystem.examples

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.superricky.threadsafeeventsystem.system.Event
import net.superricky.threadsafeeventsystem.system.EventBus
import net.superricky.threadsafeeventsystem.system.IEventListener

class MyConcurrentEvent(val message: String) : Event()

val MyConcurrentEventListener = object : IEventListener<MyConcurrentEvent> {
    override suspend fun onEvent(event: MyConcurrentEvent) {
        println(event.message)
        delay(1000)
        println("I'm Alive!")
    }

    init {
        // println("Registering MyConcurrentEventListener!") Like in SuperAdvancedExample, this is commented out to prevent random messages in other tutorials.
        EventBus.register(MyConcurrentEvent::class.java, this)
    }
}

// Make main runBlocking to allow us to use delays.
fun main() = runBlocking {
    println("Thread Safe Event System Concurrent Example")

    // We already auto-registered our concurrentEventListener here, so we don't need to worry about that.

    println("This should run before the \"Hello, World!\", message, and the \"I'm Alive!\" message.")
    delay(250)
    EventBus.dispatch(MyConcurrentEvent("Hello, World!")) // Create a MyConcurrentEvent and dispatch it, we don't need a reference to it anymore.
    delay(500)
    println("This should print inbetween both messages.")
    delay(1000)
    println("This should print after both messages have finished.")
}