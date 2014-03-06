package org.faolrd;

import java.io.File;
import org.faolrd.io.FReader;
import org.faolrd.net.ProxyManager;
import org.faolrd.parser.html.sites.HideMyAssHTMLParser;
import org.faolrd.parser.json.sites.GoogleWebSearchJSONParser;
import org.faolrd.utils.Helpers;

/**
 *
 * @author jnphilipp
 * @version 0.0.2
 */
public class LanguageDetection {
	public void start() throws Exception {
		String wordlist_file = Helpers.getSubUserDir("data") + "/" + Manager.getManager().getProperty("wordlist.file");

		ProxyManager proxyManager = new ProxyManager();
		proxyManager.setParser(new GoogleWebSearchJSONParser());
		proxyManager.setProxyParser(new HideMyAssHTMLParser());
		if ( new File(wordlist_file).exists() ) {
			String[] words = FReader.readLines(wordlist_file);

			for ( String word : words ) {
				proxyManager.fetch(word);
				GoogleWebSearchJSONParser g = (GoogleWebSearchJSONParser)proxyManager.getParser();

				Manager.info(LanguageDetection.class, word, "Estimated Count: " + g.getMeta().getEstimatedCount(), "Count: " + g.getMeta().getCount() + "\n");
			}
		}
		else
			Manager.error(App.class, "The wordlist file does not exist: " + wordlist_file);
	}
}