package crawler2;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Source;

class HTMLRefactor {

	private final Master			master;
	private final String			url;
	private final Source			src;
	private final int				h;
	private final OutputDocument	od;

	HTMLRefactor( final Master master, final String url, final Source src, final int h ) {
		this.master = master;
		this.url = url;
		this.src = src;
		this.h = h;
		this.od = new OutputDocument( src );
	}

	OutputDocument refactoring() {
		scan( HTMLElementName.IMG, new HTMLTagRefactorIMG( master, url, h, od ) );
		return this.od;
	}

	private void scan( final String tag, final HTMLTagRefactor r ) {
		for( Element e : od.getSegment().getAllElements( tag ) ) {
			r.refactoring( e );
		}
	}
}
