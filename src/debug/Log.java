package debug;

import java.text.DateFormat;
import java.util.Date;

public class Log {

	public static void v( final Class<?> cls, final String msg ) {
		System.out.println( format( cls, msg ) );
	}

	public static void e( final Class<?> cls, final String msg ) {
		System.err.println( format( cls, msg ) );
	}

	private static String getTime() {
		return DateFormat.getDateTimeInstance().format( new Date( System.currentTimeMillis() ) );
	}

	private static String format( final Class<?> cls, final String msg ) {
		return "[" + cls.getSimpleName() + " " + getTime() + "]\n" + msg + "\n";
	}
}
