package crawler2;

class CSSModifier {

	private CSSModifier() {
	}

	static String modify( Brain brain, String url, String line ) {

		final StringBuilder sb = new StringBuilder();
		int last = 0;
		int head = 0;

		while( (head = line.indexOf( "url(", last )) != -1 ) {
			sb.append( line.substring( last, head ) );

			final char quotechar = line.charAt( head + 4 );
			final boolean quote = quotechar == '\'' || quotechar == '"';

			final int start = head + 4 + (quote ? 1 : 0);
			final int end = line.indexOf( ")", start ) - (quote ? 1 : 0);

			if( end <= -1 ) {
				last = head;
				break;
			}

			last = end + 1 + (quote ? 1 : 0);

			final String target = line.substring( start, end );
			final String ext = StrUtil.getExtension( target );
			final String fullpath = StrUtil.makeFullPath( url, target );

			if( brain.f.makeID( fullpath, ext ) )
				brain.t.offer( new FileSaveRunner( brain, fullpath ) );

			sb.append( "url(\"" ).append( brain.f.getFileName( fullpath ) ).append( "\")" );
		}

		sb.append( line.substring( last ) );

		return sb.toString();
	}
}
