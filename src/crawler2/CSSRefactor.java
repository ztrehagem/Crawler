package crawler2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

class CSSRefactor {

	private final Crawler	master;
	private final String	url;
	private final File		file;

	CSSRefactor( Crawler master, String url, File file ) {
		this.master = master;
		this.url = url;
		this.file = file;
	}

	void process() {
		final StringBuilder buf = new StringBuilder();

		try {
			BufferedReader r = new BufferedReader( new FileReader( this.file ) );
			String line;

			while( (line = r.readLine()) != null ) {
				buf.append( lineRefactoring( line, url, master ) + "\n" );
			}
			r.close();
		}
		catch( IOException e ) {
			Log.e( getClass(), "css reading exception : " + e );
			return;
		}

		try {
			Writer w = new FileWriter( this.file );
			w.write( buf.toString() );
			w.flush();
			w.close();
		}
		catch( IOException e ) {
			Log.e( getClass(), "css writing exception : " + e );
		}
	}

	static String lineRefactoring( final String line, final String url, final Crawler master ) {
		StringBuilder lbuf = new StringBuilder();
		int last = 0;
		int head = 0;

		while( (head = line.indexOf( "url(", last )) != -1 ) {
			lbuf.append( line.substring( last, head ) );

			final boolean quote = line.charAt( head + 4 ) == '\'' || line.charAt( head + 4 ) == '"';
			final int start = head + 4 + (quote ? 1 : 0);
			final int end = line.indexOf( ")", start ) - (quote ? 1 : 0);

			last = end + 1 + (quote ? 1 : 0);

			final String target = line.substring( start, end );

			final String ext = Tools.getExtension( target );
			if( ext == null ) {
				lbuf.append( line.substring( head, last ) );
				continue;
			}

			final String fullpath = Tools.makeFullPath( url, target );
			if( master.f.makeID( fullpath, ext ) ) {
				master.t.exec( new FileSaveRunner( master, fullpath ) );
			}

			final String result = "url(\"" + master.f.getFileName( fullpath ) + "\")";
			//					Log.v( getClass(), "css modify '" + target + "' -> '" + result + "'" );
			lbuf.append( result );
		}
		lbuf.append( line.substring( last ) );

		return lbuf.toString();
	}
}
