package net.superricky.threadsafeeventsystem.benchmarking

import net.superricky.threadsafeeventsystem.system.EventBus
import net.superricky.threadsafeeventsystem.system.IEventListener

/**
 * This is just a demo benchmark of our thread safe kotlin event system, that you can run for yourself.
 */

suspend fun main() {
    val iterations = 100
    val eventListenerCount = 50_000
    var totalRegisterTime = 0.0
    var totalDispatchTime = 0.0
    var totalUnregisterTime = 0.0

    repeat(iterations) {
        println("Loading Thread Safe Event System!")
        println("Registering Listeners!")
        val startTimeRegister = System.currentTimeMillis()
        for (i in 1..eventListenerCount) {
            val tempListener = BenchmarkEventListener()
            EventBus.register(BenchmarkEvent::class.java, tempListener)
        }
        val registerTime = (System.currentTimeMillis() - startTimeRegister).toDouble() / 1000
        totalRegisterTime += registerTime
        println("Register Operation took: ${String.format("%.52f", registerTime).trimEnd('.', '0')}s")

        println("Dispatching Event!")
        val startTimeDispatch = System.currentTimeMillis()
        val testEvent = BenchmarkEvent()
        val dispatchJob = EventBus.dispatch(testEvent)
        val dispatchTime = (System.currentTimeMillis() - startTimeDispatch).toDouble() / 1000
        totalDispatchTime += dispatchTime
        dispatchJob.join()
        println("Dispatch Operation took: ${String.format("%.52f", dispatchTime).trimEnd('.', '0')}s")

        println("Unregistering all Listeners!")
        val startTimeUnregister = System.currentTimeMillis()
        EventBus.getRegisteredEventsOfType(BenchmarkEvent::class.java)?.forEach { eventListener ->
            EventBus.unregister(BenchmarkEvent::class.java, eventListener as IEventListener<BenchmarkEvent>)
        }
        val unregisterTime = (System.currentTimeMillis() - startTimeUnregister).toDouble() / 1000
        totalUnregisterTime += unregisterTime
        println("Unregister Operation took: ${String.format("%.52f", unregisterTime).trimEnd('.', '0')}s")

        println("Iteration ${it + 1} Finished!")
    }

    val averageRegisterTime = totalRegisterTime / iterations
    val averageDispatchTime = totalDispatchTime / iterations
    val averageUnregisterTime = totalUnregisterTime / iterations

    println("Average Register Operation time: ${String.format("%.52f", averageRegisterTime).trimEnd('.', '0')}s")
    println("Average Dispatch Operation time: ${String.format("%.52f", averageDispatchTime).trimEnd('.', '0')}s")
    println("Average Unregister Operation time: ${String.format("%.52f", averageUnregisterTime).trimEnd('.', '0')}s")
}