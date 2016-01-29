package crawler2;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Source;

class HTMLModifier {

	private final Brain				brain;
	private final String			url;
	private final int				h;
	private final OutputDocument	od;

	HTMLModifier( final Brain brain, final String url, final String src, final int h ) {
		this.brain = brain;
		this.url = url;
		this.h = h;
		this.od = new OutputDocument( new Source( src.replace( "\n", "" ) ) );

		this.exec();
	}

	private void exec() {
		List<AttributeModifier> list = new ArrayList<>();

		list.add( new FileLinkAttributeModifier( brain, od, url, HTMLElementName.IMG, "src" ) );
		list.add( new FileLinkAttributeModifier( brain, od, url, HTMLElementName.LINK, "href" ) );
		list.add( new FileLinkAttributeModifier( brain, od, url, HTMLElementName.SCRIPT, "src" ) );
		list.add( new FileLinkAttributeModifier( brain, od, url, HTMLElementName.VIDEO, "src" ) );

		for( AttributeModifier am : list )
			am.modify();

		// TODO 追加　他のメディアタグ
		pagelinktag( "a", "href" );
		pagelinktag( "iframe", "src" );
		attribute( "background" );
		styleAttribute();
		styleTag();
	}

	String getResult() {
		return this.od.toString();
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

			if( brain.h.makeID( fullpath, h ) )
				brain.t.offer( new HTMLSaveRunner( brain, fullpath, h ) );

			this.modify( e, attrname, brain.h.getFileName( fullpath ) );
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

			if( brain.f.makeID( fullpath, ext ) )
				brain.t.offer( new FileSaveRunner( brain, fullpath ) );

			this.modify( e, attrname, brain.f.getFileName( fullpath ) );
		}
	}

	private void styleAttribute() {
		final String attrname = "style";
		for( Element e : od.getSegment().getAllElements( attrname, Pattern.compile( ".*" ) ) ) {

			final String value = e.getAttributeValue( attrname );
			if( value == null )
				continue;

			this.modify( e, attrname, Tools.cssModify( brain, url, value ) );
		}
	}

	private void styleTag() {
		for( Element e : od.getSegment().getAllElements( "style" ) ) {
			final String type = e.getAttributeValue( "type" );
			if( type != null && !type.equals( "text/css" ) )
				continue;

			// TODO ここ真面目にタグの中身だけにしたい
			final String result = Tools.cssModify( brain, url, e.toString() );

			od.replace( e, result );
		}
	}

	private boolean isHTML( final String fullpath ) {
		if( !fullpath.startsWith( "http:" ) && !fullpath.startsWith( "https:" ) )
			return false;
		final String ext = Tools.getExtension( fullpath );
		return ext == null || Tools.in( ext, new String[] { "html", "htm", "asp", "aspx", "php", "cgi" } );
	}

	private void modify( final Element e, final String attrname, final String value ) {
		try {
			od.replace( e.getAttributes(), true ).put( attrname.toLowerCase(), value );
		}
		catch( Exception exc ) {
			brain.log.e( getClass(), "cant modifyRef : " + exc );
		}
	}
}
