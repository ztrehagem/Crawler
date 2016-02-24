package crawler2;

import java.io.File;
import java.util.concurrent.Callable;

class FileSaveRunner implements Callable<Void> {

	private final Brain		brain;
	private final String	url;
	private final File		file;

	FileSaveRunner( Brain brain, String url ) {
		this.brain = brain;
		this.url = url;
		this.file = new File( brain.root, brain.f.getFileName( url ) );
	}

	@Override
	public Void call() throws Exception {

		NetUtil.downloadToFile( url, file );

		brain.log.i( getClass(), "saved '" + url + "' -> '" + file + "'" );
		return null;
	}
}
