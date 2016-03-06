package crawler2;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

class Brain {

	final HTMLMaster h;
	final FileMaster f;
	final ThreadObserver t;
	final File root;
	final Log log;
	final int connectionNum;
	final boolean printDebugLog;

	Brain( String url, String rootpath, int connectionNum, boolean printDebugLog ) throws MalformedURLException {
		this.connectionNum = connectionNum;
		this.printDebugLog = printDebugLog;

		this.root = new File( new File( rootpath ), String.valueOf( System.currentTimeMillis() ) + "-" + new URL( url ).getHost().replace( ':', '-' ).replace( '.', '-' ) );
		this.root.mkdirs();
		this.log = new Log( this );

		this.h = new HTMLMaster();
		this.f = new FileMaster();
		this.t = new ThreadObserver( this );
	}
}
