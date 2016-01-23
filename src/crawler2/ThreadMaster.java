package crawler2;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import debug.Log;

class ThreadMaster {

	private final ExecutorService			exe;
	private final BlockingQueue<Future<?>>	q;

	public ThreadMaster() {
		exe = Executors.newFixedThreadPool( 16 );
		q = new ArrayBlockingQueue<>( Short.MAX_VALUE );
	}

	public void exec( Callable<?> c ) {
		offer( exe.submit( c ) );
	}

	public void exec( Runnable r ) {
		offer( exe.submit( r ) );
	}

	private void offer( Future<?> f ) {
		try {
			q.put( f );
		}
		catch( InterruptedException e ) {
			Log.e( getClass(), "InterruptedException in offer : " + e );
		}
	}

	public void awaitEmpty() {
		Future<?> f;
		while( (f = q.poll()) != null ) {
			try {
				f.get();
			}
			catch( InterruptedException e ) {
				Log.e( getClass(), "Thread is Interrupted : " + e );
			}
			catch( ExecutionException e ) {
				Log.e( getClass(), "Exception from Thread : " + e );
				e.printStackTrace();
			}
		}
	}

	public boolean shutdown() {
		try {
			Thread.sleep( 100 );
		}
		catch( InterruptedException e ) {
			e.printStackTrace();
		}
		this.exe.shutdown();
		return this.exe.isTerminated();
	}
}
