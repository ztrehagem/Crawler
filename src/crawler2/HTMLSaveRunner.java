package crawler2;

import java.io.File;
import java.io.IOException;

class HTMLSaveRunner implements Runnable {

	private final Brain		brain;
	private final String	url;
	private final File		file;
	private final int		h;

	HTMLSaveRunner( final Brain brain, final String url, final int h ) {
		this.brain = brain;
		this.url = url;
		this.file = new File( brain.root, brain.h.getFileName( url ) );
		this.h = h - 1;
		if( this.h < 0 )
			throw new RuntimeException();
	}

	@Override
	public void run() {

		try {
			final String src = Tools.downloadToString( url );

			final HTMLModifier r = new HTMLModifier( brain, url, src, h );

			Tools.saveStringToFile( file, r.getResult() );
		}
		catch( IOException e ) {
			brain.log.e( getClass(), "failed : " + e );
			return;
		}

		brain.log.v( getClass(), "saved '" + url + "' -> '" + file + "'" );
	}
}
