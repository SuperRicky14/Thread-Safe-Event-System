package net.superricky.threadsafeeventsystem.examples

import net.superricky.threadsafeeventsystem.system.Event
import net.superricky.threadsafeeventsystem.system.EventBusSingleton
import net.superricky.threadsafeeventsystem.system.EventListener

// This tutorial is for when you want to have a global / static event listener.
// Since static classes cannot implement interfaces, we have to use something called the singleton design pattern here (It is also used internally in our EventBus file).

class MyAdvancedEvent(val message: String) : Event() // Nothing fancy here, just simple event declaration

// Literally just the standard event listener declaration, except it uses Kotlin's object {} way of creating singleton classes here.
object MyAdvancedSingletonEventListener : EventListener<MyAdvancedEvent> {
    override suspend fun onEvent(event: MyAdvancedEvent) {
        println(event.message)
    }

    /**
     * You can make your singleton object's auto-register themselves here!
     * Keep in mind that a limitation of Kotlin is that these singleton objects will need to be referenced at-least once in your program,
     * as unlike classes these do something called "lazy-initialization", where instead of running this init {} method on startup, they
     * will instead run this method when the class is first accessed.
     */
    init {
        println("Registering MyAdvancedSingletonEventListener!")
        EventBusSingleton.register(MyAdvancedEvent::class.java, MyAdvancedSingletonEventListener) // Exactly the same as the other two examples, our Event Bus works out of the box with object {} declarations!
    }
}

fun main() {
    println("Thread Safe Event System Intermediate Example.")

    /*
     * We reference MyAdvancedSingletonEventListener here, so that it's init {} method will be called.
     * Usually this auto-initialization works best with class event listeners that are instanced and created during runtime, multiple times.
     * Since the only way we could fix this would be to scan the process's memory, get all Event Listener singletons into a list and then
     * register them one by one.
     */
    MyAdvancedSingletonEventListener

    println("Dispatching MyAdvancedEvent!")
    val myAdvancedEvent = MyAdvancedEvent("Hello, World!")
    EventBusSingleton.dispatchBlocking(myAdvancedEvent) // Again, the same as the other two examples.
    // You should now see that even though we haven't made any listeners, and haven't registered the listener in this file, that "Hello, World!" will still print.

    println("End of program.")
}