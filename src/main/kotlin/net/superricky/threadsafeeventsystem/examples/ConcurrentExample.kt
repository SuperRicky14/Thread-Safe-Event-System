package net.superricky.threadsafeeventsystem.examples

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.superricky.threadsafeeventsystem.system.Event
import net.superricky.threadsafeeventsystem.system.EventBusSingleton
import net.superricky.threadsafeeventsystem.system.EventListener

class MyConcurrentEvent(val message: String) : Event()

class MyConcurrentEventListener() : EventListener<MyConcurrentEvent> {
    override suspend fun onEvent(event: MyConcurrentEvent) {
        println("Hello, World")
        delay(1000)
        println("I'm Alive!")
    }

    // You can also make any EventListener auto-register themselves as soon as they are created, inside their constructor.
    // This does not work correctly with object {} singletons, as they lazy initialize, basically meaning they won't do this at startup, leaving you with a bunch of unregistered singleton objects sitting around wasting memory.
    init {
        println("Registering MyConcurrentEventListener!")
        EventBusSingleton.register(MyConcurrentEvent::class.java, this)
    }
}

// Make main runBlocking to allow us to use delays.
fun main() = runBlocking {
    println("Thread Safe Event System Concurrent Example")

    println("Creating MyConcurrentEventListener!")
    val myConcurrentEventListener = MyConcurrentEventListener()

    // We already auto-registered our concurrentEventListener here, so we don't need to worry about that.

    println("This should run before the \"Hello, World!\", message, and the \"I'm Alive!\" message.")
    delay(250)
    EventBusSingleton.dispatch(MyConcurrentEvent("Hello, World!")) // Create a MyConcurrentEvent and dispatch it, we don't need a reference to it anymore.
    delay(500)
    println("This should print inbetween both messages.")
    delay(1000)
    println("This should print after both messages have finished.")
}