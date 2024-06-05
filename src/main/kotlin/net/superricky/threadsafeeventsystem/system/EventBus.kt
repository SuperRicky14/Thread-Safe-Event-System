package net.superricky.threadsafeeventsystem.system

import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap

// Coroutine stuff
private val dispatcher = Dispatchers.Default
private val scope = CoroutineScope(dispatcher)

/**
 * We specify the type for our EventBus val to be IEventBus, preventing people from accessing the
 * object directly, and only allowing access through our interface. This allows our code to be further
 * decoupled and easier to maintain, and makes changes to the EventBus require less work.
 */
val EventBus: IEventBus = object : IEventBus {
    private val listeners = ConcurrentHashMap<Class<out Event>, MutableSet<IEventListener<out Event>>>()

    /**
     * Register a listener for a specific event type
     * This function took 0.00255s on average to register 50,000 events listeners
     * Time Complexity: O(1) (for hash map lookup) + O(1) (for adding item to hashset) = O(1)
     */
    override fun <T : Event> register(eventType: Class<T>, listener: IEventListener<T>) {
        listeners.computeIfAbsent(eventType) { ConcurrentHashMap.newKeySet() }.add(listener)
    }

    /**
     * Unregister a listener for a specific event type
     * This function took 0.00257s on average to unregister 50,000 event listeners
     * Time Complexity: O(1) (for hash map lookup) + O(1) (for removing item from hashset) = O(1)
     */
    override fun <T : Event> unregister(eventType: Class<T>, listener: IEventListener<T>) {
        listeners[eventType]?.remove(listener)
    }

    // Only used for benchmarking / debugging, this will not be shipped in production.
    override fun <T : Event> getRegisteredEventsOfType(eventType: Class<T>): MutableSet<IEventListener<out Event>>? {
        return listeners[eventType]
    }

    /**
     * Dispatch an event to all registered listeners
     * This function took 0.00015s on average to dispatch to 50,000 event listeners (not including any code the function may have run afterward)
     * Time Complexity: O(1) (for hash map lookup) + O(m / n) where n = numberOfAvaliableCores (for iterating over listeners) = O(m / n)
     * Best Case: O(1) if there was an equal amount of cores to the amount of listeners
     * Likely Case: O(m / n) where n = numberOfAvaliableCores
     * Worst Case: O(m) on a single-threaded system
     */
    override fun <T : Event> dispatch(event: T) = scope.launch {
        listeners[event::class.java]?.forEach { listener ->
            launch {
                @Suppress("UNCHECKED_CAST")
                (listener as IEventListener<T>).onEvent(event)
            }
        }
    }

    /**
     * Similar to the dispatch method above, except we block the thread this method was launched in until everything inside has finished executing.
     * Everything inside here will still actually run concurrently, we just block until everything is finished.
     * The actual time it takes depends on each individual function, for example if each onEvent takes 100ms to complete, this function will take a little bit over 100ms to complete.
     * If you had 50,000 event listeners all waiting for 1 second, this function will take 1 second to complete, as all of those coroutines delays will be run concurrently, and this function will just wait until all of those are done.
     *
     * Time Complexity:
     * O(1) for hashmap lookup +
     * O(m / n) where n = numberOfAvailableCores (for launching coroutines) +
     * O(t_max) where t_max is the longest time taken by an individual coroutine to complete.
     *
     * Best Case: O(1) if there was an equal amount of cores to the amount of listeners
     * Likely Case: O(m / n) where n = numberOfAvailableCores + O(t_max)
     * Worst Case: O(m) on a single-threaded system + O(t_max)
     */
    override fun <T : Event> dispatchBlocking(event: T) = runBlocking {
        scope.launch {
            listeners[event::class.java]?.map { listener ->
                launch {
                    @Suppress("UNCHECKED_CAST")
                    (listener as IEventListener<T>).onEvent(event)
                }
            }?.joinAll()
        }.join()
    }
}