package crawler2;

import java.io.File;

class FileSaveRunner implements Runnable {

	private final Brain brain;
	private final String url;
	private final File file;

	FileSaveRunner( Brain brain, String url ) {
		this.brain = brain;
		this.url = url;
		this.file = new File( brain.root, brain.f.getFileName( url ) );
	}

	@Override
	public void run() {
		try {
			NetUtil.downloadToFile( url, file );
		}
		catch( Exception e ) {
			brain.log.e( getClass(), "failed : " + e );
		}

		brain.log.i( getClass(), "saved '" + url + "' -> '" + file + "'" );
	}
}
