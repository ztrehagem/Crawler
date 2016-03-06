package crawler2;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class MyThreadPoolExecutor extends ThreadPoolExecutor {

	private BeforeExecuteListener before;
	private AfterExecuteListener after;

	MyThreadPoolExecutor( int size ) {
		super( size, size, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>() );
	}

	void setBeforeExecuteListener( BeforeExecuteListener l ) {
		this.before = l;
	}

	void setAfterExecuteListener( AfterExecuteListener l ) {
		this.after = l;
	}

	@Override
	protected void beforeExecute( Thread t, Runnable r ) {
		if( before != null )
			before.beforeExecute();
		super.beforeExecute( t, r );
	}

	@Override
	protected void afterExecute( Runnable r, Throwable t ) {
		if( after != null )
			after.afterExecute( t );
		super.afterExecute( r, t );
	}

	interface BeforeExecuteListener {

		void beforeExecute();
	}

	interface AfterExecuteListener {

		void afterExecute( Throwable t );
	}
}
