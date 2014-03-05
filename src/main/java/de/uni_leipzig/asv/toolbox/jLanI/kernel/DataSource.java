/*
 * $Header: /usr/cvs/toolbox/src/de/uni_leipzig/asv/toolbox/jLanI/kernel/DataSource.java,v 1.4 2007/08/10 15:49:49 lsteiner Exp $
 * 
 * Created on 15.04.2005, 10:39:35 by knorke
 * for project jLanI
 */
package de.uni_leipzig.asv.toolbox.jLanI.kernel;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author knorke
 */
public class DataSource implements Serializable {

	private HashMap wordlist;

	private String name;

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 4459345515315875129L;

	/**
	 * standard constructor
	 * 
	 * @param name -
	 *            the name of the language-wordlist
	 * @param wordlist -
	 *            the map word->probability
	 */
	public DataSource(String name, HashMap wordlist) {
		this.name = name;
		this.wordlist = wordlist;
	}

	/**
	 * returns the probability of a given word "word" or null
	 * 
	 * @param word
	 * @return
	 */
	public Double get(String word) {
		try {
			return (Double) this.wordlist.get(word);
		} catch (Exception e) {
			return null;
		}
	}

	public int size() {
		return this.wordlist.size();
	}

	public String getName() {
		return this.name;
	}

}
