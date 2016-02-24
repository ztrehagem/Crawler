package crawler2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

class NetUtil {

	private NetUtil() {
	}

	static void downloadToFile( String url, File dist ) throws FileNotFoundException, MalformedURLException, IOException {
		downloadToFile( new URL( url ), dist );
	}

	static void downloadToFile( URL url, File dist ) throws FileNotFoundException, IOException {

		InputStream in = url.openStream();
		FileOutputStream out = new FileOutputStream( dist );

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

	static String downloadToString( String url ) throws MalformedURLException, IOException {
		return downloadToString( new URL( url ) );
	}

	static String downloadToString( URL url ) throws IOException {

		StringBuilder sb = new StringBuilder();
		InputStreamReader in = new InputStreamReader( url.openStream() );

		char[] buf = new char[4096];

		while( true ) {
			int len = in.read( buf );
			if( len <= 0 )
				break;
			sb.append( buf, 0, len );
		}

		in.close();

		return sb.toString();
	}
}
