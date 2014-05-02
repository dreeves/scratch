package atcmit;

import lombok.*;
import java.util.*;
import static java.lang.Math.*;

import static atcmit.$.*;
import java.util.concurrent.*;

// like java.util.Timer, but strictly better
class Timer {

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// api //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

// units are seconds

// ?? daemons ??

static ExecutorService pool = Executors.newCachedThreadPool(
	new java.util.concurrent.ThreadFactory(){public Thread newThread(final Runnable v){
		Thread r = new Thread(new Runnable(){public void run(){
			try {println("hi"); v.run(); println("bye");}
			catch (Throwable e) {println("wut"); e.printStackTrace();}
			finally {println("fuck");}
		}});
		r.setDaemon(true); r.setName("Timer-"+rand(1000,10000)); return r;}});
//static void runIn(double seconds, Runnable v) {println("wut",seconds);timer.schedule(v,(long)(seconds*1000),java.util.concurrent.TimeUnit.MILLISECONDS);}
//static void runWithFixedRate(double seconds, Runnable v) {timer.scheduleAtFixedRate(v,0,(long)(seconds*1000),java.util.concurrent.TimeUnit.MILLISECONDS);}
//static void runWithFixedDelay(double seconds, Runnable v) {timer.scheduleWithFixedDelay(v,0,(long)(seconds*1000),java.util.concurrent.TimeUnit.MILLISECONDS);}


public static void main(String[] args) {Future v = pool.submit(new Runnable(){public void run(){println("yo"); fail("hy");}}); while (!v.isDone()) {println("bwu");} println("yay");}



static void runRepeat(final double period, final Runnable task) {
	runIn(0,new Runnable(){public void run(){double time = timeUnixEpoch(); task.run(); runAt(time+period,this);}});
	}
static void runRepeatAtFixedRate(final double period, final Runnable task) {
	new Runnable(){
		double time = timeUnixEpoch();
		public void run() {runAt(time,compose(task,this)); time += period;}
		}.run();
	}
static void runIn(double delay, Runnable task) {runAt(timeUnixEpoch()+delay,task);}
static void runAt(double time, Runnable task) {
	if (!impl.thread.isAlive()) throw new IllegalStateException("Timer already cancelled.");
	val t = new Task(); t.$ = task; t.time = time;
	synchronized(impl.lock) {impl.queue.add(t); if (impl.queue.peek() == t) impl.whatName.notify();}
	}

static void causeToBeDaemon() {impl.thread.setDaemon(true);}

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~// impl //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

static double timeUnixEpoch() {return System.currentTimeMillis()/1000.0;}
static Runnable compose(final Runnable a, final Runnable b) {return new Runnable(){public void run(){a.run(); b.run();}};}
static class Task {Runnable $; double time;}

static class impl {
	static PriorityQueue<Task> queue = new PriorityQueue<Task>(11,new Comparator<Task>(){public int compare(Task a, Task b){return (int)signum(a.time - b.time);}});
	static Object lock = new Object();
	static Object whatName = new Object();
	static Thread thread = new Thread(new Runnable(){public void run() {
		while (true) {
			try {
				Task task;
				double until;
				synchronized(lock) {
					// Wait for queue to become non-empty
					while (queue.isEmpty()) whatName.wait();

					// Queue nonempty; look at first evt and do the right thing
					task = queue.peek();
					until = task.time - timeUnixEpoch();
					if (until <= 0) {
						queue.remove();
						
					} else {
						whatName.wait((long)(until*1000));
					}
				}
				// TODO: run in separate thread, maybe threadpool?
				// hmmmm that separate thread might also need to be checking? so we don't have to wait for it to activate?
				if (until <= 0) task.$.run(); // Task fired; run it, holding no locks
			} catch(InterruptedException e) {
			}
		}
		}});
	static {
		thread.setName("Timer-"+rand(1000,10000));
		thread.start();}
}

}