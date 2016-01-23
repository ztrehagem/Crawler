package crawler2;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import debug.Log;

class ThreadMaster {

	private final ExecutorService	exe;
	private final Queue<Future<?>>	q;

	public ThreadMaster() {
		exe = Executors.newCachedThreadPool();
		q = new ArrayBlockingQueue<>( Byte.MAX_VALUE );
	}

	public void exec( Callable<?> c ) {
		if( !q.offer( exe.submit( c ) ) )
			Log.e( getClass(), "failed enqueue" );
	}

	public void exec( Runnable r ) {
		if( !q.offer( exe.submit( r ) ) )
			Log.e( getClass(), "failed enqueue" );
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
