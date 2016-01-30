package crawler2;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class FileMaster implements Master {

	private final Map<String, String>	map;

	private static final String			PREFIX	= "file-";

	FileMaster() {
		this.map = new HashMap<>();
	}

	@Override
	public String getFileName( String url ) {
		return map.get( url );
	}

	synchronized boolean makeID( String url, String extension ) {
		if( map.containsKey( url ) )
			return false;
		register( url, (extension != null) ? "." + extension : "" );
		return true;
	}

	private void register( String url, String suffix ) {
		map.put( url, PREFIX + UUID.randomUUID() + suffix );
	}
}
