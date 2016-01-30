package crawler2;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;

class Log {

	private final Brain	brain;
	private PrintWriter	logfile;

	Log( Brain brain ) {
		this.brain = brain;
	}

	public void v( Class<?> cls, String msg ) {
		print( System.out, format( cls, msg ) );
	}

	public void e( Class<?> cls, String msg ) {
		print( System.err, format( cls, msg ) );
	}

	private void print( PrintStream ps, String s ) {
		if( logfile != null )
			logfile.println( s );
		if( brain.printLog )
			ps.println( s );
	}

	private String format( Class<?> cls, String msg ) {
		return "[" + cls.getSimpleName() + " " + getTime() + "]" + System.lineSeparator() + msg + System.lineSeparator();
	}

	private String getTime() {
		return DateFormat.getDateTimeInstance().format( new Date( System.currentTimeMillis() ) );
	}

	void open() throws IOException {
		if( logfile != null )
			return;
		final File file = new File( brain.root, "log.txt" );
		file.createNewFile();
		this.logfile = new PrintWriter( file );
	}

	void close() throws IOException {
		this.logfile.close();
		this.logfile = null;
	}
}
