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

	private int												found, submit, offer, complete, success, failed;

	private final Brain										brain;
	private final ExecutorService							exe;
	private final BlockingQueue<Future<SaveRunnerResult>>	q;

	ThreadObserver( Brain brain ) {
		this.brain = brain;
		exe = Executors.newFixedThreadPool( brain.connectionNum );
		q = new LinkedBlockingQueue<>();
		found = submit = offer = complete = success = failed = 0;
	}

	void offer( Callable<SaveRunnerResult> c ) {
		this.found += 1;

		try {
			addQ( exe.submit( c ) );
		}
		catch( Exception e ) {
			brain.log.e( getClass(), "failed submit to executor" );
		}
	}

	synchronized private void addQ( Future<SaveRunnerResult> f ) {
		this.submit += 1;

		if( q.offer( f ) ) {
			this.offer += 1;
		}
	}

	void await() {
		Future<SaveRunnerResult> f;

		if( brain.printDebugLog ) {
			final Thread t = new Thread() {

				@Override
				public void run() {
					int size;
					do {
						try {
							Thread.sleep( 3000 );
						}
						catch( InterruptedException e ) {
						}
						size = q.size();
						brain.log.d( getClass(), "Q size : " + size );
					} while( size > 0 );
				}
			};
			t.start();
		}

		while( (f = q.poll()) != null ) {
			try {
				SaveRunnerResult r = f.get();
				this.success += 1;
				brain.log.i( getClass(), "saved '" + r.url + "' -> '" + r.filename + "'" );
			}
			catch( InterruptedException e ) {
				this.failed += 1;
				brain.log.e( getClass(), "Interrupted : " + e );
			}
			catch( ExecutionException e ) {
				this.failed += 1;
				brain.log.e( getClass(), "failed : " + e.getCause() );
			}
			finally {
				this.complete += 1;
			}
		}
	}

	void shutdown() {
		this.exe.shutdown();
		brain.log.i( getClass(), "shutdown..." );
		try {
			this.exe.awaitTermination( 30, TimeUnit.SECONDS );
		}
		catch( InterruptedException e ) {
			brain.log.e( getClass(), "Interrupted in awaitTermination" );
		}

		final String endl = System.lineSeparator();
		brain.log.i( getClass(), "    found : " + this.found + endl + "   submit : " + this.submit + endl + "    offer : " + this.offer + endl + " complete : " + this.complete + endl + "  success : " + this.success + endl + "   failed : " + this.failed );
	}
}
