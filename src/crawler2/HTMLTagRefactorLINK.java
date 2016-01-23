package crawler2;

import debug.Log;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.OutputDocument;

public class HTMLTagRefactorLINK extends HTMLTagRefactor {

	protected HTMLTagRefactorLINK( Master master, String url, int h, OutputDocument od ) {
		super( master, url, h, od );
	}

	@Override
	void refactoring( Element e ) {
		final String rel = e.getAttributeValue( "rel" );
		if( rel == null || !rel.equals( "stylesheet" ) ) {
			return;
		}

		final String attrname = "href";

		final String href = e.getAttributeValue( attrname );
		if( href == null ) {
			Log.e( getClass(), "link has no href attribute" );
			return;
		}

		final String fullpath = Tools.makeFullPath( pageurl, href );
		if( fullpath == null ) {
			Log.e( getClass(), "cant make fullpath" );
			return;
		}

		final TriBool t = master.f.makeID( fullpath, e );
		if( t == TriBool.ERROR ) {
			Log.e( getClass(), "cant resolve extension '" + href + "'" );
			return;
		}

		final boolean exist = t == TriBool.TRUE ? false : true;

		if( !exist ) {
			master.t.exec( new FileSaveRunner( master, fullpath ) );
		}

		super.modifyRef( e, attrname, master.f.getFileName( fullpath ) );
	}

}
