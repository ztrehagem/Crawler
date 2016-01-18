package crawler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import debug.Log;

class External implements Runnable {
	// single External

	private final File	file;
	private final URL	url;

	External( String url, File file ) throws MalformedURLException {
		this.url = new URL( url );
		this.file = file;
	}

	@Override
	public void run() {

		try {
			if( !file.createNewFile() ) {
				Log.e( getClass(), "This file is already exist '" + file + "'" );
				return;
			}
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
		catch( Exception e ) {
			Log.e( getClass(), "Exception on External '" + url + "'" );
			e.printStackTrace();
		}
	}
}
