package crawler2;

import java.util.List;
import java.util.regex.Pattern;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Segment;

public class SpecificAttributeModifier extends AttributeModifier {

	SpecificAttributeModifier( Brain brain, OutputDocument od, String url, String attrname ) {
		super( brain, od, url, null, attrname );
	}

	@Override
	List<Element> getElements( Segment s ) {
		return s.getAllElements( attrname, Pattern.compile( ".*" ) );
	}

	@Override
	String logic( Element e ) {
		final String path = e.getAttributeValue( attrname );
		if( path == null )
			return null;

		final String fullpath = StrUtil.makeFullPath( url, path );
		if( fullpath == null )
			return null;

		final String ext = StrUtil.getExtension( path );

		if( brain.f.makeID( fullpath, ext ) )
			brain.t.offer( new FileSaveRunner( brain, fullpath ) );

		return brain.f.getFileName( fullpath );
	}

}
