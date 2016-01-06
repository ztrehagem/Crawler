package demo;

import java.net.URL;
import java.util.List;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.MasonTagTypes;
import net.htmlparser.jericho.MicrosoftConditionalCommentTagTypes;
import net.htmlparser.jericho.PHPTagTypes;
import net.htmlparser.jericho.Source;

public class DisplayAllElements {

	public static void main( String[] args ) throws Exception {

		String url = "http://megahertz.michikusa.jp";
		if( args.length == 0 ) {
			System.out.println( "using default url [" + url + "]" );
		}
		else {
			url = args[0];
		}

		if( url.indexOf( ":" ) == -1 ) {
			url = "file:" + url;
		}

		MicrosoftConditionalCommentTagTypes.register();
		PHPTagTypes.register();
		PHPTagTypes.PHP_SHORT.deregister();
		MasonTagTypes.register();

		Source source = new Source( new URL( url ) );
		List<Element> el = source.getAllElements();

		for( Element e : el ) {
			System.out.println( "- - - - -" );
			System.out.println( e.getDebugInfo() );
			if( e.getAttributes() != null ) {
				System.out.println( "XHTML StartTag:\n" + e.getStartTag().tidy( true ) );
			}
			System.out.println( "Source text with content:\n" + e );
		}
		System.out.println( source.getCacheDebugInfo() );
	}

}
