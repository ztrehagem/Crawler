package crawler;

import debug.Log;
import net.htmlparser.jericho.MasonTagTypes;
import net.htmlparser.jericho.MicrosoftConditionalCommentTagTypes;
import net.htmlparser.jericho.PHPTagTypes;

public class Main {

	public static void main( String[] args ) {
		String url = "http://megahertz.michikusa.jp";
		if( args.length == 0 ) {
			Log.e( Main.class, "using default url '" + url + "'" );
		}
		else {
			url = args[0];
		}

		if( !url.contains( ":" ) ) {
			url = "file:" + url;
		}
		if( !url.endsWith( "/" ) ) {
			url = url + "/";
		}

		initialize_Jericho();

		new Master( url );
	}

	private static void initialize_Jericho() {
		MicrosoftConditionalCommentTagTypes.register();
		PHPTagTypes.register();
		PHPTagTypes.PHP_SHORT.deregister();
		MasonTagTypes.register();
	}

}
