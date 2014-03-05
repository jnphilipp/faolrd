/*
 * $Header: /usr/cvs/toolbox/src/de/uni_leipzig/asv/toolbox/jLanI/kernel/Response.java,v 1.5 2007/08/10 15:49:49 lsteiner Exp $
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
import java.util.*;

/**
 * @author knorke
 * @date Apr 13, 2005 2:26:16 PM
 */
public class Response implements Serializable {

	/**
	 * generated serialVersionUID
	 */
	private static final long serialVersionUID = -3550862422974476882L;

	private HashMap<String, Double> result;

	private HashMap seenWords;

	private int errorcode = 0;

	private int id = -1;

	private int sentenceLength = -1;

	private HashMap coverage;

	private HashMap wordCount;

	/**
	 * @return Returns the result.
	 */
	public HashMap getResult() {
		return result;
	}

	/**
	 * @param result
	 *            The result to set.
	 */
	public void setResult(HashMap<String, Double> result) {
		this.result = result;
	}

	public LangResult getLangResult(double mincov, int mincount) {
		double sum = 0.0;
		Set<String> langSet = result.keySet();
		String[] langAr = langSet.toArray(new String[] {});
		int langCount = langAr.length;
		Double[] valueAr = new Double[langCount];

		for (int i = 0; i < langCount; i++) {
			valueAr[i] = result.get(langAr[i]);
			sum += valueAr[i].doubleValue();
		}

		for (int i = 0; i < 3; i++) {
			for (int j = i + 1; j < langAr.length; j++) {
				if (valueAr[i].doubleValue() < valueAr[j].doubleValue()) {
					String temp = langAr[i];
					langAr[i] = langAr[j];
					langAr[j] = temp;

					Double tmp = valueAr[i];
					valueAr[i] = valueAr[j];
					valueAr[j] = tmp;
				}
			}
		}

		return new LangResult(langAr[0],
				(int) (valueAr[0].doubleValue() / sum * 100), langAr[1],
				(int) (valueAr[1].doubleValue() / sum * 100), this
						.getLanguageCoverage(langAr[0]), this
						.getLanguageWordCount(langAr[0]), mincov, mincount);
	}

	public String toString() {
		StringBuffer ret = new StringBuffer();
		ret.append("[" + this.id + "] Response-Object with values: "
				+ this.result);
		if (this.seenWords != null) {
			ret.append(" seen words: " + this.seenWords);
		}
		return ret.toString();
	}

	/**
	 * @return Returns the errorcode.
	 */
	public int getErrorcode() {
		return errorcode;
	}

	/**
	 * @param errorcode
	 *            The errorcode to set.
	 */
	public void setErrorcode(int errorcode) {
		this.errorcode = errorcode;
	}

	/**
	 * @return Returns the seenWords.
	 */
	public HashMap getSeenWords() {
		return seenWords;
	}

	/**
	 * @param seenWords
	 *            The seenWords to set.
	 */
	public void setSeenWords(HashMap seenWords) {
		this.seenWords = seenWords;
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

	/**
	 * 
	 * @return the coverage of words
	 */
	public HashMap getCoverage() {
		return this.coverage;
	}

	public HashMap getWordCount() {
		return this.wordCount;
	}

	/**
	 * 
	 * @param coverage
	 *            to set ( Map::language->double)
	 */
	public void setCoverage(HashMap coverage) {
		this.coverage = coverage;
	}

	/**
	 * 
	 * @param wordCount
	 *            to set ( Map::language->int)
	 */
	public void setWordCount(HashMap wordCount) {
		this.wordCount = wordCount;
	}

	public double getLanguageCoverage(String lang) {
		if (this.coverage == null) {
			System.err
					.println("Response-Object #"
							+ this.id
							+ ": Attention! this is an reduced dataset! There are no coverage informations available!");
			return -1.0;
		}
		if (this.coverage.containsKey(lang))
			return ((Double) this.coverage.get(lang)).doubleValue();
		else {
			System.err.println("Response-Object #" + this.id
					+ ": Attention! language " + lang + " not found!");
			return -1.0;
		}
	}

	public int getLanguageWordCount(String lang) {
		if (this.coverage == null) {
			System.err
					.println("Response-Object #"
							+ this.id
							+ ": Attention! this is an reduced dataset! There are no coverage informations available!");
			return 0;
		}
		if (this.wordCount.containsKey(lang))
			return ((Integer) this.wordCount.get(lang)).intValue();
		else {
			System.err.println("Response-Object #" + this.id
					+ ": Attention! language " + lang + " not found!");
			return 0;
		}
	}

	/**
	 * @return Returns the sentenceLength.
	 */
	public int getSentenceLength() {
		return this.sentenceLength;
	}

	/**
	 * @param sentenceLength
	 *            The sentenceLength to set.
	 */
	public void setSentenceLength(int sentenceLength) {
		this.sentenceLength = sentenceLength;
	}
}
