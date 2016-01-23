package crawler2;

import java.util.Map;
import debug.Log;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Source;

class HTMLRefactor {

	private final Master			master;
	private final String			url;
	private final int				h;
	private final OutputDocument	od;

	HTMLRefactor( final Master master, final String url, final Source src, final int h ) {
		this.master = master;
		this.url = url;
		this.h = h;
		this.od = new OutputDocument( src );

		this.refactoring();
	}

	private void refactoring() {
		tag( "img", "src" );
		tag( "link", "href" );
		tag( "script", "src" );
		tag( "a", "href" );
		tag( "iframe", "src" );
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
				if( master.h.makeID( fullpath ) && h > 0 )
					master.t.exec( new HTMLSaveRunner( master, fullpath, h ) );
				this.modify( e, attrname, master.h.getFileName( fullpath ) );
			}
			else {
				final TriBool t = master.f.makeID( fullpath, e );
				if( t == TriBool.ERROR )
					continue;
				final boolean exist = t == TriBool.TRUE ? false : true;

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
		return ext == null || in( ext, new String[] { "html", "htm", "asp", "aspx", "php" } );
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
}
