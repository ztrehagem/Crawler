package crawler2;

import java.util.List;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Segment;

public class StyleTagContentModifier extends AttributeModifier {

	private final OutputDocument od;

	StyleTagContentModifier( Brain brain, OutputDocument od, String url ) {
		super( brain, od, url, "style", null );
		this.od = od;
	}

	@Override
	List<Element> getElements( Segment s ) {
		return s.getAllElements( attrname );
	}

	@Override
	String logic( Element e ) {
		final String type = e.getAttributeValue( "type" );
		if( type != null && !type.equals( "text/css" ) )
			return null;

		// TODO ここ真面目にタグの中身だけにしたい
		final String result = Tools.cssModify( brain, url, e.toString() );

		od.replace( e, result );

		return null;
	}

}
