package exp;

import java.net.URI;
import crawler.Log;

public class ExpURI {

	public static void main( String[] args ) throws Exception {
		String s = "//example.com/#a?query=123";

		URI uri = new URI( s );

		Log.v( ExpURI.class, uri.getPath() );
	}

}
