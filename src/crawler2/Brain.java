package crawler2;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

class Brain {

	final HTMLMaster	h;
	final FileMaster	f;
	final ThreadManager	t;
	final File			root;
	final Log			log;
	final int			connectionNum;
	final boolean		printLog;

	Brain( String url, String rootpath, int connectionNum, boolean printLog ) throws MalformedURLException {
		this.connectionNum = connectionNum;
		this.printLog = printLog;

		this.root = new File( new File( rootpath ), String.valueOf( System.currentTimeMillis() ) + "-" + new URL( url ).getHost().replace( ':', '-' ).replace( '.', '-' ) );
		this.root.mkdirs();
		this.log = new Log( this );

		this.h = new HTMLMaster();
		this.f = new FileMaster();
		this.t = new ThreadManager( this );
	}
}
