package crawler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import debug.Log;

class External implements Runnable {
	// single External

	private final File		file;
	private final URL		url;
	private final boolean	isCss;

	External( String url, File file ) throws MalformedURLException {
		this.url = new URL( url );
		this.file = file;
		this.isCss = this.file.getName().endsWith( ".css" );
	}

	@Override
	public void run() {

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
			Log.e( getClass(), "FileNotFoundException in run : ExtFileSaving '" + file + "' <- '" + url + "' : " + e );
		}
		catch( IOException e ) {
			Log.e( getClass(), "Exception in run : ExtFileSaving '" + url + "': " + e );
		}

		if( isCss ) {
			new CssExternal( this.url, this.file ).process();
		}
	}
}
