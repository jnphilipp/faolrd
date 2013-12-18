package org.lcc;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.lcc.io.FReader;
import org.lcc.parser.json.sites.GoogleWebSearchJSONParser;
import org.lcc.results.google.GoogleResult;
import org.lcc.utils.Helpers;

/**
*
* @author jnphilipp
* @version 0.0.1
*/
public class App {
	public static void main(String[] args) throws Exception {
		Manager manager;
		if ( args.length == 0 )
			manager = Manager.getInstance();
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

			manager = Manager.getInstance(conf, log);
		}

		String wordlist_file = Helpers.getSubUserDir("data") + "/" + manager.getProperty("wordlist.file");
		if ( new File(wordlist_file).exists() ) {
			String[] words = FReader.readLines(wordlist_file);

			for ( String word : words ) {
				GoogleWebSearchJSONParser g = new GoogleWebSearchJSONParser();
				g.setSleep(manager.getLongProperty("GoogleWebSearchJSONParser.sleeptime"));
				g.fetchAll(word);

				System.out.println(g.getMeta().getUrl() + "\nEstimated Count: " + g.getMeta().getEstimatedCount() + "\nCount: " + g.getMeta().getCount() + "\n");
				for ( GoogleResult result : g.getResults() )
					System.out.println(result.getUrl() + "\n" + result.getTitle());
			}
		}
		else
			Manager.error(App.class, "The wordlist file does not exist: " + wordlist_file);
	}
}