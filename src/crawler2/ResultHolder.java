package crawler2;

class ResultHolder {

	private final Brain brain;

	private int find, submit, execute, complete, succeed, fail;

	ResultHolder( Brain brain ) {
		this.brain = brain;
	}

	void print() {
		final String endl = System.lineSeparator();
		brain.log.i( getClass(), "     find : " + this.find + endl + "   submit : " + this.submit + endl + "  execute : " + this.execute + endl + " complete : " + this.complete + endl + "  succeed : " + this.succeed + endl + "     fail : " + this.fail );
	}

	synchronized void found() {
		this.find++;
	}

	synchronized void submitted() {
		this.submit++;
	}

	synchronized void executed() {
		this.execute++;
	}

	synchronized void completed() {
		this.complete++;
	}

	synchronized void succeeded() {
		this.succeed++;
	}

	synchronized void failed() {
		this.fail++;
	}

	int getSubmitted() {
		return submit;
	}

	int getExecuted() {
		return execute;
	}

	int getCompleted() {
		return complete;
	}
}
