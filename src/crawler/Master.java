package crawler;

import java.io.File;
import debug.Log;

public class Master {

	private final String	url;
	private final File		root;

	public Master( String url ) {
		this.url = url;
		Log.v( Master.class, "target url '" + url + "'" );

		root = new File( "result/" + String.valueOf( System.currentTimeMillis() ) );
		root.mkdirs();
		Log.v( Master.class, "root dir '" + root.getAbsolutePath() + "'" );
	}
}
