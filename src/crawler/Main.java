package crawler;

import crawler.one.Single;
import debug.Log;
import net.htmlparser.jericho.MasonTagTypes;
import net.htmlparser.jericho.MicrosoftConditionalCommentTagTypes;
import net.htmlparser.jericho.PHPTagTypes;

public class Main {

	public static void main( String[] args ) {
		String url = "http://megahertz.michikusa.jp/";
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
			//			url = url + "/";
		}

		initialize_Jericho();

		// -- thread --
		//		new Master( url );

		// -- single get --
		try {
			new Single( url ).process();
		}
		catch( Exception e ) {
			e.printStackTrace();
		}

		Log.v( Main.class, "done!" );
	}

	private static void initialize_Jericho() {
		MicrosoftConditionalCommentTagTypes.register();
		PHPTagTypes.register();
		PHPTagTypes.PHP_SHORT.deregister();
		MasonTagTypes.register();
	}

}
