package org.faolrd;

import de.uni_leipzig.asv.toolbox.jLanI.kernel.DataSourceException;
import de.uni_leipzig.asv.toolbox.jLanI.kernel.LanIKernel;
import de.uni_leipzig.asv.toolbox.jLanI.kernel.Request;
import de.uni_leipzig.asv.toolbox.jLanI.kernel.RequestException;
import de.uni_leipzig.asv.toolbox.jLanI.kernel.Response;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import org.faolrd.io.FReader;
import org.faolrd.net.ProxyManager;
import org.faolrd.parser.html.HTMLParser;
import org.faolrd.parser.html.sites.GoogleWebSearchHTMLParser;
import org.faolrd.parser.html.sites.HideMyAssHTMLParser;
import org.faolrd.results.Meta;
import org.faolrd.results.google.GoogleMeta;
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
	 * Retrieves the word list and queries Google.
	 * @throws Exception 
	 */
	public void start() throws Exception {
		String wordlist_file = Helpers.getSubUserDir("data") + "/" + Manager.getManager().getProperty("wordlist.file");
		if ( !new File(wordlist_file).exists() )
			throw new IOException("The wordlist file does not exist: " + wordlist_file);

		ProxyManager proxyManager = ProxyManager.getInstance();
		proxyManager.setProxyParser(new HideMyAssHTMLParser());
		String[] words = FReader.readLines(wordlist_file);

		for ( String word : words ) {
			GoogleWebSearchHTMLParser google = new GoogleWebSearchHTMLParser();
			proxyManager.fetch(word, google);
			Meta meta = google.getMeta();

			Manager.info(LanguageDetection.class, word, "Estimated Count: " + ((GoogleMeta)meta).getEstimatedCount(), "Count: " + meta.getCount());
		}
	}
	
	/**
	 * 
	 * @param sentence
	 * @return <code>True</code> if the language of the website the same like the language of the given wordlist 
	 * @throws RequestException
	 * @throws DataSourceException 
	 */
	public boolean checkLanguage(String sentence) throws RequestException, DataSourceException
	{
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
			Manager.info("Doc-Language: "+finalLang+"("+finalValue+")");
		
		return finalLang.equalsIgnoreCase(this.language);
	}
	
	public boolean getUrlText(String url) throws Exception
	{
		String text;
		HTMLParser parser = new HTMLParser(url, true);
		text = parser.removeAllTags();
		
		return checkLanguage(text);
	}

	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}
}