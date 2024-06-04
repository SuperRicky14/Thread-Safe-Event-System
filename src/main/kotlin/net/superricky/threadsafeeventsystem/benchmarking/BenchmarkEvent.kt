package net.superricky.threadsafeeventsystem.benchmarking

import kotlinx.coroutines.delay
import net.superricky.threadsafeeventsystem.system.Event
import net.superricky.threadsafeeventsystem.system.EventListener

class BenchmarkEvent() : Event()

class BenchmarkEventListener : EventListener<BenchmarkEvent> {
    override suspend fun onEvent(event: BenchmarkEvent) {
        return // No code here, as we just want to test the performance of the Event system.
    }
}