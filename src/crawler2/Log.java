package crawler2;

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
		if( Crawler.PrintLog )
			ps.println( format( cls, msg ) );
	}

	private static String format( final Class<?> cls, final String msg ) {
		return "[" + cls.getSimpleName() + " " + getTime() + "]\n" + msg + "\n";
	}

	private static String getTime() {
		return DateFormat.getDateTimeInstance().format( new Date( System.currentTimeMillis() ) );
	}
}
