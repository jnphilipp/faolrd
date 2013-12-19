package org.faolrd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.faolrd.io.FReader;
import org.faolrd.parser.json.sites.GoogleWebSearchJSONParser;
import org.faolrd.utils.Helpers;

/**
 *
 * @author jnphilipp
 * @version 0.0.1
 */
public class LanguageDetection {
	public void start() {
		String wordlist_file = Helpers.getSubUserDir("data") + "/" + Manager.getManager().getProperty("wordlist.file");

		if ( new File(wordlist_file).exists() ) {
			try {
				String[] words = FReader.readLines(wordlist_file);

				for ( String word : words ) {
					GoogleWebSearchJSONParser g = new GoogleWebSearchJSONParser();
					g.setSleep(Manager.getManager().getLongProperty("GoogleWebSearchJSONParser.sleeptime"));
					g.fetchAll(word);

					Manager.info(LanguageDetection.class, word, "Estimated Count: " + g.getMeta().getEstimatedCount(), "Count: " + g.getMeta().getCount() + "\n");
				}
			}
			catch ( FileNotFoundException e ) {
				Manager.error(LanguageDetection.class, e.toString());
			}
			catch ( IOException e ) {
				Manager.error(LanguageDetection.class, e.toString());
			}
			catch ( Exception e ) {
				Manager.error(LanguageDetection.class, e.toString());
			}
		}
		else
			Manager.error(App.class, "The wordlist file does not exist: " + wordlist_file);
	}
}