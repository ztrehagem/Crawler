package crawler2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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
		URL url;
		try {
			url = new URL( this.url );
		}
		catch( MalformedURLException e ) {
			Log.e( getClass(), "Exception in run : new URL '" + this.url + "' : " + e );
			return;
		}

		try {
			file.createNewFile();
		}
		catch( IOException e ) {
			Log.e( getClass(), "Exception in run : createNewFile '" + file + "' : " + e );
			return;
		}

		try {
			URLConnection c = url.openConnection();
			InputStream in = c.getInputStream();
			FileOutputStream out = new FileOutputStream( file );
			byte[] buf = new byte[4096];
			while( true ) {
				int len = in.read( buf );
				if( len <= 0 )
					break;
				out.write( buf, 0, len );
			}
			out.close();
			in.close();
		}
		catch( FileNotFoundException e ) {
			Log.e( getClass(), "FileNotFound '" + url + "' : " + e );
			return;
		}
		catch( IOException e ) {
			Log.e( getClass(), "Exception in run : FileSaving '" + url + "': " + e );
			return;
		}

		if( isCss ) {
			new CSSRefactor( this.master, this.url, this.file ).process();
		}

		Log.v( getClass(), "saved '" + this.url + "' -> '" + this.file + "'" );
	}
}
