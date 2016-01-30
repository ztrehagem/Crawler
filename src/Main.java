
import crawler2.Crawler;

public class Main {

	public static void main( String[] args ) throws Exception {
		//		String url = "http://www.apple.com/";
		String url = "http://www.ise.shibaura-it.ac.jp/";
		//		String url = "http://dengekibunko.jp/newreleases/978-4-04-865133-2/";
		//		String url = "http://www.comitia.co.jp/";
		//		String url = "http://www.comitia.co.jp/ml.html";
		//		String url = "https://www.google.co.jp";
		//		String url = "http://www.yahoo.co.jp/";
		int level = 1;
		//		int level = 2;

		if( args.length == 0 ) {
			System.out.println( "using default '" + level + " " + url + "'" );
		}
		else if( args.length == 1 ) {
			level = Integer.parseInt( args[0] );
			System.out.println( "using default url '" + url + "'" );
		}
		else if( args.length >= 2 ) {
			url = args[1];
		}

		System.out.println( "\n----- Crawler start -----\n" );

		new Crawler( url, level, 24, true ).exec();

		System.out.println( "\n----- Crawler finish -----\n" );
	}
}
