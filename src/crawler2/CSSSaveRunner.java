package crawler2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

class CSSSaveRunner implements Runnable {

	private final Crawler	master;
	private final String	url;
	private final File		file;

	CSSSaveRunner( final Crawler master, final String url ) {
		this.master = master;
		this.url = url;
		this.file = new File( master.root, master.f.getFileName( url ) );
	}

	@Override
	public void run() {

		try {
			file.createNewFile();

			FileWriter w = new FileWriter( file );
			BufferedReader r = new BufferedReader( new InputStreamReader( new URL( url ).openStream() ) );

			String line;
			while( (line = r.readLine()) != null )
				w.write( Tools.CSSRefactoring( master, url, line ) + '\n' );

			r.close();
			w.close();
		}
		catch( IOException e ) {
			Log.e( getClass(), "failed : " + e );
			return;
		}

		Log.v( getClass(), "saved '" + url + "' -> '" + file + "'" );
	}

}
