package crawler2;

import java.util.concurrent.TimeUnit;
import crawler2.MyThreadPoolExecutor.AfterExecuteListener;
import crawler2.MyThreadPoolExecutor.BeforeExecuteListener;

class ThreadObserver implements BeforeExecuteListener, AfterExecuteListener {

	private final Brain					brain;
	private final MyThreadPoolExecutor	exe;
	private final ResultHolder			result;
	private final Object				mtx;

	ThreadObserver( Brain brain ) {
		this.brain = brain;
		this.exe = new MyThreadPoolExecutor( brain.connectionNum );
		this.result = new ResultHolder( brain );
		this.mtx = new Object();

		this.exe.setBeforeExecuteListener( this );
		this.exe.setAfterExecuteListener( this );
	}

	void offer( Runnable r ) {
		result.found();

		try {
			exe.submit( r );
			result.submitted();
		}
		catch( Exception e ) {
			brain.log.e( getClass(), "failed submit : " + e );
		}
	}

	void awaitComplete() {
		try {
			synchronized( mtx ) {
				mtx.wait();
			}
		}
		catch( InterruptedException e ) {
			brain.log.e( getClass(), "Interrupted in await" );
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
	public void beforeExecute() {
		this.result.executed();
	}

	@Override
	public void afterExecute( Throwable t ) {
		this.result.completed();

		if( t == null ) {
			this.result.succeeded();
		}
		else {
			this.result.failed();
		}

		final int completed = this.result.getCompleted();
		final int executed = this.result.getExecuted();
		final int submitted = this.result.getSubmitted();

		brain.log.i( getClass(), "" + completed + " / " + executed + " / " + submitted );

		if( completed >= submitted ) {
			synchronized( mtx ) {
				mtx.notifyAll();
			}
		}
	}
}
