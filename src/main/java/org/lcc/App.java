package org.lcc;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
*
* @author jnphilipp
* @version 0.0.1
*/
public class App {
	public static void main(String[] args) throws Exception {
		if ( args.length == 0 )
			Manager.getInstance();
		else {
			List<String> l = Arrays.asList(args);
			Iterator<String> it = l.iterator();
			String conf = "";
			String log = "";

			while ( it.hasNext() ) {
				switch ( it.next().toString() ) {
					case "-conf": {
						conf = it.next().toString();
					} break;
					case "-log": {
						log = it.next().toString();
					} break;
				}
			}

			Manager.getInstance(conf, log);
		}

		LanguageDetection ld = new LanguageDetection();
		ld.start();
	}
}