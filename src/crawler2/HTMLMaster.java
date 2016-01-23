package crawler2;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class HTMLMaster {

	private final Map<String, String> map;

	public HTMLMaster() {
		this.map = new HashMap<>();
	}

	public String getFileName( final String url ) {
		return map.get( url );
	}

	public boolean makeID( final String url ) {
		return this.makeID( url, false );
	}

	synchronized public boolean makeID( String url, boolean start ) {
		if( map.containsKey( url ) ) {
			return false;
		}
		final String id = start ? "start" : UUID.randomUUID().toString();
		final String filename = "page-" + id + ".html";
		map.put( url, filename );
		return true;
	}
}
