package net.superricky.threadsafeeventsystem.examples

import net.superricky.threadsafeeventsystem.system.Event
import net.superricky.threadsafeeventsystem.system.EventBus
import net.superricky.threadsafeeventsystem.system.IEventListener

class MySingletonEvent(val message: String) : Event()
class MySuperAdvancedEvent(val message: String) : Event()
class MySuperAdvancedTestEvent(val number: Int) : Event()

/**
 * Or, you can do a quick trick to make object singletons auto-register themselves!
 * The reason why we use val syntax here, instead of the standard:
 * object MyAdvancedSingletonEventListener : EventListener<MySingletonEvent> {}
 * is because we need to force something called eager-initialization here.
 * Normally, in Kotlin all object singletons are loaded lazily into memory, which means that we
 * need to access this class from somewhere, breaking the decoupled code that events give.
 * So instead, since our object is set in a val, we can still access it the same, except it will be loaded
 * on class construct, right before our main() function (or wherever your program's entry point) is run.
 */
@Suppress("UNUSED")
val MyAdvancedSingletonEventListener = object : IEventListener<MySingletonEvent> {
    override suspend fun onEvent(event: MySingletonEvent) {
        println("Handled in MyAdvancedSingletonEventListener! Message: ${event.message}")
    }

    /**
     * You can make your singleton object's auto-register themselves here!
     * Keep in mind that a limitation of Kotlin is that these singleton objects will need to be referenced at-least once in your program,
     * as unlike classes these do something called "lazy-initialization", where instead of running this init {} method on startup, they
     * will instead run this method when the class is first accessed.
     * This also works on classes, except instead you wouldn't do that eager-initialization trick as classes already eager-initialize.
     */
    init {
        //println("Auto-Registering MyAdvancedSingletonEventListener!") Commented out, so it doesn't interfere with the other examples
        EventBus.register(MySingletonEvent::class.java, this) // Auto-register ourselves with the event bus.
    }
}

/**
 * Here we do the same auto-registering tricks as above, except we inherit from more than one IEventListener!
 */
@Suppress("UNUSED")
val MySuperAdvancedEventListener = object : IEventListener<Event> {
    override suspend fun onEvent(event: Event) {
        when (event) {
            is MySingletonEvent -> {
                handleMySingletonEvent(event)
            }
            is MySuperAdvancedEvent -> {
                handleMySuperAdvancedEvent(event)
            }
            else -> {
                // Handle edge cases however you want here.
                println("-=WARNING=-")
                println("Received Event (class: ${event::class.java}) that was not supposed to be passed to our MySuperAdvancedEventListener class!")
                println("Something is wrong with your registration process!")
            }
        }
    }

    private fun handleMySingletonEvent(event: MySingletonEvent) {
        println("MySingletonEvent was handled inside MySuperAdvancedEventListener, Message: ${event.message}")
    }

    private fun handleMySuperAdvancedEvent(event: MySuperAdvancedEvent) {
        println("MySuperAdvancedEvent was handled inside MySuperAdvancedEventListener, Message: ${event.message}")
    }


    init {
        //println("Auto-Registering MyAdvancedSingletonEventListener!") Commented out, so it doesn't interfere with the other examples

        @Suppress("UNCHECKED_CAST")
        EventBus.register(MySingletonEvent::class.java, this as IEventListener<MySingletonEvent>)

        @Suppress("UNCHECKED_CAST")
        EventBus.register(MySuperAdvancedEvent::class.java, this as IEventListener<MySuperAdvancedEvent>)
    }
}

fun main() {
    // If you uncommented out those printlns earlier, you should see those Auto-Registering messages print before this message
    println("Thread Safe Event System Super-Advanced Example")

    // They were auto-registered, which means we don't have to worry about registering them at all!
    // All we need to do is create an instance of those events and dispatch them, and the EventBus and our Listeners defined earlier will handle them!

    println("Dispatching MySingletonEvent!")
    EventBus.dispatchBlocking(MySingletonEvent("Hello, World!"))

    println("Dispatching MySuperAdvancedEvent!")
    EventBus.dispatchBlocking(MySuperAdvancedEvent("Hello again, World!"))

    /*
     * You should see nothing printed between this dispatching message, and the "End of program." message
     * since we only registered the appropriate Events for our MySuperAdvancedTestEvent, so they shouldn't
     * even get called and trigger that warning message we defined in our when {} loop.
     */
    println("Dispatching MySuperAdvancedTestEvent!")
    EventBus.dispatchBlocking(MySuperAdvancedTestEvent(14))

    println("End of program.")
}