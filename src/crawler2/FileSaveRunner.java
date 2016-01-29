package crawler2;

import java.io.File;
import java.io.IOException;

class FileSaveRunner implements Runnable {

	private final String	url;
	private final File		file;

	FileSaveRunner( final Crawler master, final String url ) {
		this.url = url;
		this.file = new File( master.root, master.f.getFileName( url ) );
	}

	@Override
	public void run() {

		try {
			Tools.downloadToFile( url, file );
		}
		catch( IOException e ) {
			Log.e( getClass(), "failed : " + e );
			return;
		}

		Log.v( getClass(), "saved '" + url + "' -> '" + file + "'" );
	}
}
