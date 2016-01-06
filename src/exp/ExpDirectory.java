package exp;

import java.io.File;

public class ExpDirectory {

	public static void main( String[] args ) throws Exception {
		File f = new File( "./" );
		System.out.println( "Path : " + f.getPath() );
		System.out.println( "AbsolutePath : " + f.getAbsolutePath() );
		System.out.println( "CanonicalPath : " + f.getCanonicalPath() );
	}

}
