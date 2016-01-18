package crawler;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import debug.Log;

public class Master {
	// Master -> Page

	private final File		root;
	private final String	url;
	private final int		h;

	private final Map<String, Integer> pagemap;

	private int pageIdCounter = 0;

	public Master( String url, int h ) throws MalformedURLException {
		this.root = new File( "result/" + String.valueOf( System.currentTimeMillis() ) );
		this.root.mkdirs();
		Log.v( getClass(), "dir '" + this.root.getAbsolutePath() + "'" );

		this.url = url;
		Log.v( getClass(), "target '" + url + "'" );

		this.h = h;

		this.pagemap = new HashMap<>();
	}

	public void process() throws MalformedURLException, InterruptedException {
		Log.v( getClass(), "start" );
		Thread t = new Thread( new Page( this, url, ++pageIdCounter, h - 1 ) );
		t.start();
		t.join();
		Log.v( Main.class, "done!" );
	}

	File getRootDir() {
		return this.root;
	}

	synchronized PageInfo addPageList( String url ) {
		if( pagemap.containsKey( url ) ) {
			return new PageInfo( pagemap.get( url ), true );
		}

		pagemap.put( url, ++pageIdCounter );
		return new PageInfo( pageIdCounter, false );
	}
}
