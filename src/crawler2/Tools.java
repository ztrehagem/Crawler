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
		String[] sp;
		String ext = path;

		try {
			ext = new URI( ext ).getPath();
		}
		catch( URISyntaxException e ) {
			return null;
		}

		try {
			//			Log.v( Tools.class, "getExtension (original) '" + path + "'" );
			if( ext.endsWith( "/" ) )
				return null;
			sp = ext.split( "\\/" );
			ext = sp[sp.length - 1];
			//			Log.v( Tools.class, "getExtension (/) '" + ext + "'" );
			if( !ext.contains( "." ) )
				return null;
			sp = ext.split( "\\." );
			ext = sp[sp.length - 1];
			//			Log.v( Tools.class, "getExtension (.) '" + ext + "'" );
		}
		catch( ArrayIndexOutOfBoundsException | NullPointerException e ) {
			return null;
		}
		return ext.equals( "" ) ? null : ext;
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

	static boolean isPage( String href ) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}
}
