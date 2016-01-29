package crawler2;

import java.io.File;
import java.io.IOException;

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

		try {
			final String src = Tools.downloadToString( url );

			final HTMLRefactor r = new HTMLRefactor( master, url, src, h );

			Tools.saveStringToFile( file, r.getResult() );
		}
		catch( IOException e ) {
			Log.e( getClass(), "failed : " + e );
			return;
		}

		Log.v( getClass(), "saved '" + url + "' -> '" + file + "'" );
	}
}
