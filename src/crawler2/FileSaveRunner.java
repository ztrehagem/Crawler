package crawler2;

import java.io.File;
import java.util.concurrent.Callable;

class FileSaveRunner implements Callable<SaveRunnerResult> {

	private final String	url;
	private final File		file;

	FileSaveRunner( Brain brain, String url ) {
		this.url = url;
		this.file = new File( brain.root, brain.f.getFileName( url ) );
	}

	@Override
	public SaveRunnerResult call() throws Exception {

		NetUtil.downloadToFile( url, file );

		return new SaveRunnerResult( url, file.toString() );
	}
}
