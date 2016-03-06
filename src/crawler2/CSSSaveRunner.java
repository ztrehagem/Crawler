package crawler2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;

class CSSSaveRunner implements Runnable {

	private final Brain brain;
	private final String url;
	private final File file;

	CSSSaveRunner( Brain brain, String url ) {
		this.brain = brain;
		this.url = url;
		this.file = new File( brain.root, brain.f.getFileName( url ) );
	}

	@Override
	public void run() {
		try {
			FileWriter w = new FileWriter( file );
			BufferedReader r = new BufferedReader( new InputStreamReader( new URL( url ).openStream() ) );

			String line;
			while( (line = r.readLine()) != null )
				w.write( CSSModifier.modify( brain, url, line ) + '\n' );

			r.close();
			w.close();
		}
		catch( Exception e ) {
			brain.log.e( getClass(), "failed : " + e );
		}

		brain.log.i( getClass(), "saved '" + url + "' -> '" + file + "'" );
	}

}
