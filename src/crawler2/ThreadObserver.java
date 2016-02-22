package crawler2;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

class ThreadObserver {

	private int								add		= 0;
	private int								remove	= 0;

	private final Brain						brain;
	private final ExecutorService			exe;
	private final BlockingQueue<Future<?>>	q;

	ThreadObserver( Brain brain ) {
		this.brain = brain;
		exe = Executors.newFixedThreadPool( brain.connectionNum );
		q = new LinkedBlockingQueue<>();
	}

	void offer( Callable<?> c ) {
		addQ( exe.submit( c ) );
	}

	void offer( Runnable r ) {
		addQ( exe.submit( r ) );
	}

	synchronized private void addQ( Future<?> f ) {
		try {
			q.put( f );
			this.add += 1;
		}
		catch( InterruptedException e ) {
			brain.log.e( getClass(), "Interrupted in addQ" );
		}
	}

	void await() {
		Future<?> f;
		while( (f = q.poll()) != null ) {
			try {
				f.get();
				this.remove += 1;
			}
			catch( InterruptedException e ) {
				brain.log.e( getClass(), "Interrupted : " + e );
			}
			catch( ExecutionException e ) {
				brain.log.e( getClass(), "Exception : " + e );
				e.printStackTrace();
			}
		}
	}

	void shutdown() {
		this.exe.shutdown();
		brain.log.v( getClass(), "shutdown..." );
		try {
			this.exe.awaitTermination( 30, TimeUnit.SECONDS );
		}
		catch( InterruptedException e ) {
			brain.log.e( getClass(), "Interrupted in awaitTermination" );
		}
		brain.log.v( getClass(), "created : " + this.add + "\ncompleted : " + this.remove );
	}
}
