package crawler2;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

class ThreadObserver implements Consumer<Future<?>> {

	private final Brain						brain;
	private final ExecutorService			exe;
	private final BlockingQueue<Future<?>>	q;
	private final ResultHolder				result;

	ThreadObserver( Brain brain ) {
		this.brain = brain;
		this.exe = Executors.newFixedThreadPool( brain.connectionNum );
		this.q = new LinkedBlockingQueue<>();
		//		this.q = new ArrayBlockingQueue<>( 4 );
		this.result = new ResultHolder( brain );
	}

	void offer( Callable<?> c ) {
		result.found();

		try {
			addQ( exe.submit( c ) );
		}
		catch( Exception e ) {
			brain.log.e( getClass(), "failed submit : " + e );
		}
	}

	private void addQ( Future<?> f ) {
		result.submitted();

		if( q.offer( f ) ) {
			result.offered();
		}
	}

	void await() {

		while( !q.isEmpty() ) {
			try {
				Thread.sleep( 1000 );
				// これが終了しないとプログラムが終了しない
				// 開始前のFutureについてもforEachしてしまう
			}
			catch( InterruptedException e ) {
			}

			q.forEach( this );

			if( brain.printDebugLog )
				brain.log.d( getClass(), "Q size : " + q.size() );
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

		this.result.print();
	}

	@Override
	public void accept( Future<?> t ) {
		if( t.isDone() ) {
			q.remove( t );

			try {
				t.get();
				result.succeeded();
			}
			catch( InterruptedException e ) {
				result.failed();
				brain.log.e( getClass(), "Interrupted : " + e );
			}
			catch( ExecutionException e ) {
				result.failed();
				brain.log.e( getClass(), "failed : " + e.getCause() );
			}
			finally {
				result.completed();
			}
		}
	}

}
