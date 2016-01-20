package crawler2;

import java.net.URI;
import debug.Log;
import net.htmlparser.jericho.MasonTagTypes;
import net.htmlparser.jericho.MicrosoftConditionalCommentTagTypes;
import net.htmlparser.jericho.PHPTagTypes;

public class Main {

	public static void main( String[] args ) throws Exception {
		String url = "http://www.apple.com/";
		int h = 1;

		if( args.length == 0 ) {
			Log.e( Main.class, "using default '" + h + " " + url + "'" );
		}
		else if( args.length == 1 ) {
			h = Integer.parseInt( args[0] );
			Log.e( Main.class, "using default url '" + url + "'" );
		}
		else if( args.length >= 2 ) {
			url = args[1];
		}

		if( !url.contains( ":" ) ) {
			url = "file:" + url;
		}

		url = url + (new URI( url ).getPath().startsWith( "/" ) ? "" : "/");

		initialize_Jericho();

		new Master( url, h );
	}

	private static void initialize_Jericho() {
		MicrosoftConditionalCommentTagTypes.register();
		PHPTagTypes.register();
		PHPTagTypes.PHP_SHORT.deregister();
		MasonTagTypes.register();
	}

}
