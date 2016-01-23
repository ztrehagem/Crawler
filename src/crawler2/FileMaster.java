package crawler2;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.htmlparser.jericho.Element;

class FileMaster {

	private final Map<String, String> map;

	public FileMaster() {
		this.map = new HashMap<>();
	}

	public String getFileName( final String url ) {
		return map.get( url );
	}

	public TriBool makeID( final String url, final Element e ) {
		String ext = Tools.getExtension( e );
		if( ext == null ) {
			ext = Tools.getExtension( url );
			if( ext == null )
				return TriBool.ERROR;
		}

		return this.makeID( url, ext ) ? TriBool.TRUE : TriBool.FALSE;
	}

	synchronized public boolean makeID( final String url, final String extension ) {
		if( map.containsKey( url ) ) {
			return false;
		}
		map.put( url, "file-" + UUID.randomUUID() + "." + extension );
		return true;
	}
}
