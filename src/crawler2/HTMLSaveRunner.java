package crawler2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import net.htmlparser.jericho.Source;

class HTMLSaveRunner implements Runnable {

	private final Crawler	master;
	private final String	url;
	private final File		file;
	private final int		h;

	HTMLSaveRunner( final Crawler master, final String url, final int h ) {
		this.master = master;
		this.url = url;
		this.file = new File( master.root, master.h.getFileName( url ) );
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

		save( new HTMLRefactor( master, url, src, h ).getResult() );

		Log.v( getClass(), "saved '" + this.url + "' -> '" + this.file + "'" );
	}

	private void save( final String s ) {
		try {
			file.createNewFile();
		}
		catch( IOException e ) {
			Log.e( getClass(), "Exception in save : createNewFile '" + file + "' : " + e );
		}

		try {
			FileWriter w = new FileWriter( file );
			w.write( s );
			w.flush();
			w.close();
		}
		catch( IOException e ) {
			Log.e( getClass(), "Exception in savePage : saving '" + url + "' : " + e );
		}
	}

}
