package crawler2;

import java.net.URI;
import java.net.URISyntaxException;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;

class Tools {

	private Tools() {

	}

	public static String makeFullPath( String from, String to ) {
		String result = null;
		try {
			result = new URI( from ).resolve( to ).toString();
		}
		catch( URISyntaxException e ) {

		}
		return result;
	}

	public static String getExtension( String path ) {
		String[] sp;
		String ext;
		try {
			//			Log.v( Tools.class, "getExtension (original) '" + path + "'" );
			sp = path.split( "\\/" );
			ext = sp[sp.length - 1];
			//			Log.v( Tools.class, "getExtension (/) '" + ext + "'" );
			sp = path.split( "\\#" );
			ext = sp[0];
			//			Log.v( Tools.class, "getExtension (#) '" + ext + "'" );
			sp = path.split( "\\?" );
			ext = sp[0];
			//			Log.v( Tools.class, "getExtension (?) '" + ext + "'" );
			sp = path.split( "\\." );
			ext = sp[sp.length - 1];
			//			Log.v( Tools.class, "getExtension (.) '" + ext + "'" );
		}
		catch( ArrayIndexOutOfBoundsException e ) {
			return null;
		}
		return ext.equals( "" ) ? null : ext;
	}

	public static String getExtension( Element e ) {
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

	public static boolean isPage( String href ) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}
}
