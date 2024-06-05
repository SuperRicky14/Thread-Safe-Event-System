package net.superricky.threadsafeeventsystem.examples

import net.superricky.threadsafeeventsystem.system.Event
import net.superricky.threadsafeeventsystem.system.EventBus
import net.superricky.threadsafeeventsystem.system.IEventListener

// This tutorial is for when you want to have a global / static event listener.
// Since static classes cannot implement interfaces, we have to use something called the singleton design pattern here (It is also used internally in our EventBus file).

class MyAdvancedEvent(val message: String) : Event() // Nothing fancy here, just simple event declaration

// Literally just the standard event listener declaration, except it uses Kotlin's object {} way of creating singleton classes here.
object MySingletonEventListener : IEventListener<MyAdvancedEvent> {
    override suspend fun onEvent(event: MyAdvancedEvent) {
        println(event.message)
    }
}

fun main() {
    println("Thread Safe Event System Intermediate Example.")

    println("Registering MySingletonEventListener!")
    EventBus.register(MyAdvancedEvent::class.java, MySingletonEventListener) // Exactly the same as the other two examples, our Event Bus works out of the box with object {} declarations!

    println("Dispatching MyAdvancedEvent!")
    val myAdvancedEvent = MyAdvancedEvent("Hello, World!")
    EventBus.dispatchBlocking(myAdvancedEvent) // Again, the same as the other two examples.
    // You should now see that even though we haven't made any listeners, and haven't registered the listener in this file, that "Hello, World!" will still print.

    println("End of program.")
}