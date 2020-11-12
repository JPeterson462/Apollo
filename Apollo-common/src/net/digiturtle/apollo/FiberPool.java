package net.digiturtle.apollo;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.jetlang.core.Disposable;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.ThreadFiber;

public class FiberPool {
	
	private Fiber[] fibers;
	private int index;
	private ArrayList<Disposable> tasks;
	
	public FiberPool (int numFibers) {
		fibers = new Fiber[numFibers];
		index = 0;
		tasks = new ArrayList<>();
	}
	
	public synchronized void scheduleTask (int msInterval, Runnable runnable) {
		if (fibers[index] != null) {
			fibers[index].dispose();
		}
		fibers[index] = new ThreadFiber();
		Fiber fiber = fibers[index];
		index++;
		fiber.start();
		Disposable task = fiber.scheduleAtFixedRate(runnable, 0, msInterval, TimeUnit.MILLISECONDS);
		tasks.add(task);
	}
	
	public void stopTask (int index) {
		fibers[index].dispose();
	}
	
	public synchronized void scheduleTask (Runnable runnable) {
		fibers[index] = new ThreadFiber();
		Fiber fiber = fibers[index];
		index++;
		fiber.start();
		fiber.execute(runnable);
	}

}
