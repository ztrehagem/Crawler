package crawler2;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class ThreadMaster {

	private final ExecutorService			exe;
	private final BlockingQueue<Future<?>>	q;

	ThreadMaster() {
		exe = Executors.newFixedThreadPool( Crawler.ConnectionNumLimit );
		q = new ArrayBlockingQueue<>( Short.MAX_VALUE );
	}

	void offer( Callable<?> c ) {
		addQ( exe.submit( c ) );
	}

	void offer( Runnable r ) {
		addQ( exe.submit( r ) );
	}

	private void addQ( Future<?> f ) {
		try {
			q.put( f );
		}
		catch( InterruptedException e ) {
			Log.e( getClass(), "InterruptedException in offer : " + e );
		}
	}

	void await() {
		Future<?> f;
		while( (f = q.poll()) != null ) {
			try {
				f.get();
			}
			catch( InterruptedException e ) {
				Log.e( getClass(), "Interrupted : " + e );
			}
			catch( ExecutionException e ) {
				Log.e( getClass(), "Exception : " + e );
				e.printStackTrace();
			}
		}
	}

	void shutdown() {
		this.exe.shutdown();
	}
}
