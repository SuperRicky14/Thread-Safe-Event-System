package net.superricky.threadsafeeventsystem.examples

import net.superricky.threadsafeeventsystem.system.Event
import net.superricky.threadsafeeventsystem.system.EventBus
import net.superricky.threadsafeeventsystem.system.IEventListener

// This intermediate example shows a more object-oriented way of creating events.

// Create our event, except this time we add a method into our event.
class MyOOEvent(val message: String, val printIterations: Int) : Event() {

    // A simple method to print our message x amount of times, as defined in the printIterations variable.
    // It doesn't have to be called handleSelf, Events are just classes, you can add whatever variables or methods you want into here!
    fun handleSelf() {
        repeat(printIterations) {
            println(message)
        }
    }
}

class MyOOEventListener : IEventListener<MyOOEvent> {
    // Same as above, we override our onEvent function here, except this time we call the handleSelf() method in the event dispatched to this listener.
    override suspend fun onEvent(event: MyOOEvent) {
        event.handleSelf()
    }
}

fun main() {
    println("Thread Safe Event System Intermediate Example.")

    println("Creating MyOOEventListener")
    val myOOEventListener = MyOOEventListener()

    println("Registering MyOOEventListener")
    EventBus.register(MyOOEvent::class.java, myOOEventListener) // Exactly the same as in the beginner example

    println("Dispatching MyOOEvent!")
    val myOOEvent = MyOOEvent("Hello, World!", 5)
    EventBus.dispatch(myOOEvent) // Again, the same as in the beginner example

    println("End of program.")
}