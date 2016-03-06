package crawler2;

import java.util.List;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Segment;

abstract class AttributeModifier {

	abstract List<Element> getElements( Segment s );

	abstract String logic( Element e );

	protected final Brain brain;

	private final OutputDocument od;
	protected final String url;
	protected final String tagname;
	protected final String attrname;

	AttributeModifier( Brain brain, OutputDocument od, String url, String tagname, String attrname ) {
		this.brain = brain;
		this.od = od;
		this.url = url;
		this.tagname = tagname;
		this.attrname = attrname;
	}

	final void modify() {
		for( Element e : getElements( od.getSegment() ) ) {
			final String result = logic( e );
			if( result == null )
				continue;
			od.replace( e.getAttributes(), true ).put( attrname.toLowerCase(), result );
		}
	}
}
