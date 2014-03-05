/*
 * $Header: /usr/cvs/toolbox/src/de/uni_leipzig/asv/toolbox/jLanI/tools/LanguageContainer.java,v 1.6 2007/08/10 15:49:48 lsteiner Exp $
 * Created on 16.08.2005
 *
 */
package de.uni_leipzig.asv.toolbox.jLanI.tools;

/**
 * @author Michael Welt
 * 
 */
public class LanguageContainer {
	private static LanguageContainer singleton = null;

	private String[] languages = null;

	private String[] defaults = { "de", "en", "fr" };

	private LanguageContainer() {
		languages = defaults;
	}

	public static LanguageContainer getInstance() {
		if (singleton == null)
			singleton = new LanguageContainer();
		return singleton;
	}

	public void setLanguages(String[] lans) {
		if (lans != null && lans.length > 0)
			this.languages = lans;
	}

	public String[] getLanguages() {
		return this.languages;
	}
}
