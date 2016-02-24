package crawler2;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import net.htmlparser.jericho.MasonTagTypes;
import net.htmlparser.jericho.MicrosoftConditionalCommentTagTypes;
import net.htmlparser.jericho.PHPTagTypes;

public class Crawler {

	private final Brain				brain;
	private final String			url;
	private final int				level;

	private static final String		default_rootDirPath		= "./result";
	private static final int		default_connectionNum	= 16;
	private static final boolean	default_printDebugLog		= false;

	public Crawler( String url, int level ) throws MalformedURLException, URISyntaxException, InterruptedException {
		this( url, level, default_rootDirPath, default_connectionNum, default_printDebugLog );
	}

	public Crawler( String url, int level, boolean printDebugLog ) throws MalformedURLException, URISyntaxException, InterruptedException {
		this( url, level, default_rootDirPath, default_connectionNum, printDebugLog );
	}

	public Crawler( String url, int level, int connectionNum, boolean printLog ) throws MalformedURLException, URISyntaxException, InterruptedException {
		this( url, level, default_rootDirPath, connectionNum, printLog );
	}

	public Crawler( String url, int level, String rootDirPath, int connectionNum, boolean printLog ) throws URISyntaxException, MalformedURLException, InterruptedException {
		initialize_Jericho();

		url = (url.contains( ":" ) ? "" : "file:") + url;
		url = url + (new URI( url ).getPath().startsWith( "/" ) ? "" : "/");

		this.url = url;
		this.level = level;

		this.brain = new Brain( url, rootDirPath, connectionNum, printLog );
	}

	public void exec() throws IOException {

		brain.log.open();

		brain.log.i( getClass(), "target '" + url + "'" );
		brain.log.i( getClass(), "root '" + brain.root + "'" );

		brain.log.i( getClass(), "start" );

		try {
			brain.h.makeStartID( url );
			brain.t.offer( new HTMLSaveRunner( brain, url, level ) );
			brain.t.await();
		}
		catch( Exception e ) {
			brain.log.e( getClass(), "Exception in Crawler" );
		}
		finally {
			brain.t.shutdown();
		}

		brain.log.i( getClass(), "done!" );

		brain.log.close();
	}

	private static void initialize_Jericho() {
		MicrosoftConditionalCommentTagTypes.register();
		PHPTagTypes.register();
		PHPTagTypes.PHP_SHORT.deregister();
		MasonTagTypes.register();
	}
}
