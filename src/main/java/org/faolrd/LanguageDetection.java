package org.faolrd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.faolrd.io.FReader;
import org.faolrd.parser.json.sites.GoogleWebSearchJSONParser;
import org.faolrd.utils.Helpers;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import de.uni_leipzig.asv.toolbox.jLanI.kernel.DataSourceException;
import de.uni_leipzig.asv.toolbox.jLanI.kernel.LanIKernel;
import de.uni_leipzig.asv.toolbox.jLanI.kernel.Request;
import de.uni_leipzig.asv.toolbox.jLanI.kernel.RequestException;
import de.uni_leipzig.asv.toolbox.jLanI.kernel.Response;

/**
 *
 * @author jnphilipp, proewer
 * @version 0.0.1
 */
public class LanguageDetection {
	
	/**
	 * language
	 */
	private String language;
	
	/**
	 * build the google results
	 */
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