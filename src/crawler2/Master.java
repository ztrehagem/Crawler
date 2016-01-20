package crawler2;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import debug.Log;

public class Master {

	final File	root;

	final URL	url;
	final int	h;

	public Master( String url, int h ) throws MalformedURLException {
		this.url = new URL( url );
		this.h = h;

		this.root = new File( "result/" + String.valueOf( System.currentTimeMillis() ) + "-"
			+ this.url.getHost().replace( ':', '-' ).replace( '.', '-' ) );
		this.root.mkdirs();

		Log.v( getClass(), "target '" + url + "'\nroot '" + root + "'" );

		process();
	}

	public void process() {
		Log.v( getClass(), "start" );

		Log.v( getClass(), "all done!" );
	}
}
