package crawler2;

import debug.Log;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.OutputDocument;

public class HTMLTagRefactorSCRIPT extends HTMLTagRefactor {

	protected HTMLTagRefactorSCRIPT( Master master, String url, int h, OutputDocument od ) {
		super( master, url, h, od );
	}

	@Override
	void refactoring( Element e ) {
		final String attrname = "src";

		final String src = e.getAttributeValue( attrname );
		if( src == null ) {
			return;
		}

		final String fullpath = Tools.makeFullPath( pageurl, src );
		if( fullpath == null ) {
			Log.e( getClass(), "cant make fullpath" );
			return;
		}

		final TriBool t = master.f.makeID( fullpath, e );
		if( t == TriBool.ERROR ) {
			Log.e( getClass(), "cant resolve extenesion '" + src + "'" );
			return;
		}

		final boolean exist = t == TriBool.TRUE ? false : true;

		if( !exist ) {
			master.t.exec( new FileSaveRunner( master, fullpath ) );
		}

		super.modifyRef( e, attrname, master.f.getFileName( fullpath ) );
	}

}
