package crawler2;

import java.util.List;
import java.util.regex.Pattern;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Segment;

public class StyleAttributeModifier extends AttributeModifier {

	StyleAttributeModifier( Brain brain, OutputDocument od, String url ) {
		super( brain, od, url, null, "style" );
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	List<Element> getElements( Segment s ) {
		return s.getAllElements( attrname, Pattern.compile( ".*" ) );
	}

	@Override
	String logic( Element e ) {
		final String value = e.getAttributeValue( attrname );
		if( value == null )
			return null;

		return Tools.cssModify( brain, url, value );
	}

}
