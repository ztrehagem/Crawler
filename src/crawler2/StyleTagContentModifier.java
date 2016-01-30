package crawler2;

import java.util.List;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Segment;

public class StyleTagContentModifier extends AttributeModifier {

	private final OutputDocument od;

	StyleTagContentModifier( Brain brain, OutputDocument od, String url ) {
		super( brain, od, url, "style", "type" );
		this.od = od;
	}

	@Override
	List<Element> getElements( Segment s ) {
		return s.getAllElements( attrname );
	}

	@Override
	String logic( Element e ) {
		final String type = e.getAttributeValue( attrname );
		if( type != null && !type.equals( "text/css" ) )
			return null;

		// TODO ここ真面目にタグの中身だけにしたい
		final String result = CSSModifier.modify( brain, url, e.toString() );

		od.replace( e, result );

		return null;
	}

}
