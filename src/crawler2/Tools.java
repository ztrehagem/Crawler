package crawler2;

import java.net.URI;
import java.net.URISyntaxException;

class Tools {

	private Tools() {

	}

	static String makeFullPath( String from, String to ) {
		String result = null;
		try {
			result = new URI( from ).resolve( to ).toString();
		}
		catch( Exception e ) {

		}
		return result;
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
}
