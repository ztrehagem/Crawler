package crawler2;

class ResultHolder {

	private final Brain	brain;

	private int			find, submit, offer, complete, succeed, fail;

	ResultHolder( Brain brain ) {
		this.brain = brain;
	}

	void print() {
		final String endl = System.lineSeparator();
		brain.log.i( getClass(), "     find : " + this.find + endl + "   submit : " + this.submit + endl + "    offer : " + this.offer + endl + " complete : " + this.complete + endl + "  succeed : " + this.succeed + endl + "     fail : " + this.fail );
	}

	void found() {
		this.find++;
	}

	void submitted() {
		this.submit++;
	}

	void offered() {
		this.offer++;
	}

	void completed() {
		this.complete++;
	}

	void succeeded() {
		this.succeed++;
	}

	void failed() {
		this.fail++;
	}
}
