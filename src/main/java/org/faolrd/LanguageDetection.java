package org.faolrd;

import de.uni_leipzig.asv.toolbox.jLanI.kernel.DataSourceException;
import de.uni_leipzig.asv.toolbox.jLanI.kernel.LanIKernel;
import de.uni_leipzig.asv.toolbox.jLanI.kernel.Request;
import de.uni_leipzig.asv.toolbox.jLanI.kernel.RequestException;
import de.uni_leipzig.asv.toolbox.jLanI.kernel.Response;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import org.faolrd.parser.html.sites.HideMyAssHTMLParser;
import org.faolrd.results.Result;
import org.faolrd.utils.Helpers;


/**
 *
 * @author jnphilipp, proewer, Saijin
 * @version 0.0.7
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
		this.language = Manager.getManager().getProperty("wordlist.language");

		if ( !new File(this.wordlistFile).exists() )
			throw new IOException("The wordlist file does not exist: " + this.wordlistFile);

		List<String> queries = new LinkedList<>();
		Set<String[]> sites = new LinkedHashSet<>();
		this.createQueries(queries);

		ProxyManager proxyManager = ProxyManager.getInstance();
		if ( Manager.getManager().getBooleanProperty("faolrd.use_proxies") )
			proxyManager.setProxyParser(new HideMyAssHTMLParser());
		for ( String query : queries ) {
			GoogleWebSearchHTMLParser google = new GoogleWebSearchHTMLParser();
			proxyManager.fetch(query, google);

			for ( Result result : google.getResults() ) {
				if ( Manager.getManager().getBooleanProperty("faolrd.save_all_results") ) {
					List<Object> finalLanguage = new LinkedList<>();
					this.checkURL(result.getURL(), finalLanguage);
					sites.add(new String[]{query, result.getTitle(), result.getURL(),(result.getContent() != null ? result.getContent().replace("\n", " ") : ""), finalLanguage.get(0).toString(), finalLanguage.get(1).toString()});
				}
				else {
					List<Object> finalLanguage = new LinkedList<>();
					if ( this.checkURL(result.getURL(), finalLanguage) )
						sites.add(new String[]{query, result.getTitle(), result.getURL(), result.getContent().replace("\n", " "), finalLanguage.get(0).toString(), finalLanguage.get(1).toString()});
				}
			}

			FileWriter.writeCSV(Helpers.getSubUserDir("data") + "/" + Manager.getManager().getProperty("wordlist.file") + "_result.csv", sites);
		}
	}

	public void createQueries(Collection<String> queries) throws IOException {
		int averageLength = Manager.getManager().getIntegerProperty("query.average_lentgh");
		int maxLength = Manager.getManager().getIntegerProperty("query.max_length");
		int maxQueries = Manager.getManager().getIntegerProperty("query.max_queries");

		Random r = new Random(System.currentTimeMillis());
		List<String[]> csv = new LinkedList<>();
		FileReader.readCSV(this.wordlistFile, csv, ";");
		if ( Manager.getManager().getBooleanProperty("wordlist.has_header") )
			csv.remove(0);

		int[] minMax = Helpers.getMinMax(csv, 1);
		int middleLow = (Integer.parseInt(csv.get(minMax[1])[1])- Integer.parseInt(csv.get(minMax[0])[1])) / 3;
		int highMiddle = 2 * middleLow;
		for ( int i = 0; i < maxQueries; i++ ) {
			String query = "";

			int words = (r.nextInt(maxLength) > averageLength? maxLength: averageLength);
			for ( int j = 0; j < words; j++ ) {
				List<Integer> suddenDeath = new LinkedList<>();
				int next = r.nextInt(csv.size());

				if ( j == 0 ) {
					query += csv.get(next)[0] + " ";
					suddenDeath.add(this.headshot(highMiddle,middleLow , Integer.parseInt(csv.get(next)[1])));
				}
				else {
					int headshot = this.headshot(highMiddle,middleLow , Integer.parseInt(csv.get(next)[1]));
					boolean kill = true;
					for ( int k : suddenDeath )
						if ( headshot == k )
							kill = false;

					if ( kill ) {
						if ( query.contains(csv.get(next)[0]) )
							j--;
						else {
							query += csv.get(next)[0] + " ";
							suddenDeath.add(this.headshot(highMiddle,middleLow , Integer.parseInt(csv.get(next)[1])));
						}
						if ( suddenDeath.size() >= 3 )
							suddenDeath.clear();
					}
					else {
						j--;
					}
				}
			}

			query = query.substring(0, query.length() - 1);
			if ( !queries.contains(query) )
				queries.add(query);
			else {
				i--;
			}
		}
	}

	private int headshot(int a, int b, int c) {
		if ( c > a )
			return 1;
		else if ( b > a )
			return 0;
		else
			return -1;
	}

	/**
	 * 
	 * @param sentence
	 * @param finalLanguage
	 * @return <code>True</code> if the language of the website the same like the language of the given wordlist 
	 * @throws RequestException
	 * @throws DataSourceException 
	 */
	public boolean checkLanguage(String sentence, Collection<Object> finalLanguage) throws RequestException, DataSourceException {
		Set languages = new HashSet();
		int modus = 0;
		boolean reduce = false;
		Request req = new Request(sentence, languages, modus, reduce);
		LanIKernel kernel = LanIKernel.getInstance();
		Response res = kernel.evaluate(req);
		HashMap result = new HashMap(res.getResult());

		String finalLang = "";
		double finalValue = Double.MIN_VALUE;
		Iterator iterator = result.keySet().iterator();
		while ( iterator.hasNext() ) {
			Object key = iterator.next();
			double value = Double.valueOf(result.get(key).toString());
			if ( value > finalValue ) {
				finalValue = value;
				finalLang = key.toString();
			}
		}

		Manager.debug(LanguageDetection.class, "Language: " + this.language, "JLani language: " + finalLang + " (" + finalValue + ")");

		if ( finalLanguage != null ) {
			finalLanguage.add(finalLang);
			finalLanguage.add(finalValue);
		}
		else {
			finalLanguage.add("");
			finalLanguage.add(0);
		}

		return finalLang.equalsIgnoreCase(this.language);
	}

	public boolean checkURL(String url) throws RequestException, DataSourceException {
		return this.checkURL(url, null);
	}

	public boolean checkURL(String url, Collection<Object> finalLanguage) throws RequestException, DataSourceException {
		Manager.debug(LanguageDetection.class, "URL: " + url);
		HTMLParser parser;

		if ( finalLanguage == null )
			finalLanguage = new LinkedList<>();

		if ( !Manager.getManager().getBooleanProperty("faolrd.jlani.check_results") ) {
			finalLanguage.add("");
			finalLanguage.add(0);
			return true;
		}

		try {
			parser = new HTMLParser(url, true);
			if ( !parser.isResponseCodeOK() ) {
				finalLanguage.add("");
				finalLanguage.add(0);
				return false;
			}
			if ( !parser.getContentType().contains("text/html") ) {
				finalLanguage.add("");
				finalLanguage.add(0);
				return false;
			}
		}
		catch ( Exception e ) {
			Manager.debug(LanguageDetection.class, e.toString());
			finalLanguage.add("");
			finalLanguage.add(0);
			return false;
		}

		return checkLanguage(parser.removeAllTags(), finalLanguage);
	}
}