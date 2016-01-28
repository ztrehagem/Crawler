package crawler2;

import java.net.URI;
import java.net.URISyntaxException;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;

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

	static String getExtension( Element e ) {
		final String en = e.getName().toLowerCase();
		if( en.equals( HTMLElementName.LINK ) ) {
			final String rel = e.getAttributeValue( "rel" );
			if( rel != null && !rel.toLowerCase().equals( "stylesheet" ) )
				return null;
			else
				return "css";
		}
		if( en.equals( HTMLElementName.SCRIPT ) ) {
			final String type = e.getAttributeValue( "type" );
			if( type == null || type.toLowerCase().equals( "text/javascript" ) )
				return "js";
			else
				return null;
		}
		return null;
	}

	static String getExtension( Element e, String path ) {
		String result = getExtension( e );
		if( result == null )
			result = getExtension( path );
		return result;
	}
}
