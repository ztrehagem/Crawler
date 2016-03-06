package crawler2;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;

class Log {

	private final Brain brain;
	private PrintWriter logfile;

	Log( Brain brain ) {
		this.brain = brain;
	}

	public void d( Class<?> cls, String msg ) {
		if( brain.printDebugLog )
			print( System.out, format( "d", cls, msg ) );
	}

	public void i( Class<?> cls, String msg ) {
		print( System.out, format( "i", cls, msg ) );
	}

	public void e( Class<?> cls, String msg ) {
		print( System.err, format( "e", cls, msg ) );
	}

	private void print( PrintStream ps, String s ) {
		if( logfile != null )
			logfile.println( s );
		ps.println( s );
	}

	private String format( String mode, Class<?> cls, String msg ) {
		return "[" + mode + "/" + cls.getSimpleName() + " " + getTime() + "]" + System.lineSeparator() + msg + System.lineSeparator();
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
