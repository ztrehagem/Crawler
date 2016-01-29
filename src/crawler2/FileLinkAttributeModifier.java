package crawler2;

import java.util.List;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Segment;

class FileLinkAttributeModifier extends AttributeModifier {

	FileLinkAttributeModifier( Brain brain, OutputDocument od, String url, String tagname, String attrname ) {
		super( brain, od, url, tagname, attrname );
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

		if( isUnknown( e ) )
			return null;

		final String ext = Tools.getExtension( path );

		if( brain.f.makeID( fullpath, ext ) ) {
			if( ext != null && ext.toLowerCase().equals( "css" ) )
				brain.t.offer( new CSSSaveRunner( brain, fullpath ) );
			else
				brain.t.offer( new FileSaveRunner( brain, fullpath ) );
		}

		return brain.f.getFileName( fullpath );
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
}
