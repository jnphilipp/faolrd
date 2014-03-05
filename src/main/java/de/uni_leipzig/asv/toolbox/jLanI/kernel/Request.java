/*
 * $Header: /usr/cvs/toolbox/src/de/uni_leipzig/asv/toolbox/jLanI/kernel/Request.java,v 1.6 2007/08/10 15:49:49 lsteiner Exp $
 * 
 * Created on Apr 13, 2005
 * by knorke
 * 
 * package jLanI
 * for jLanI project
 *
 */
package de.uni_leipzig.asv.toolbox.jLanI.kernel;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author knorke
 * @date Apr 13, 2005 2:24:03 PM
 */
public class Request implements Serializable {

	/**
	 * generated serialVersionUID
	 */
	private static final long serialVersionUID = 3216123764097547843L;

	/**
	 * the sentence to check
	 */
	private String sentence;

	/**
	 * the list of languages to check for
	 */
	private Set languages;

	/**
	 * not implemented yet
	 */
	private int modus;

	/**
	 * reduce the Response for faster processing?
	 */
	private boolean reduce;

	/**
	 * request-id to identify the correct response later
	 */
	private int id;

	/**
	 * how many words of the given data/sentence lani has to check
	 */
	private int wordsToCheck;

	/**
	 * instantiate the attributes to prevent nullpointer-exceptions ans save
	 * some cputime for checking against null-objects later
	 * 
	 * @throws RequestException
	 * 
	 */
	public Request() throws RequestException {
		this("", new HashSet(), 0, !true);
	}

	/**
	 * 
	 * @param sentence
	 *            the sentence to check
	 * @param languages
	 *            the languages, empty (but not null!) set allowed, so check
	 *            against all known langs
	 * @param modus
	 *            not implemented yet
	 * @param reduce
	 *            reduce the (verbose-)output for better performance
	 * @throws RequestException
	 */
	public Request(String sentence, Set languages, int modus, boolean reduce)
			throws RequestException {
		if (sentence == null)
			throw new RequestException("sentence cannot be null!");

		this.sentence = sentence;
		if (languages == null)
			throw new RequestException("the set of languages cannot be null!");
		this.languages = languages;
		this.modus = modus;
		this.reduce = reduce;
		this.id = -1;
		this.wordsToCheck = 0;
	}

	/**
	 * @return Returns the languages.
	 */
	public Set getLanguages() {
		return languages;
	}

	/**
	 * @param languages
	 *            The languages to set.
	 * @throws RequestException
	 */
	public void setLanguages(Set languages) throws RequestException {
		if (languages == null)
			throw new RequestException("the set of languages cannot be null!");
		this.languages = languages;
	}

	/**
	 * @return Returns the sentence.
	 */
	public String getSentence() {
		return sentence;
	}

	/**
	 * @param sentence
	 *            The sentence to set.
	 * @throws RequestException
	 */
	public void setSentence(String sentence) throws RequestException {
		if (sentence == null)
			throw new RequestException("sentence cannot be null!");
		this.sentence = sentence;
	}

	/**
	 * @return Returns the reduce.
	 */
	public boolean isReduced() {
		return reduce;
	}

	/**
	 * reduce the response to save some cpu-time and memory? please read the
	 * fine manual for details
	 * 
	 * @param reduce
	 *            The reduce to set.
	 */
	public void setReduce(boolean reduce) {
		this.reduce = reduce;
	}

	/**
	 * @return Returns the amount of words of the sentence to check (the upper
	 *         limit)
	 */
	public int getWordsToCheck() {
		return wordsToCheck;
	}

	/**
	 * @param wordsToCheck
	 *            The wordsToCheck to set. 0 means "check all words" (default)
	 */
	public void setWordsToCheck(int wordsToCheck) {
		if (wordsToCheck < 0) {
			System.err
					.println("upper limit must be greater than or equal to 0, reset value to default 0");
			wordsToCheck = 0;
		}

		this.wordsToCheck = wordsToCheck;
	}

	/**
	 * @return Returns the modus.
	 */
	public int getModus() {
		return modus;
	}

	/**
	 * @param modus
	 *            The modus to set.
	 */
	public void setModus(int modus) {
		this.modus = modus;
	}

	/**
	 * @return Returns the id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            The id to set.
	 */
	public void setId(int id) {
		this.id = id;
	}
}
