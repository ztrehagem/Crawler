package crawler.one;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import debug.Log;
import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.Attributes;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Source;

public class Single {

	private final File			localDir;
	private final URL			url;
	private Map<String, String>	extfilemap;

	private int		fileId		= 0;
	private Object	fileIdMutex	= new Object();

	public Single( String url ) throws Exception {
		localDir = new File( "result/" + String.valueOf( System.currentTimeMillis() ) );
		localDir.mkdirs();
		Log.v( getClass(), "dir '" + localDir.getAbsolutePath() + "'" );

		this.url = new URL( url );
		Log.v( getClass(), "target '" + url + "'\nhost '" + this.url.getHost() + "'\n" + "path '" + this.url.getPath()
			+ "'\n" + "file '" + this.url.getFile() + "'\nprotocol '" + this.url.getProtocol() + "'" );

		extfilemap = new HashMap<>();
	}

	public void process() throws Exception {

		final Source src = new Source( url );
		final OutputDocument od = new OutputDocument( src );

		saveExternals( src );

		saveHTML( od );
	}

	private void saveExternals( Source src ) {
		searchExternals( HTMLElementName.LINK, "href", src );
		searchExternals( HTMLElementName.SCRIPT, "src", src );
		searchExternals( HTMLElementName.IMG, "src", src );

		int id = 0;
		Set<String> keyset = extfilemap.keySet();
		//		for( String path : keyset ) {
		//			try {
		//				save( new File( localDir, extfilemap.get( path ) ), new URL( path ) );
		//			}
		//			catch( IOException e ) {
		//				e.printStackTrace();
		//			}
		//		}
	}

	private void searchExternals( String tag, String attrName, Source src ) {
		List<Element> l = src.getAllElements( tag );
		for( Element e : l ) {
			Attributes attrs = e.getAttributes();
			Attribute attr = attrs.get( attrName );
			if( attr == null ) {
				continue;
			}
			String path = attr.getValue();
			Log.v( getClass(), path );
			if( path == null || path.startsWith( "#" ) || path.startsWith( "javascript:" ) ) {
				continue;
			}

			path = makeFullPath( path );

			if( extfilemap.containsKey( path ) ) {
				continue;
			}

			int id;
			synchronized( fileIdMutex ) {
				id = ++fileId;
			}
			final String outputFilename = id + "." + getExtension( path );
			extfilemap.put( path, outputFilename );

			// 出力HTMLのhrefを書き換える // Map中被りにも適用しろ
			{
				OutputDocument od = new OutputDocument( e );
				Log.v( getClass(), "od before '" + od.toString() + "'" );
				HashMap<String, String> attrmap = new HashMap<>();
				attrmap.put( attrName, outputFilename );
				od.replace( attrs, attrmap );
				Log.v( getClass(), "od after '" + od.toString() + "'" );
			}

			Log.v( getClass(), "add extfiles <" + tag + " " + attrName + "> '" + path + "'" );
		}
	}

	private String makeFullPath( final String path ) {
		StringBuilder sb = new StringBuilder();
		if( path.contains( "//" ) ) { // 絶対パス
			sb.append( path );
			if( !path.contains( ":" ) ) {
				sb.insert( 0, ":" );
				sb.insert( 0, this.url.getProtocol() );
			}
		}
		else { // 相対パス
			sb.append( this.url.getProtocol() );
			sb.append( "://" );
			sb.append( this.url.getHost() );
			if( path.startsWith( "/" ) ) { // ルートから
				sb.append( path );
			}
			else { // カレントディレクトリから
				String t = this.url.getPath();

				// カレントパスがファイルであればカレントディレクトリパスを求める
				String ts[] = t.split( "/" );
				if( ts.length != 0 && ts[ts.length - 1].contains( "." ) ) {
					StringBuilder tb = new StringBuilder();
					for( int i = 0; i < ts.length - 1; i++ ) {
						tb.append( ts[i] );
						tb.append( "/" );
					}
					t = tb.toString();
				}

				if( !t.startsWith( "/" ) ) {
					sb.append( "/" );
				}
				sb.append( t );

				if( !sb.toString().endsWith( "/" ) ) {
					sb.append( "/" );
				}
				sb.append( path );
			}
		}
		return sb.toString();
	}

	private void saveHTML( OutputDocument od ) throws IOException {
		final File file = new File( localDir.getCanonicalPath() + "/" + localDir.getName() + ".html" );
		if( !file.createNewFile() ) {
			Log.e( getClass(), "failed create html file" );
			return;
		}

		FileWriter w = new FileWriter( file );
		w.write( od.toString() );
		w.flush();
		w.close();
	}

	private void save( File file, URL url ) throws IOException {
		if( !file.createNewFile() ) {
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
		Log.v( getClass(), "save '" + url + "' -> '" + file.getPath() + "'" );
		out.close();
		in.close();
	}

	private String getExtension( String path ) {
		final String[] sp = path.split( "\\." );
		if( sp.length == 0 )
			return null;
		return sp[sp.length - 1];
	}
}
