package crawler2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

class Tools {

	private Tools() {

	}

	static String makeFullPath( final String from, final String to ) {
		try {
			return new URI( from ).resolve( to ).toString();
		}
		catch( Exception e ) {

		}
		return null;
	}

	static String getExtension( String path ) {
		try {
			path = new URI( path ).getPath();
		}
		catch( URISyntaxException e ) {
			return null;
		}

		try {
			if( path.endsWith( "/" ) )
				return null;

			String[] sp = path.split( "\\/" );
			path = sp[sp.length - 1];

			if( !path.contains( "." ) || path.endsWith( "." ) )
				return null;

			sp = path.split( "\\." );
			path = sp[sp.length - 1];
		}
		catch( ArrayIndexOutOfBoundsException | NullPointerException e ) {
			return null;
		}
		return path.equals( "" ) ? null : path;
	}

	static String cssModify( final Crawler master, final String url, final String line ) {
		final StringBuilder sb = new StringBuilder();
		int last = 0;
		int head = 0;

		while( (head = line.indexOf( "url(", last )) != -1 ) {
			sb.append( line.substring( last, head ) );

			final boolean quote = line.charAt( head + 4 ) == '\'' || line.charAt( head + 4 ) == '"';
			final int start = head + 4 + (quote ? 1 : 0);
			final int end = line.indexOf( ")", start ) - (quote ? 1 : 0);

			if( end <= -1 ) {
				last = head;
				break;
			}

			last = end + 1 + (quote ? 1 : 0);

			final String target = line.substring( start, end );

			final String ext = Tools.getExtension( target );

			final String fullpath = Tools.makeFullPath( url, target );

			if( master.f.makeID( fullpath, ext ) )
				master.t.exec( new FileSaveRunner( master, fullpath ) );

			sb.append( "url(\"" + master.f.getFileName( fullpath ) + "\")" );
		}
		sb.append( line.substring( last ) );

		return sb.toString();
	}

	static void downloadToFile( final String url, final File dist ) throws FileNotFoundException, MalformedURLException, IOException {
		downloadToFile( new URL( url ), dist );
	}

	static void downloadToFile( final URL url, final File dist ) throws FileNotFoundException, IOException {
		dist.createNewFile();

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

	static String downloadToString( final String url ) throws MalformedURLException, IOException {
		return downloadToString( new URL( url ) );
	}

	static String downloadToString( final URL url ) throws IOException {

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

	static void saveStringToFile( final File file, final String s ) throws IOException {
		FileWriter w = new FileWriter( file );
		w.write( s );
		w.flush();
		w.close();
	}

	static boolean in( final String s, final String[] a ) {
		for( String as : a ) {
			if( s.equals( as ) )
				return true;
		}
		return false;
	}
}
