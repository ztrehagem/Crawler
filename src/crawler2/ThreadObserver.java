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
		while( (f = q.poll()) != null ) {
			try {
				SaveRunnerResult r = f.get();
				this.success += 1;
				brain.log.v( getClass(), "saved '" + r.url + "' -> '" + r.filename + "'" );
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
		brain.log.v( getClass(), "shutdown..." );
		try {
			this.exe.awaitTermination( 30, TimeUnit.SECONDS );
		}
		catch( InterruptedException e ) {
			brain.log.e( getClass(), "Interrupted in awaitTermination" );
		}
		brain.log.v( getClass(), "    found : " + this.found + "\n   submit : " + this.submit + "\n    offer : " + this.offer + "\n complete : " + this.complete + "\n  success : " + this.success + "\n   failed : " + this.failed );
	}
}
