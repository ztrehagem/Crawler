package crawler2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.Date;

class Log {

	private Log() {

	}

	public static void v( final Class<?> cls, final String msg ) {
		print( System.out, cls, msg );
	}

	public static void e( final Class<?> cls, final String msg ) {
		print( System.err, cls, msg );
	}

	private static void print( final PrintStream ps, final Class<?> cls, final String msg ) {
		final String s = format( cls, msg );
		if( w != null ) {
			try {
				w.write( s );
				w.flush();
			}
			catch( IOException e ) {
			}
		}
		if( Crawler.PrintLog )
			ps.println( s );
	}

	private static String format( final Class<?> cls, final String msg ) {
		return "[" + cls.getSimpleName() + " " + getTime() + "]\n" + msg + "\n";
	}

	private static String getTime() {
		return DateFormat.getDateTimeInstance().format( new Date( System.currentTimeMillis() ) );
	}

	private static FileWriter w;

	public static void open( File root ) {
		if( w != null )
			return;

		final File file = new File( root, "log.txt" );
		try {
			file.createNewFile();
			w = new FileWriter( file );
		}
		catch( IOException e ) {
			Log.v( Log.class, "cant create log file" );
		}
	}

	public static void close() {
		if( w != null ) {
			try {
				w.close();
			}
			catch( IOException e ) {
				e.printStackTrace();
			}
			w = null;
		}
	}
}
