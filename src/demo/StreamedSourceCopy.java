package demo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import net.htmlparser.jericho.CharacterReference;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.StreamedSource;
import net.htmlparser.jericho.Tag;

public class StreamedSourceCopy {

	public static void main( String[] args ) throws Exception {
		String sourceUrlString = "data/test.html";
		if( args.length == 0 )
			System.err.println( "Using default argument of \"" + sourceUrlString + '"' );
		else
			sourceUrlString = args[0];
		if( sourceUrlString.indexOf( ':' ) == -1 )
			sourceUrlString = "file:" + sourceUrlString;
		StreamedSource streamedSource = new StreamedSource( new URL( sourceUrlString ) );
		//streamedSource.setBuffer(new char[65000]); // uncomment this to use a fixed buffer size
		Writer writer = null;
		try {
			writer = new OutputStreamWriter( new FileOutputStream( "StreamedSourceCopyOutput.html" ),
				streamedSource.getEncoding() );
			System.out.println( "Processing segments:" );
			int lastSegmentEnd = 0;
			for( Segment segment : streamedSource ) {
				System.out.println( segment.getDebugInfo() );
				if( segment.getEnd() <= lastSegmentEnd )
					continue; // if this tag is inside the previous tag (e.g. a server tag) then ignore it as it was already output along with the previous tag.
				lastSegmentEnd = segment.getEnd();
				if( segment instanceof Tag ) {
					Tag tag = (Tag)segment;
					// HANDLE TAG
					// Uncomment the following line to ensure each tag is valid XML:
					// writer.write(tag.tidy()); continue;
				}
				else if( segment instanceof CharacterReference ) {
					CharacterReference characterReference = (CharacterReference)segment;
					// HANDLE CHARACTER REFERENCE
					// Uncomment the following line to decode all character references instead of copying them verbatim:
					// characterReference.appendCharTo(writer); continue;
				}
				else {
					// HANDLE PLAIN TEXT
				}
				// unless specific handling has prevented getting to here, simply output the segment as is:
				writer.write( segment.toString() );
			}
			writer.close();
			System.err.println( "\nA copy of the source document has been output to StreamedSourceCopyOuput.html" );
		}
		catch( Exception ex ) {
			if( writer != null )
				try {
					writer.close();
				}
				catch( IOException ex2 ) {
				}
			throw ex;
		}
	}
}
