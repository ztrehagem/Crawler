package crawler2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import debug.Log;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Source;

class HTMLSaveRunner implements Runnable {

	private final Master	m;
	private final String	url;
	private final File		file;
	private final int		h;

	public HTMLSaveRunner( final Master m, final String url, final int h ) {
		this.m = m;
		this.url = url;
		this.file = new File( m.root, m.h.getFileName( url ) );
		this.h = h - 1;
		if( this.h < 0 )
			throw new RuntimeException();
	}

	@Override
	public void run() {

		Source src;
		try {
			src = new Source( new URL( url ) );
		}
		catch( IOException e ) {
			Log.e( getClass(), "Exception in run : new Source '" + url + "' : " + e );
			return;
		}

		save( new HTMLRefactor( m, url, src, h ).getResult() );
	}

	private void save( final OutputDocument od ) {
		try {
			file.createNewFile();
		}
		catch( IOException e ) {
			Log.e( getClass(), "Exception in save : createNewFile '" + file + "' : " + e );
		}

		try {
			FileWriter w = new FileWriter( file );
			w.write( od.toString() );
			w.flush();
			w.close();
		}
		catch( IOException e ) {
			Log.e( getClass(), "Exception in savePage : saving '" + url + "' : " + e );
		}
	}

}
