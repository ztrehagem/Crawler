package crawler2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.Date;

class Log {

	private final Brain	brain;
	private FileWriter	w;

	Log( Brain brain ) {
		this.brain = brain;
	}

	public void v( final Class<?> cls, final String msg ) {
		print( System.out, cls, msg );
	}

	public void e( final Class<?> cls, final String msg ) {
		print( System.err, cls, msg );
	}

	private void print( final PrintStream ps, final Class<?> cls, final String msg ) {
		final String s = format( cls, msg );
		if( w != null ) {
			try {
				w.write( s );
				w.write( '\n' );
				w.flush();
			}
			catch( IOException e ) {
			}
		}
		if( brain.printLog )
			ps.println( s );
	}

	private String format( final Class<?> cls, final String msg ) {
		return "[" + cls.getSimpleName() + " " + getTime() + "]\n" + msg + "\n";
	}

	private String getTime() {
		return DateFormat.getDateTimeInstance().format( new Date( System.currentTimeMillis() ) );
	}

	void open() throws IOException {
		if( w != null )
			return;
		final File file = new File( brain.root, "log.txt" );
		file.createNewFile();
		this.w = new FileWriter( file );
	}

	void close() throws IOException {
		this.w.close();
		this.w = null;
	}
}
