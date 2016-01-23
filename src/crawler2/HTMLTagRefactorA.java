package crawler2;

import debug.Log;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.OutputDocument;

public class HTMLTagRefactorA extends HTMLTagRefactor {

	protected HTMLTagRefactorA( Master master, String url, int h, OutputDocument od ) {
		super( master, url, h, od );
	}

	@Override
	void refactoring( Element e ) {
		final String attrname = "href";

		final String href = e.getAttributeValue( attrname );
		if( href == null || href.startsWith( "#" ) )
			return;

		final String fullpath = Tools.makeFullPath( pageurl, href );
		if( fullpath == null ) {
			Log.e( getClass(), "cant make fullpath" );
			return;
		}

		if( !fullpath.startsWith( "http:" ) && !fullpath.startsWith( "https:" ) )
			return;

		final String ext = Tools.getExtension( href );

		final boolean isHTML = ext == null || ext.equals( "html" ) || ext.equals( "htm" );

		if( isHTML ) {
			if( master.h.makeID( fullpath ) && h > 0 )
				master.t.exec( new HTMLSaveRunner( master, fullpath, h ) );
			super.modifyRef( e, attrname, master.h.getFileName( fullpath ) );
		}
		else {
			if( master.f.makeID( fullpath, ext ) )
				master.t.exec( new FileSaveRunner( master, fullpath ) );
			super.modifyRef( e, attrname, master.f.getFileName( fullpath ) );
		}
	}

}
