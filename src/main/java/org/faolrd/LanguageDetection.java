package org.faolrd;

import de.uni_leipzig.asv.toolbox.jLanI.kernel.DataSourceException;
import de.uni_leipzig.asv.toolbox.jLanI.kernel.LanIKernel;
import de.uni_leipzig.asv.toolbox.jLanI.kernel.Request;
import de.uni_leipzig.asv.toolbox.jLanI.kernel.RequestException;
import de.uni_leipzig.asv.toolbox.jLanI.kernel.Response;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import org.faolrd.net.ProxyManager;
import org.faolrd.parser.html.sites.HideMyAssHTMLParser;
import org.faolrd.parser.json.sites.GoogleWebSearchJSONParser;
import org.faolrd.utils.Helpers;

/**
 *
 * @author jnphilipp
 * @version 0.0.3
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

		ProxyManager proxyManager = new ProxyManager();
		proxyManager.setParser(new GoogleWebSearchJSONParser());
		proxyManager.setProxyParser(new HideMyAssHTMLParser());
		proxyManager.loadProxies();
		/*if ( new File(wordlist_file).exists() ) {
			String[] words = FReader.readLines(wordlist_file);

			for ( String word : words ) {
				proxyManager.fetch(word);
				GoogleWebSearchJSONParser g = (GoogleWebSearchJSONParser)proxyManager.getParser();

				Manager.info(LanguageDetection.class, word, "Estimated Count: " + ((GoogleMeta)g.getMeta()).getEstimatedCount(), "Count: " + g.getMeta().getCount() + "\n");
			}
		}
		else
			Manager.error(App.class, "The wordlist file does not exist: " + wordlist_file);*/
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