package net.superricky.threadsafeeventsystem.examples

import net.superricky.threadsafeeventsystem.system.Event
import net.superricky.threadsafeeventsystem.system.EventBusSingleton
import net.superricky.threadsafeeventsystem.system.EventListener

// Create a new event, you can use whatever parameters you want, strings, primitive types, other classes, etc.
class MyEvent(val message: String) : Event()

// Create an Event Listener, this class will handle our MyEvent event.
class MyEventListener : EventListener<MyEvent> {
    // Override the onEvent function present in the EventListener interface, this will be called whenever an event is dispatched by the event bus.
    // This is a suspend function because our listener uses Kotlin coroutines, this is pretty much just to support delays and other features coroutines allow.
    // You don't have to use anything within coroutines.
    override suspend fun onEvent(event: MyEvent) {
        println(event.message) // Keep in mind that onEvent will be called asynchronously with the caller, likely on a completely different thread. The event system by itself is thread-safe, although it is up to you to ensure your application is thread safe.
    }
}

// Now we can finally use our defined Event and it's respective listener above!
fun main() {
    println("Thread Safe Event System Beginner Example.")

    println("Creating MyEventListener")
    val myEventListener = MyEventListener()

    println("Registering MyEventListener")

    /*
     * To register an event, we simply grab the EventBusSingleton and call the register method.
     * The register method needs two parameters:
     *     The java class of the event you want to register
     *     The object reference to the event listener you wish to bind it to
     * IMPORTANT: You MUST make sure that you specify the right event for the right event listener, otherwise you will likely get some pretty nasty errors related to type-safety.
     */
    EventBusSingleton.register(MyEvent::class.java, myEventListener)

    println("Dispatching MyEvent!")
    val myEvent1 = MyEvent("Hello, World!")

    // Pretty simple, just pass in the object reference of the Event you just made
    // The dispatch blocking method in the event bus, will still run all the listeners in parallel (see EventBus.kt#dispatchBlocking), but it will wait until they are all finished executing.
    // Essentially, if you want things to happen in order (i.e. some code -> dispatch to event listeners -> run more code), you use dispatchBlocking.
    // If you were in a real application, and you just want the event to be dispatched, not caring about the order it happens in, you would use dispatch.
    // Just calling dispatch will dispatch your event and then continue running whatever code you have on this thread.
    EventBusSingleton.dispatchBlocking(myEvent1)

    println("Dispatching another MyEvent!")
    val myEvent2 = MyEvent("Hello, Again!")
    EventBusSingleton.dispatchBlocking(myEvent2)

    println("Unregistering MyEventListener")
    /*
     * To unregister an event, the process is the same as registering, except we just call the unregister method instead.
     */
    EventBusSingleton.unregister(MyEvent::class.java, myEventListener)

    println("Dispatching another another MyEvent!")
    val myEvent3 = MyEvent("Hello Again, World!")

    // This should display nothing, as the event listener watching this event was unregistered, meaning no-one is listening to this event.
    // This is essentially the main benefit of event-driven architecture, as it serves as a good way to completely decouple code from each-other.
    // You can also notice how there were no errors here, because this method doesn't care what happens after the event is dispatched, we just send off the event and go about our day.
    EventBusSingleton.dispatchBlocking(myEvent3)

    println("End of program.")
}