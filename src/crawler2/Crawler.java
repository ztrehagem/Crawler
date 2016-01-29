package crawler2;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import net.htmlparser.jericho.MasonTagTypes;
import net.htmlparser.jericho.MicrosoftConditionalCommentTagTypes;
import net.htmlparser.jericho.PHPTagTypes;

public class Crawler {

	public static int		ConnectionNumLimit	= 16;
	public static String	ResultDirectoryPath	= "./result";
	public static boolean	PrintLog			= false;

	final FileMaster		f;
	final HTMLMaster		h;
	final ThreadMaster		t;
	final File				root;

	private final String	url;
	private final int		level;

	public Crawler( String url, int level ) throws URISyntaxException, MalformedURLException, InterruptedException {
		initialize_Jericho();

		url = (url.contains( ":" ) ? "" : "file:") + url;
		url = url + (new URI( url ).getPath().startsWith( "/" ) ? "" : "/");

		this.url = url;
		this.level = level;

		this.root = new File( new File( ResultDirectoryPath ), String.valueOf( System.currentTimeMillis() ) + "-" + new URL( url ).getHost().replace( ':', '-' ).replace( '.', '-' ) );
		this.root.mkdirs();

		this.f = new FileMaster();
		this.h = new HTMLMaster();
		this.t = new ThreadMaster();
	}

	public void exec() throws InterruptedException {
		Log.open( root );

		Log.v( getClass(), "target '" + url + "'" );
		Log.v( getClass(), "root '" + this.root + "'" );

		Log.v( getClass(), "start" );

		h.makeStartID( url );
		t.offer( new HTMLSaveRunner( this, url, level ) );
		t.await();
		t.shutdown();

		Log.v( getClass(), "done!" );

		Log.close();
	}

	private static void initialize_Jericho() {
		MicrosoftConditionalCommentTagTypes.register();
		PHPTagTypes.register();
		PHPTagTypes.PHP_SHORT.deregister();
		MasonTagTypes.register();
	}
}
