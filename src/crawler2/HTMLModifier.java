package crawler2;

import java.util.regex.Pattern;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Source;

class HTMLModifier {

	private final Crawler			master;
	private final String			url;
	private final int				h;
	private final OutputDocument	od;

	HTMLModifier( final Crawler master, final String url, final String src, final int h ) {
		this.master = master;
		this.url = url;
		this.h = h;
		this.od = new OutputDocument( new Source( src.replace( "\n", "" ) ) );

		this.refactoring();
	}

	private void refactoring() {
		filelinktag( "img", "src" );
		filelinktag( "link", "href" );
		filelinktag( "script", "src" );
		filelinktag( "video", "src" );
		// TODO 追加　他のメディアタグ
		pagelinktag( "a", "href" );
		pagelinktag( "iframe", "src" );
		attribute( "background" );
		styles();
		// TODO 埋め込み追加 styleタグ
	}

	String getResult() {
		return this.od.toString();
	}

	private void filelinktag( final String tag, final String attrname ) {
		for( Element e : od.getSegment().getAllElements( tag ) ) {
			final String path = e.getAttributeValue( attrname );
			if( path == null )
				continue;

			final String fullpath = Tools.makeFullPath( url, path );
			if( fullpath == null )
				continue;

			if( isUnknown( e ) )
				continue;

			final String ext = Tools.getExtension( path );

			if( master.f.makeID( fullpath, ext ) ) {
				if( ext != null && ext.toLowerCase().equals( "css" ) )
					master.t.exec( new CSSSaveRunner( master, fullpath ) );
				else
					master.t.exec( new FileSaveRunner( master, fullpath ) );
			}

			this.modify( e, attrname, master.f.getFileName( fullpath ) );
		}
	}

	private void pagelinktag( final String tag, final String attrname ) {
		for( Element e : od.getSegment().getAllElements( tag ) ) {
			final String path = e.getAttributeValue( attrname );
			if( path == null )
				continue;

			final String fullpath = Tools.makeFullPath( url, path );
			if( fullpath == null )
				continue;

			if( !isHTML( fullpath ) )
				continue;

			if( master.h.makeID( fullpath, h > 0 ) )
				master.t.exec( new HTMLSaveRunner( master, fullpath, h ) );

			this.modify( e, attrname, master.h.getFileName( fullpath ) );
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

			final String ext = Tools.getExtension( path );

			if( master.f.makeID( fullpath, ext ) )
				master.t.exec( new FileSaveRunner( master, fullpath ) );

			this.modify( e, attrname, master.f.getFileName( fullpath ) );
		}
	}

	private void styles() {
		final String attrname = "style";
		for( Element e : od.getSegment().getAllElements( attrname, Pattern.compile( ".*" ) ) ) {

			final String value = e.getAttributeValue( attrname );
			if( value == null )
				continue;

			this.modify( e, attrname, Tools.cssModify( master, url, value ) );
		}
	}

	private boolean isHTML( final String fullpath ) {
		if( !fullpath.startsWith( "http:" ) && !fullpath.startsWith( "https:" ) )
			return false;
		final String ext = Tools.getExtension( fullpath );
		return ext == null || Tools.in( ext, new String[] { "html", "htm", "asp", "aspx", "php", "cgi" } );
	}

	private boolean isUnknown( Element e ) {
		final String tag = e.getName().toLowerCase();
		if( tag.equals( HTMLElementName.LINK ) ) {
			final String rel = e.getAttributeValue( "rel" );
			return rel == null || !rel.toLowerCase().equals( "stylesheet" );
		}
		if( tag.equals( HTMLElementName.SCRIPT ) ) {
			final String type = e.getAttributeValue( "type" );
			return type != null && !type.toLowerCase().equals( "text/javascript" );
		}
		return false;
	}

	private void modify( final Element e, final String attrname, final String value ) {
		try {
			od.replace( e.getAttributes(), true ).put( attrname.toLowerCase(), value );
		}
		catch( Exception exc ) {
			Log.e( getClass(), "cant modifyRef : " + exc );
		}
	}
}
