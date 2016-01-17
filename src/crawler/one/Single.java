package crawler.one;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import debug.Log;
import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Source;

public class Single {

	private final File	dir;
	private final URL	url;

	private Source			src;
	private OutputDocument	od;

	private int		fileId		= 0;
	private Object	fileIdMutex	= new Object();

	private Map<String, String> extfilemap;

	public Single( String url ) throws Exception {
		this.dir = new File( "result/" + String.valueOf( System.currentTimeMillis() ) );
		this.dir.mkdirs();
		Log.v( getClass(), "dir '" + this.dir.getAbsolutePath() + "'" );

		this.url = new URL( url );
		Log.v( getClass(), "target '" + url + "'" );

		this.extfilemap = new HashMap<>();
	}

	public void process() throws Exception {

		this.src = new Source( this.url );
		this.od = new OutputDocument( this.src );

		saveExternals();

		saveHTML();
	}

	private void saveExternals() {
		searchExternals( HTMLElementName.LINK, "href" );
		searchExternals( HTMLElementName.SCRIPT, "src" );
		searchExternals( HTMLElementName.IMG, "src" );

		for( String path : extfilemap.keySet() ) {
			try {
				save( new File( dir, extfilemap.get( path ) ), new URL( path ) );
			}
			catch( IOException e ) {
				e.printStackTrace();
			}
		}
	}

	private void searchExternals( String tag, String attrName ) {
		for( Element e : src.getAllElements( tag ) ) {

			String path = e.getAttributeValue( attrName );
			if( path == null || path.startsWith( "#" ) || path.startsWith( "javascript:" ) ) {
				continue;
			}

			path = makeFullPath( path );

			if( extfilemap.containsKey( path ) ) {
				modifyRef( e, attrName, extfilemap.get( path ) );
				continue;
			}

			{
				int id;
				synchronized( fileIdMutex ) {
					id = ++fileId;
				}
				final String outputFilename = id + "." + getExtension( path );
				extfilemap.put( path, outputFilename );
				modifyRef( e, attrName, outputFilename );
			}
		}
	}

	private void modifyRef( final Element e, final String attrName, final String outputFilename ) {
		OutputDocument od = new OutputDocument( e );

		HashMap<String, String> attrmap = new HashMap<>();
		for( Attribute a : e.getAttributes() ) {
			if( a.getName().equals( attrName ) )
				continue;
			attrmap.put( a.getName(), a.getValue() );
		}
		attrmap.put( attrName, outputFilename );
		od.replace( e.getAttributes(), attrmap );
		final String replaced = od.toString();

		this.od.replace( e, replaced );
	}

	private String makeFullPath( final String path ) {
		StringBuilder sb = new StringBuilder();
		if( path.contains( "//" ) ) { // 絶対パス
			sb.append( path );
			if( !path.contains( ":" ) ) {
				sb.insert( 0, ":" ).insert( 0, this.url.getProtocol() );
			}
		}
		else { // 相対パス
			sb.append( this.url.getProtocol() ).append( "://" ).append( this.url.getHost() );
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
						tb.append( ts[i] ).append( "/" );
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

	private void saveHTML() throws IOException {
		final File file = new File( dir, dir.getName() + ".html" );
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
