package crawler;

import java.net.URI;
import debug.Log;
import net.htmlparser.jericho.MasonTagTypes;
import net.htmlparser.jericho.MicrosoftConditionalCommentTagTypes;
import net.htmlparser.jericho.PHPTagTypes;

public class Main {

	public static void main( String[] args ) throws Exception {
		//		String url = "http://megahertz.michikusa.jp/";
		//		String url = "http://www.ise.shibaura-it.ac.jp/";
		//		String url = "http://www.shibaura-it.ac.jp/";
		//		String url = "http://www.apple.com/";
		//		String url = "http://www.m3net.jp/";
		String url = "http://dengekibunko.jp/newreleases/978-4-04-865133-2/";
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

		new Master( url, h ).process();

		//		new Single( url ).process();
	}

	private static void initialize_Jericho() {
		MicrosoftConditionalCommentTagTypes.register();
		PHPTagTypes.register();
		PHPTagTypes.PHP_SHORT.deregister();
		MasonTagTypes.register();
	}

}
