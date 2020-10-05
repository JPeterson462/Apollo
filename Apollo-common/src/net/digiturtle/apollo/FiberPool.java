package net.digiturtle.apollo;

import java.util.concurrent.TimeUnit;

import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.ThreadFiber;

public class FiberPool {
	
	private Fiber[] fibers;
	private int index;
	
	public FiberPool (int numFibers) {
		fibers = new Fiber[numFibers];
		index = 0;
	}
	
	public synchronized void scheduleTask (int msInterval, Runnable runnable) {
		fibers[index] = new ThreadFiber();
		Fiber fiber = fibers[index];
		index++;
		fiber.start();
		fiber.scheduleAtFixedRate(runnable, 0, msInterval, TimeUnit.MILLISECONDS);
	}
	
	public synchronized void scheduleTask (Runnable runnable) {
		fibers[index] = new ThreadFiber();
		Fiber fiber = fibers[index];
		index++;
		fiber.start();
		fiber.execute(runnable);
	}

}
