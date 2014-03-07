package org.faolrd;

import de.uni_leipzig.asv.toolbox.jLanI.kernel.DataSourceException;
import de.uni_leipzig.asv.toolbox.jLanI.kernel.LanIKernel;
import de.uni_leipzig.asv.toolbox.jLanI.kernel.Request;
import de.uni_leipzig.asv.toolbox.jLanI.kernel.RequestException;
import de.uni_leipzig.asv.toolbox.jLanI.kernel.Response;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.faolrd.io.FReader;
import org.faolrd.net.ProxyManager;
import org.faolrd.parser.html.HTMLParser;
import org.faolrd.parser.html.sites.GoogleWebSearchHTMLParser;
import org.faolrd.parser.html.sites.HideMyAssHTMLParser;
import org.faolrd.results.Result;
import org.faolrd.utils.Helpers;

/**
 *
 * @author jnphilipp, proewer
 * @version 0.0.5
 */
public class LanguageDetection {	
	/**
	 * language
	 */
	private String language;
	/**
	 * wordlist
	 */
	private String wordlistFile;

	/**
	 * Retrieves the word list and queries Google.
	 * @throws Exception 
	 */
	public void start() throws Exception {
		this.wordlistFile = Helpers.getSubUserDir("data") + "/" + Manager.getManager().getProperty("wordlist.file");
		this.language = Manager.getInstance().getProperty("wordlist.language");

		if ( !new File(this.wordlistFile).exists() )
			throw new IOException("The wordlist file does not exist: " + this.wordlistFile);

		List<String> queries = new LinkedList<>();
		this.createQueries(queries);

		ProxyManager proxyManager = ProxyManager.getInstance();
		proxyManager.setProxyParser(new HideMyAssHTMLParser());

		for ( String query : queries ) {
			GoogleWebSearchHTMLParser google = new GoogleWebSearchHTMLParser();
			proxyManager.fetch(query, google);

			for ( Result result : google.getResults() ) {
				this.checkLanguage(language);
			}
		}
	}

	public void createQueries(Collection<String> queries) throws IOException {
		int averageLength = Manager.getInstance().getIntegerProperty("query.average_lentgh");
		int maxLength = Manager.getInstance().getIntegerProperty("query.max_length");
		int maxQueries = Manager.getInstance().getIntegerProperty("query.max_queries");

		Random r = new Random(System.currentTimeMillis());

		String[] words = FReader.readLines(this.wordlistFile);
		for ( int i = 0; i < maxQueries; i++ ) {
			String query = "";
			for ( int j = 0; j < averageLength; j++ )
				query += r.nextInt(words.length) + " ";
			query = query.substring(0, query.length() - 1);
			queries.add(query);
		}
	}

	/**
	 * 
	 * @param sentence
	 * @return <code>True</code> if the language of the website the same like the language of the given wordlist 
	 * @throws RequestException
	 * @throws DataSourceException 
	 */
	public boolean checkLanguage(String sentence) throws RequestException, DataSourceException {
			Set languages = new HashSet();
			int modus = 0;
			boolean reduce = false;
			Request req = new Request(sentence, languages, modus, reduce);
			LanIKernel kernel = LanIKernel.getInstance();
			Response res = kernel.evaluate(req);
			Hashtable result = new Hashtable(res.getResult());
			
			Enumeration enumeration = result.keys();
			double finalValue = 0;
			String finalLang = "";
			
			while(enumeration.hasMoreElements())
			{
				Object key = enumeration.nextElement();
				Object value = result.get(key);
				double val = ((Double)value).doubleValue();
				if(val > finalValue)
				{
					finalValue = val;
					finalLang = ""+key;
				}
			}
			Manager.debug(LanguageDetection.class, "language: "+finalLang+"("+finalValue+")");
		
		return finalLang.equalsIgnoreCase(this.language);
	}

	public boolean getUrlText(String url) throws Exception {
		String text;
		HTMLParser parser = new HTMLParser(url, true);
		text = parser.removeAllTags();

		return checkLanguage(text);
	}
}