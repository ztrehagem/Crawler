package crawler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import debug.Log;
import net.htmlparser.jericho.Source;

public class Slave implements Runnable {

	static File				rootDir;
	private final File		cur;
	private final String	url;

	public Slave( String url, File currentDir ) {
		this.url = url;
		this.cur = currentDir;
	}

	@Override
	public void run() {

		Source src;

		try {
			src = new Source( new URL( url ) );
		}
		catch( IOException e ) {
			e.printStackTrace();
		}
	}

	private void testCreateFile() {
		File testFile = null;
		try {
			testFile = new File( cur.getCanonicalPath() + "/test.txt" );
			Log.v( getClass(), "test file path '" + testFile.getCanonicalPath() + "'" );
			testFile.createNewFile();
		}
		catch( IOException e ) {
			e.printStackTrace();
		}
	}

}
