package crawler2;

import java.io.File;

class HTMLSaveRunner implements Runnable {

	private final Brain		brain;
	private final String	url;
	private final File		file;
	private final int		h;

	HTMLSaveRunner( Brain brain, String url, int h ) {
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
			final String src = NetUtil.downloadToString( url );
			final HTMLModifier r = new HTMLModifier( brain, url, src, h );
			StrUtil.saveToFile( file, r.getResult() );
		}
		catch( Exception e ) {
			brain.log.e( getClass(), "failed : " + e );
		}

		brain.log.i( getClass(), "saved '" + url + "' -> '" + file + "'" );
	}
}
