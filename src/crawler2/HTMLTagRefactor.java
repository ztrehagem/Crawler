package crawler2;

import java.util.Map;
import debug.Log;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.OutputDocument;

abstract class HTMLTagRefactor {

	protected final Master			master;
	protected final String			pageurl;
	protected final int				h;
	protected final OutputDocument	od;

	protected HTMLTagRefactor( Master master, String url, int h, OutputDocument od ) {
		this.master = master;
		this.pageurl = url;
		this.h = h;
		this.od = od;
	}

	abstract void refactoring( Element e );

	protected final void modifyRef( final Element e, final String attrname, final String value ) {
		try {
			Map<String, String> map = od.replace( e.getAttributes(), true );
			map.put( attrname, value );
		}
		catch( Exception exc ) {
			Log.e( getClass(), "cant modifyRef : " + exc );
		}
	}
}
