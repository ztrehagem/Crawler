package crawler2;

import java.io.File;
import java.io.IOException;

class FileSaveRunner implements Runnable {

	private final Crawler	master;
	private final String	url;
	private final File		file;
	private final boolean	isCss;

	FileSaveRunner( final Crawler master, final String url ) {
		this.master = master;
		this.url = url;
		this.file = new File( master.root, master.f.getFileName( url ) );
		this.isCss = this.file.getName().endsWith( ".css" );
	}

	@Override
	public void run() {

		try {
			Tools.download( url, file );
		}
		catch( IOException e ) {
			Log.e( getClass(), "failed : " + e );
			return;
		}

		if( isCss ) {
			new CSSRefactor( master, url, file ).process();
		}

		Log.v( getClass(), "saved '" + url + "' -> '" + file + "'" );
	}
}
