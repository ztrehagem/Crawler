package crawler2;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class HTMLMaster implements Master {

	private final Map<String, HTMLMasterMapV> map;

	HTMLMaster() {
		this.map = new HashMap<>();
	}

	@Override
	public String getFileName( final String url ) {
		return map.get( url ).filename;
	}

	void makeStartID( final String url ) {
		if( map.containsKey( url ) )
			throw new RuntimeException();
		map.put( url, new HTMLMasterMapV( "page-root.html", true ) );
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
		map.put( url, new HTMLMasterMapV( "page-" + UUID.randomUUID().toString() + ".html", cansave ) );
	}
}
