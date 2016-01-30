package crawler2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

class StrUtil {

	private StrUtil() {
	}

	static String makeFullPath( String from, String to ) {
		try {
			return new URI( from ).resolve( to ).toString();
		}
		catch( Exception e ) {

		}
		return null;
	}

	static String getExtension( String path ) {
		try {
			path = new URI( path ).getPath();
		}
		catch( URISyntaxException e ) {
			return null;
		}

		try {
			if( path.endsWith( "/" ) )
				return null;

			String[] sp = path.split( "\\/" );
			path = sp[sp.length - 1];

			if( !path.contains( "." ) || path.endsWith( "." ) )
				return null;

			sp = path.split( "\\." );
			path = sp[sp.length - 1];
		}
		catch( ArrayIndexOutOfBoundsException | NullPointerException e ) {
			return null;
		}
		return path.equals( "" ) ? null : path;
	}

	static void saveToFile( File file, String s ) throws IOException {
		FileWriter w = new FileWriter( file );
		w.write( s );
		w.flush();
		w.close();
	}

	static boolean in( String s, String[] a ) {
		for( String as : a ) {
			if( s.equals( as ) )
				return true;
		}
		return false;
	}
}
