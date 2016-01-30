package crawler2;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class HTMLMaster implements Master {

	private final Map<String, HTMLMasterMapV>	map;
	private static final String					PREFIX						= "page-";
	private static final String					SUFFIX						= ".html";
	private static final String					default_startPageFileName	= PREFIX + "root" + SUFFIX;

	HTMLMaster() {
		this.map = new HashMap<>();
	}

	@Override
	public String getFileName( String url ) {
		return map.get( url ).filename;
	}

	void makeStartID( String url ) {
		makeStartID( url, default_startPageFileName );
	}

	void makeStartID( String url, String filetitle ) {
		if( map.containsKey( url ) )
			throw new RuntimeException();
		map.put( url, new HTMLMasterMapV( PREFIX + filetitle + SUFFIX, true ) );
	}

	// return : allow do save to thread
	synchronized boolean makeID( String url, int h ) {
		final boolean cansave = h > 0;

		if( !map.containsKey( url ) ) {
			register( url, cansave );
			return cansave;
		}
		if( cansave ) {
			HTMLMasterMapV v = map.get( url );
			if( !v.saved )
				return v.saved = true;
		}
		return false;
	}

	private void register( String url, boolean cansave ) {
		map.put( url, new HTMLMasterMapV( PREFIX + UUID.randomUUID().toString() + SUFFIX, cansave ) );
	}
}
