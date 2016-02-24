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

	private int								found, submit, offer, complete;

	private final Brain						brain;
	private final ExecutorService			exe;
	private final BlockingQueue<Future<?>>	q;

	ThreadObserver( Brain brain ) {
		this.brain = brain;
		exe = Executors.newFixedThreadPool( brain.connectionNum );
		q = new LinkedBlockingQueue<>();
		found = submit = offer = complete = 0;
	}

	void offer( Callable<?> c ) {
		this.found += 1;

		try {
			addQ( exe.submit( c ) );
		}
		catch( Exception e ) {
			brain.log.e( getClass(), "failed submit to executor" );
		}
	}

	void offer( Runnable r ) {
		this.found += 1;

		try {
			addQ( exe.submit( r ) );
		}
		catch( Exception e ) {
			brain.log.e( getClass(), "failed submit to executor" );
		}
	}

	synchronized private void addQ( Future<?> f ) {
		this.submit += 1;

		if( q.offer( f ) ) {
			this.offer += 1;
		}
	}

	void await() {
		Future<?> f;
		while( (f = q.poll()) != null ) {
			try {
				f.get();
			}
			catch( InterruptedException e ) {
				brain.log.e( getClass(), "Interrupted : " + e );
			}
			catch( ExecutionException e ) {
				brain.log.e( getClass(), "Exception : " + e );
				e.printStackTrace();
			}
			finally {
				this.complete += 1;
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
		brain.log.v( getClass(), "   found : " + this.found + "\n" + "  submit : " + this.submit + "\n" + "   offer : " + this.offer + "\n" + "complete : " + this.complete );
	}
}
