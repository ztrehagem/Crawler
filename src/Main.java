
import crawler2.Crawler;

public class Main {

	public static void main( String[] args ) throws Exception {
		//		String url = "http://www.apple.com/";
		//		String url = "http://www.ise.shibaura-it.ac.jp/";
		String url = "http://dengekibunko.jp/newreleases/978-4-04-865133-2/";
		//		String url = "http://www.comitia.co.jp/";
		//		String url = "http://www.comitia.co.jp/ml.html";
		int level = 1;

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

		Crawler.ConnectionNumLimit = 24;

		System.out.println( "Crawler start" );
		new Crawler( url, level ).process();
		System.out.println( "Crawler finish" );
	}
}
