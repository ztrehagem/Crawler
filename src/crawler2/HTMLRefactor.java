package crawler2;

import java.util.Map;
import java.util.regex.Pattern;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Source;

class HTMLRefactor {

	private final Crawler			master;
	private final String			url;
	private final int				h;
	private final OutputDocument	od;

	HTMLRefactor( final Crawler master, final String url, final Source src, final int h ) {
		this.master = master;
		this.url = url;
		this.h = h;
		this.od = new OutputDocument( new Source( src.toString().replace( "\n", "" ) ) );

		this.refactoring();
	}

	private void refactoring() {
		tag( "img", "src" );
		tag( "link", "href" );
		tag( "script", "src" );
		tag( "a", "href" );
		tag( "iframe", "src" );
		attribute( "background" );
		style();
	}

	OutputDocument getResult() {
		return this.od;
	}

	private void tag( final String tag, final String attrname ) {
		for( Element e : od.getSegment().getAllElements( tag ) ) {
			final String path = e.getAttributeValue( attrname );
			if( path == null )
				continue;

			final String fullpath = Tools.makeFullPath( url, path );
			if( fullpath == null )
				continue;

			if( isHTMLLinkTag( tag ) ) {
				if( !isHTML( tag, fullpath ) )
					continue;
				synchronized( master.h ) {
					if( !master.h.has( fullpath ) ) {
						if( h > 0 ) {
							master.h.makeID( fullpath );
							master.t.exec( new HTMLSaveRunner( master, fullpath, h ) );
						}
						else {
							continue;
						}
					}
				}
				this.modify( e, attrname, master.h.getFileName( fullpath ) );
			}
			else {
				String ext;
				if( (ext = Tools.getExtension( e )) == null && (ext = Tools.getExtension( path )) == null )
					continue;

				final boolean exist = !master.f.makeID( fullpath, ext );
				if( !exist )
					master.t.exec( new FileSaveRunner( master, fullpath ) );

				this.modify( e, attrname, master.f.getFileName( fullpath ) );
			}
		}
	}

	private boolean isHTMLLinkTag( final String tag ) {
		return in( tag, new String[] { "a", "iframe" } );
	}

	private boolean isHTML( final String tag, final String path ) {
		if( !path.startsWith( "http:" ) && !path.startsWith( "https:" ) )
			return false;
		final String ext = Tools.getExtension( path );
		return ext == null || in( ext, new String[] { "html", "htm", "asp", "aspx", "php", "cgi" } );
	}

	private boolean in( final String s, final String[] a ) {
		for( String as : a ) {
			if( s.equals( as ) )
				return true;
		}
		return false;
	}

	private void modify( final Element e, final String attrname, final String value ) {
		try {
			Map<String, String> map = od.replace( e.getAttributes(), true );
			map.put( attrname.toLowerCase(), value );
		}
		catch( Exception exc ) {
			Log.e( getClass(), "cant modifyRef : " + exc );
		}
	}

	private void attribute( final String attrname ) {
		for( Element e : od.getSegment().getAllElements( attrname, Pattern.compile( ".*" ) ) ) {

			final String path = e.getAttributeValue( attrname );
			if( path == null )
				continue;

			final String fullpath = Tools.makeFullPath( url, path );
			if( fullpath == null )
				continue;

			String ext;
			if( (ext = Tools.getExtension( e )) == null && (ext = Tools.getExtension( path )) == null )
				continue;

			final boolean exist = !master.f.makeID( fullpath, ext );

			if( !exist )
				master.t.exec( new FileSaveRunner( master, fullpath ) );

			this.modify( e, attrname, master.f.getFileName( fullpath ) );
		}
	}

	private void style() {
		for( Element e : od.getSegment().getAllElements( "style", Pattern.compile( ".*" ) ) ) {

			final String source = e.getAttributeValue( "style" );
			if( source == null )
				continue;

			final String result = CSSRefactor.lineRefactoring( source, url, master );

			this.modify( e, "style", result );
		}
	}
}
