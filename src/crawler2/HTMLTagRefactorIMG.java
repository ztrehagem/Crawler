package crawler2;

import debug.Log;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.OutputDocument;

class HTMLTagRefactorIMG extends HTMLTagRefactor {

	HTMLTagRefactorIMG( Master master, String pageurl, int h, OutputDocument od ) {
		super( master, pageurl, h, od );
	}

	@Override
	void refactoring( Element e ) {
		final String attrname = "src";

		final String src = e.getAttributeValue( attrname );
		if( src == null ) {
			Log.e( getClass(), "img has no src attribute" );
			return;
		}

		final String ext = Tools.getExtension( src );
		if( ext == null ) {
			Log.e( getClass(), "img src has no extension" );
			return;
		}

		final String fullpath = Tools.makeFullPath( pageurl, src );
		if( fullpath == null ) {
			Log.e( getClass(), "cant make fullpath" );
			return;
		}

		final TriBool t = master.f.makeID( fullpath, e );
		if( t == TriBool.ERROR ) {
			Log.e( getClass(), "cant resolve extenesion" );
			return;
		}

		final boolean exist = t == TriBool.TRUE ? false : true;

		final String name = master.f.getFileName( fullpath );

		if( !exist ) {
			master.t.exec( new FileSaveRunner( master, fullpath ) );
		}

		super.modifyRef( e, attrname, name );
	}
}
