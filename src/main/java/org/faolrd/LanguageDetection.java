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
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.faolrd.io.FileReader;
import org.faolrd.io.FileWriter;
import org.faolrd.net.ProxyManager;
import org.faolrd.parser.html.HTMLParser;
import org.faolrd.parser.html.sites.GoogleWebSearchHTMLParser;
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
		Set<String[]> sites = new LinkedHashSet<>();
		this.createQueries(queries);

		ProxyManager proxyManager = ProxyManager.getInstance();
		//proxyManager.setProxyParser(new HideMyAssHTMLParser());
		for ( String query : queries ) {
			GoogleWebSearchHTMLParser google = new GoogleWebSearchHTMLParser();
			proxyManager.fetch(query, google);

			for ( Result result : google.getResults() ) {
				if ( this.checkURL(result.getURL()) )
					sites.add(new String[]{result.getTitle(), result.getURL(), result.getContent()});
			}
		}

		FileWriter.writeCSV(Helpers.getSubUserDir("data") + "/" + Manager.getManager().getProperty("wordlist.file") + "_result.csv", sites);
	}

	public void createQueries(Collection<String> queries) throws IOException {
		int averageLength = Manager.getInstance().getIntegerProperty("query.average_lentgh");
		int maxLength = Manager.getInstance().getIntegerProperty("query.max_length");
		int maxQueries = Manager.getInstance().getIntegerProperty("query.max_queries");

		Random r = new Random(System.currentTimeMillis());

		String[] words = FileReader.readLines(this.wordlistFile);
		for ( int i = 0; i < maxQueries; i++ ) {
			String query = "";
			for ( int j = 0; j < averageLength; j++ ) {
				int next = r.nextInt(words.length);
				if ( query.contains(words[next]) )
					j--;
				else
					query += words[next] + " ";
			}
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
			Manager.debug(LanguageDetection.class, "Language: "+finalLang+"("+finalValue+")");
		
		return finalLang.equalsIgnoreCase(this.language);
	}

	public boolean checkURL(String url) throws RequestException, DataSourceException {
		Manager.debug(LanguageDetection.class, "URL: " + url);
		HTMLParser parser;

		if ( !Manager.getManager().getBooleanProperty("jlani.check_results") )
			return true;

		try {
			parser = new HTMLParser(url, true);
			if ( !parser.isResponseCodeOK() )
				return false;
		}
		catch ( Exception e ) {
			Manager.debug(LanguageDetection.class, e.toString());
			return false;
		}

		return checkLanguage(parser.removeAllTags());
	}
}