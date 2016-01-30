package crawler2;

import java.util.List;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Segment;

public class PageLinkTagAttributeModifier extends AttributeModifier {

	private final int h;

	PageLinkTagAttributeModifier( Brain brain, OutputDocument od, String url, String tagname, String attrname, int h ) {
		super( brain, od, url, tagname, attrname );
		this.h = h;
	}

	@Override
	List<Element> getElements( Segment s ) {
		return s.getAllElements( tagname );
	}

	@Override
	String logic( Element e ) {
		final String path = e.getAttributeValue( attrname );
		if( path == null )
			return null;

		final String fullpath = Tools.makeFullPath( url, path );
		if( fullpath == null )
			return null;

		if( !isHTML( fullpath ) )
			return null;

		if( brain.h.makeID( fullpath, h ) )
			brain.t.offer( new HTMLSaveRunner( brain, fullpath, h ) );

		return brain.h.getFileName( fullpath );
	}

	private boolean isHTML( String fullpath ) {
		if( !fullpath.startsWith( "http:" ) && !fullpath.startsWith( "https:" ) )
			return false;
		final String ext = Tools.getExtension( fullpath );
		return ext == null || Tools.in( ext, new String[] { "html", "htm", "asp", "aspx", "php", "cgi" } );
	}

}
