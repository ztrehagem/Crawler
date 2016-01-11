package crawler.one;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import debug.Log;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

public class Single {

	private final File	localDir;
	private final URL	url;

	public Single( String url ) throws Exception {
		localDir = new File( "result/" + String.valueOf( System.currentTimeMillis() ) );
		localDir.mkdirs();
		Log.v( getClass(), "dir '" + localDir.getAbsolutePath() + "'" );

		this.url = new URL( url );
		Log.v( getClass(), "target '" + url + "'\nhost '" + this.url.getHost() + "'\n" + "path '" + this.url.getPath()
			+ "'\n" + "file '" + this.url.getFile() + "'" );
	}

	public void process() throws Exception {

		final Source src = saveHTML( url );

		saveExternals( src );
	}

	private void saveExternals( Source src ) {
		saveLink( src );
		saveScript( src );
	}

	private void saveLink( Source src ) {
		// linkタグのhref属性

		List<Element> l = src.getAllElements( "link" );
		for( Element e : l ) {
			String href = e.getAttributeValue( "href" );
			String fp = makeFullPath( href );
			Log.v( getClass(), "link:href     '" + href + "'\nlink:fullpath '" + fp + "'" );
		}
	}

	private void saveScript( Source src ) {
		// scriptタグのsrc属性

		List<Element> l = src.getAllElements( "script" );
		for( Element e : l ) {
			String ref = e.getAttributeValue( "src" );
			if( ref == null ) {
				continue;
			}
			String fp = makeFullPath( ref );
			Log.v( getClass(), "script:src '" + ref + "'\nscript:fp  '" + fp + "'" );
		}
	}

	private String makeFullPath( String path ) {
		StringBuilder sb = new StringBuilder();
		if( path.contains( "//" ) ) { // 絶対パス
			sb.append( path );
			if( !path.contains( ":" ) ) {
				sb.insert( 0, "http:" );
			}
		}
		else { // 相対パス
			sb.append( "http://" );
			sb.append( this.url.getHost() );
			if( path.startsWith( "/" ) ) { // ルートから
				sb.append( path );
			}
			else { // カレントディレクトリから
				String t = this.url.getPath();

				// カレントパスがファイルであればカレントディレクトリパスを求める
				String ts[] = t.split( "/" );
				if( ts.length != 0 && ts[ts.length - 1].contains( "." ) ) {
					t = "/";
					for( int i = 0; i < ts.length - 1; i++ ) {
						t += ts[i] + "/";
					}
				}

				if( !t.startsWith( "/" ) ) {
					sb.append( "/" );
				}
				sb.append( t );

				if( !t.endsWith( "/" ) ) {
					sb.append( "/" );
				}
				sb.append( path );
			}
		}
		return sb.toString();
	}

	private Source saveHTML( URL url ) throws IOException {
		final File dist = createFile();
		if( dist == null ) {
			return null;
		}

		final Source src = new Source( url );
		FileWriter w = new FileWriter( dist );
		w.write( src.toString() );
		w.flush();
		w.close();
		return src;
	}

	private File createFile() throws IOException {
		File page = new File( localDir.getCanonicalPath() + "/" + localDir.getName() + ".html" );
		if( !page.createNewFile() ) {
			return null;
		}
		return page;
	}

	private void save( File file, URL url ) throws IOException {
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
}
