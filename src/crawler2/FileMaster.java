package crawler2;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class FileMaster implements Master {

	private final Map<String, String> map;

	FileMaster() {
		this.map = new HashMap<>();
	}

	@Override
	public String getFileName( final String url ) {
		return map.get( url );
	}

	synchronized boolean makeID( final String url, final String extension ) {
		if( map.containsKey( url ) )
			return false;
		final String suffix = (extension != null) ? "." + extension : "";
		map.put( url, "file-" + UUID.randomUUID() + suffix );
		return true;
	}
}
