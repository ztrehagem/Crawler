package crawler2;

import java.io.File;
import java.io.IOException;

class FileSaveRunner implements Runnable {

	private final Brain		brain;
	private final String	url;
	private final File		file;

	FileSaveRunner( final Brain brain, final String url ) {
		this.brain = brain;
		this.url = url;
		this.file = new File( brain.root, brain.f.getFileName( url ) );
	}

	@Override
	public void run() {

		try {
			Tools.downloadToFile( url, file );
		}
		catch( IOException e ) {
			brain.log.e( getClass(), "failed : " + e );
			return;
		}

		brain.log.v( getClass(), "saved '" + url + "' -> '" + file + "'" );
	}
}
