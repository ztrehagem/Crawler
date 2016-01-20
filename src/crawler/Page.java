package crawler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import debug.Log;
import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Source;

class Page implements Runnable {
	// Page ->> Page
	// Page ->> External

	private final Master				master;

	private final File					dir;
	private final URL					url;
	private final int					pageId;
	private final int					h;

	private final Map<String, String>	extfilemap;
	private final Map<String, Integer>	linkedpagemap;

	private Source						src;
	private OutputDocument				od;
	private int							fileId	= 0;

	Page( Master master, String url, int pageId, int h ) throws MalformedURLException {
		this.master = master;
		this.dir = new File( master.root, String.valueOf( pageId ) );
		this.url = new URL( url );
		this.pageId = pageId;
		this.h = h;
		this.extfilemap = new HashMap<>();
		this.linkedpagemap = new HashMap<>();
	}

	@Override
	public void run() {

		this.dir.mkdir();

		try {
			this.src = new Source( this.url );
		}
		catch( IOException e ) {
			Log.e( getClass(), "Exception in run : new Source '" + this.url + "' : " + e );
			return;
		}

		this.od = new OutputDocument( src );

		searchExternals();
		searchPages();
		savePage();
		runThreads();
	}

	void searchExternals() {
		searchExternals( HTMLElementName.LINK, "href" );
		searchExternals( HTMLElementName.SCRIPT, "src" );
		searchExternals( HTMLElementName.IMG, "src" );
	}

	void searchExternals( String tag, String attrname ) {
		for( Element e : src.getAllElements( tag ) ) {

			String path = e.getAttributeValue( attrname );
			if( !isExternalLink( path ) )
				continue;

			path = makeFullPath( path );
			String outputFilename;

			if( !extfilemap.containsKey( path ) ) {
				final String ext = getExtension( e, path );
				if( ext == null ) {
					//					Log.v( getClass(), "unknown MIME type '" + e );
					continue;
				}
				outputFilename = ++fileId + "." + ext;
				extfilemap.put( path, outputFilename );
			}
			else {
				outputFilename = extfilemap.get( path );
			}
			modifyRef( e, attrname, dir.getName() + "/" + outputFilename );
		}
	}

	private void searchPages() {
		searchPages( HTMLElementName.A, "href" );
		searchPages( HTMLElementName.IFRAME, "src" );
	}

	private void searchPages( String tag, String attrname ) {
		for( Element e : src.getAllElements( tag ) ) {

			String path = e.getAttributeValue( attrname );
			if( !isExternalLink( path ) )
				continue;

			path = makeFullPath( path );
			final PageInfo i = master.addPageList( path );
			final String outputFilename = i.pageId + ".html";

			if( this.h > 0 && !i.exist )
				linkedpagemap.put( path, i.pageId );

			if( this.h == 0 && !i.exist )
				continue;

			modifyRef( e, attrname, outputFilename );
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
			if( path.startsWith( "//" ) ) {
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

	private String getExtension( Element e, String path ) {
		if( e.getName().equals( HTMLElementName.LINK ) ) {
			String rel = e.getAttributeValue( "rel" );
			if( rel != null && !rel.toLowerCase().equals( "stylesheet" ) )
				return null;
			else
				return "css";
		}
		if( e.getName().equals( HTMLElementName.SCRIPT ) ) {
			String type = e.getAttributeValue( "type" );
			if( type == null || type.toLowerCase().equals( "text/javascript" ) )
				return "js";
			else
				return null;
		}
		String[] sp = path.split( "#" );
		if( sp.length == 0 )
			return null;
		path = sp[0];
		sp = path.split( "\\?" );
		if( sp.length == 0 )
			return null;
		path = sp[0];
		sp = path.split( "\\." );
		if( sp.length == 0 )
			return null;
		return sp[sp.length - 1];
	}

	private boolean isExternalLink( String ref ) {
		return ref != null && !ref.startsWith( "#" ) && !ref.startsWith( "javascript:" )
			&& !ref.startsWith( "mailto:" );
	}

	private void savePage() {
		final File file = new File( master.root, this.pageId + ".html" );
		try {
			file.createNewFile();
		}
		catch( IOException e ) {
			Log.e( getClass(), "Exception in savePage : createFile() '" + file + "' : " + e );
		}
		try {
			FileWriter w = new FileWriter( file );
			w.write( od.toString() );
			w.flush();
			w.close();
		}
		catch( IOException e ) {
			Log.e( getClass(), "Exception in savePage : saving '" + url + "' : " + e );
		}
	}

	private void runThreads() {
		ArrayList<Thread> tl = new ArrayList<>();

		// Pages
		for( Entry<String, Integer> e : linkedpagemap.entrySet() ) {
			try {
				tl.add( new Thread( new Page( master, e.getKey(), e.getValue(), h - 1 ) ) );
			}
			catch( MalformedURLException exc ) {
				Log.e( getClass(), "Exception in runThreads : pages '" + e + "' : " + exc );
			}
		}

		// Externals
		for( Entry<String, String> e : extfilemap.entrySet() ) {
			try {
				tl.add( new Thread( new External( e.getKey(), new File( dir, e.getValue() ) ) ) );
			}
			catch( MalformedURLException exc ) {
				Log.e( getClass(), "Exception in runThreads : exts '" + e + "' : " + exc );
			}
		}

		for( Thread t : tl ) {
			t.start();
		}

		for( Thread t : tl ) {
			try {
				t.join();
			}
			catch( InterruptedException e ) {
				Log.e( getClass(), "Exception in runThreads : joining : " + e );
			}
		}
	}
}
