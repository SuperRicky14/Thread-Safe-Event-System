# Thread Safe Event System
Thread Safe Event System is a (thread safe) Kotlin event system, featuring parallel execution of event listeners, versatile and flexible developer interface, working out of the box with extensive documentation, speed and reliability. It supports both blocking (not synchronous) and concurrent dispatching.

The "documentation" for TSES is pretty much all in the code, as it is a relatively simple codebase with a few examples I have added in to help you get working with this.

### Why did I make this?
Because one of my projects needs an event system for it's rewrite, but the project also runs asynchronously, and I just could not find a good alternative in Kotlin.
