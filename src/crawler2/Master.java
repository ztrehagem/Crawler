package crawler2;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import debug.Log;

public class Master {

	final FileMaster		f;
	final HTMLMaster		h;
	final ThreadMaster		t;
	final File				root;

	private final String	url;
	private final int		lmt;

	public Master( String url, int lmt ) throws URISyntaxException, MalformedURLException, InterruptedException {
		url = (url.contains( ":" ) ? "" : "file:") + url;
		url = url + (new URI( url ).getPath().startsWith( "/" ) ? "" : "/");

		this.url = url;
		this.lmt = lmt;

		this.root = new File( "result/" + String.valueOf( System.currentTimeMillis() ) + "-"
			+ new URL( url ).getHost().replace( ':', '-' ).replace( '.', '-' ) );
		this.root.mkdirs();

		this.f = new FileMaster();
		this.h = new HTMLMaster();
		this.t = new ThreadMaster();

		Log.v( getClass(), "target '" + url + "'" );
		Log.v( getClass(), "root '" + this.root + "'" );

		process();
	}

	private void process() throws InterruptedException {
		Log.v( getClass(), "start" );

		h.makeID( url, true );
		t.exec( new HTMLSaveRunner( this, url, lmt ) );
		t.awaitEmpty();
		if( !t.shutdown() ) {
			Log.e( getClass(), "thread pool shutdowned is not terminated" );
		}

		Log.v( getClass(), "done!" );
	}
}
