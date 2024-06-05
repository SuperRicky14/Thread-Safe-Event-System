package net.superricky.threadsafeeventsystem.benchmarking

import net.superricky.threadsafeeventsystem.system.Event
import net.superricky.threadsafeeventsystem.system.IEventListener

class BenchmarkEvent() : Event()

class BenchmarkEventListener : IEventListener<BenchmarkEvent> {
    override suspend fun onEvent(event: BenchmarkEvent) {
        return // No code here, as we just want to test the performance of the Event system.
    }
}