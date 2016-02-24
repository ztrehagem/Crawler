package crawler2;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

class ThreadObserver {

	private final Brain						brain;
	private final ExecutorService			exe;
	private final BlockingQueue<Future<?>>	q;
	private final ResultHolder				result;

	ThreadObserver( Brain brain ) {
		this.brain = brain;
		exe = Executors.newFixedThreadPool( brain.connectionNum );
		q = new LinkedBlockingQueue<>();
		this.result = new ResultHolder( brain );
	}

	void offer( Callable<?> c ) {
		result.found();

		try {
			addQ( exe.submit( c ) );
		}
		catch( Exception e ) {
			brain.log.e( getClass(), "failed submit to executor" );
		}
	}

	private void addQ( Future<?> f ) {
		result.submitted();

		if( q.offer( f ) ) {
			result.offered();
		}
	}

	void await() {
		Consumer<Future<?>> consumer = new QConsumer( brain, result, q );

		while( !q.isEmpty() ) {
			q.forEach( consumer );

			if( brain.printDebugLog )
				brain.log.d( getClass(), "Q size : " + q.size() );

			try {
				Thread.sleep( 1000 ); // これが終了しないとプログラムが終了しない
			}
			catch( InterruptedException e ) {
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

		this.result.print();
	}

	private class QConsumer implements Consumer<Future<?>> {

		private final Brain				brain;
		private final ResultHolder		result;
		private final Queue<Future<?>>	q;

		QConsumer( Brain brain, ResultHolder result, Queue<Future<?>> q ) {
			this.brain = brain;
			this.result = result;
			this.q = q;
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
}
