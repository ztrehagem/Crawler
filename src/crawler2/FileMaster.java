package crawler2;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class FileMaster {

	private final Map<String, String> map;

	FileMaster() {
		this.map = new HashMap<>();
	}

	String getFileName( final String url ) {
		return map.get( url );
	}

	synchronized boolean makeID( final String url, final String extension ) {
		if( map.containsKey( url ) )
			return false;
		map.put( url, "file-" + UUID.randomUUID() + "." + extension );
		return true;
	}
}
