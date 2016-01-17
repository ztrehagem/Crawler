package crawler;

import java.io.File;
import java.io.IOException;
import debug.Log;

public class Master {

	private final String	url;
	private final File		root;

	public Master( String url ) {
		this.url = url;
		Log.v( Master.class, "target url '" + url + "'" );

		root = Slave.rootDir = new File( "result/" + String.valueOf( System.currentTimeMillis() ) );
		root.mkdirs();
		Log.v( Master.class, "root dir '" + root.getAbsolutePath() + "'" );

		// --　ignition --
		File current;
		try {
			current = new File( root.getCanonicalPath() );
		}
		catch( IOException e ) {
			e.printStackTrace();
			return;
		}
		Thread th = new Thread( new Slave( url, current ) );
		th.start();

		// -- complete -- 
		try {
			th.join();
		}
		catch( InterruptedException e ) {
			e.printStackTrace();
		}
	}
}
