/*
 * $Header: /usr/cvs/toolbox/src/de/uni_leipzig/asv/toolbox/jLanI/kernel/DatasourceManager.java,v 1.9 2007/08/10 15:49:49 lsteiner Exp $
 * 
 * Created on Apr 20, 2005
 * by knorke
 * 
 * package jLanI
 * for jLanI project
 *
 */
package de.uni_leipzig.asv.toolbox.jLanI.kernel;

//import de.uni_leipzig.asv.toolbox.jLanI.gui.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author knorke
 * @date Apr 20, 2005 7:48:48 AM
 */
public class DatasourceManager {

	private static DatasourceManager instance = null;

	public HashMap datasources;

	private double REST;

	private String wordlistseparator = " ";

	/**
	 * private singleton constructor
	 * 
	 */
	private DatasourceManager() {
		this.datasources = new HashMap();

		// TODO: Rest-Wert aus dem configfile holen
		this.REST = 0.1;
	}

	/**
	 * get singleton instance
	 * 
	 * @return
	 */
	public static DatasourceManager getInstance() {
		if (instance == null)
			instance = new DatasourceManager();
		return instance;
	}

	/**
	 * adds a datasource from wordlist and tokenfile
	 * 
	 * @param name
	 * @param listfile
	 * @param tokenfile
	 * @return
	 * @throws DataSourceException
	 */
	public boolean addDatasource(String name, String listfile, String tokenfile)
			throws DataSourceException {

		if (listfile == null) {
			throw new DataSourceException("listfile cannot be null!");
		}

		// if ( this.datasources.keySet().contains( name ) ) {
		// System.err
		// .println( "WARNING! datasourcemanager already contains language '"
		// + name + "'! i will NOT overwrite this." );
		// return false;
		// }

		HashMap wordlist = new HashMap();

		int token = 0;
		// token = this.parseTokenFile( tokenfile );
		// if ( token <= 0 ) {
		// throw new DataSourceException(
		// "token (size of reference corpus) cannot be " + token
		// + "!" );
		// }
		wordlist = this.parseWordlistFile(listfile, token);

		DataSource newsource = new DataSource(name, wordlist);
		this.datasources.put(name, newsource);

		return true;
	}

	/**
	 * loads a datasource from file filename and put it into the list of
	 * datasources
	 * 
	 * @param filename -
	 *            a file with da dump'd datasource
	 * @return true is good, false is bad ;)
	 */
	public boolean addSerializedDatasource(String filename) {

		ObjectInputStream is = null;
		DataSource newsource = null;

		try {
			is = new ObjectInputStream(new GZIPInputStream(new FileInputStream(
					filename)));
		} catch (FileNotFoundException e) {
			System.err.println("cannot find file '" + filename + "'!");
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			System.err.println("cannot open file '" + filename + "'!");
			e.printStackTrace();
			return false;
		}

		try {
			newsource = (DataSource) is.readObject();
		} catch (IOException e1) {
			System.err.println("cannot read from file '" + filename + "'!");
			e1.printStackTrace();
			return false;
		} catch (ClassNotFoundException e1) {
			System.out.println("cannot find class!");
			e1.printStackTrace();
			return false;
		}

		try {
			is.close();
		} catch (IOException e2) {
			System.err.println("cannot close file '" + filename + "'!");
			e2.printStackTrace();
			return false;
		}

		if (this.datasources.containsKey(newsource.getName())) {
			System.err.println("WARNING! overwriting existing '"
					+ newsource.getName() + "' with the datasource from '"
					+ filename + "'!");
		}

		this.datasources.put(newsource.getName(), newsource);

		return true;
	}

	/**
	 * reset, like die LaniKernel.reset()
	 * 
	 */
	public void reset() {
		this.datasources = null;
		DatasourceManager.instance = null;
	}

	/**
	 * writes a datasource to disc for faster loading or dynamic loading
	 * 
	 * @param name
	 * @param filename
	 * @return
	 */
	public boolean serializeDatasource(String name, String filename) {

		ObjectOutputStream os = null;

		// open the file
		try {
			os = new ObjectOutputStream(new GZIPOutputStream(
					new FileOutputStream(filename)));
		} catch (FileNotFoundException e) {
			System.err.println("cannot find file '" + filename + "'!");
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			System.err.println("cannot open file '" + filename + "'!");
			e.printStackTrace();
			return false;
		}

		// write the datasource
		try {
			os.writeObject(this.datasources.get(name));
		} catch (IOException e1) {
			System.err.println("cannot write the object stream to file '"
					+ filename + "'!");
			e1.printStackTrace();
			return false;
		}

		// and close the file
		try {
			os.close();
		} catch (IOException e2) {
			System.err.println("cannot close file '" + filename + "'!");
			e2.printStackTrace();
			return false;
		}

		// all fine? -> true
		return true;
	}

	/**
	 * return true, if language is known by the datasourcemanager
	 * 
	 * @param language
	 * @return true if language is in the datasourcemanager
	 */
	public boolean hasLanguage(String language) {
		return this.datasources.containsKey(language);
	}

	/**
	 * 
	 * @param languages
	 * @param word
	 * @return
	 * @throws DataSourceException
	 */
	public HashMap[] getEvaluationData(Set languages, String word,
			boolean reduced) throws DataSourceException {

		int num_langs = languages.size();
		String temp = null;

		if (!this.datasources.keySet().containsAll(languages)) {
			System.out.println("given languages '" + languages
					+ "' is not a subset of '" + this.datasources.keySet()
					+ "'");
			throw new DataSourceException("one or some languages in '"
					+ languages + "' are not known by DatasourceManager!");

		}

		HashMap retvalues[] = new HashMap[2];
		HashMap ret = new HashMap();
		if (!reduced)
			retvalues[1] = new HashMap();
		Double prob = null;
		String lang = null;
		double probSum = 0.0;
		int failed = 0;

		// get the probabilities
		for (Iterator iter = languages.iterator(); iter.hasNext();) {
			lang = (String) iter.next();
			// System.out.println("lang: " + lang);
			prob = ((DataSource) this.datasources.get(lang)).get(word);

			// word not in the DataSource-container for this language
			if (prob == null) {
				failed++;
				ret.put(lang, null);
			} else {
				probSum += prob.doubleValue();
				ret.put(lang, prob);
				if (!reduced)
					retvalues[1].put(lang, null);
			}
		}

		// System.out.println( "a(" + word + ") ret: " + ret );

		// calculating REST
		// it's tricky if the given word is not in any wordlist
		double rest;
		if (failed == num_langs)
			rest = 1.0;
		else
			rest = (this.REST / (double) num_langs) * probSum;

		// inserting REST
		for (Iterator iter = ret.keySet().iterator(); iter.hasNext();) {
			lang = (String) iter.next();
			if (ret.get(lang) == null) {
				ret.put(lang, new Double(rest));
				probSum += rest;
			}
		}

		// System.out.println( "b(" + word + ") ret: " + ret );

		// normalise values to 1.0
		for (Iterator iter = ret.keySet().iterator(); iter.hasNext();) {
			lang = (String) iter.next();
			ret.put(lang, new Double(((Double) ret.get(lang)).doubleValue()
					* (double) num_langs / probSum));
		}

		// System.out.println( "c(" + word + ") ret: " + ret );

		retvalues[0] = ret;
		return retvalues;
	}

	/**
	 * some statistics
	 */
	public String toString() {
		StringBuffer ret = new StringBuffer();

		ret.append("DatasourceManager contains:\n");
		ret.append("  " + this.datasources.size() + " languages:");
		ret.append(" " + this.datasources.keySet() + "\n");

		DataSource temp = null;
		int size = 0;
		for (Iterator iter = this.datasources.values().iterator(); iter
				.hasNext();) {
			temp = (DataSource) iter.next();
			size += temp.size();
		}
		ret.append("  " + size + " words");

		return ret.toString();

	}

	/**
	 * adds a new datasource
	 * 
	 * @param source -
	 *            from deserialisation
	 * @return false, if an error occurs
	 */
	private boolean addDatasource(DataSource source) {

		if (source == null) {
			System.err.println("cannot add the null-datasource");
			return false;
		}
		// System.out.println("= "+this.datasources.keySet());

		if (this.datasources.containsKey(source.getName())) {

			System.err
					.println("WARNING! datasourcemanager already contains language '"
							+ source.getName()
							+ "'! i will NOT overwrite this.");
			return false;
		}

		this.datasources.put(source.getName(), source);

		return true;
	}

	/**
	 * opens the tokenfile, read it, parse output and return the token from that
	 * file
	 * 
	 * @param tokenfile
	 * @return tokencount
	 * @throws DataSourceException
	 */
	private int parseTokenFile(String tokenfile) throws DataSourceException {

		int token = 0;
		LineNumberReader tokenf;

		// open tokenfile
		try {
			tokenf = new LineNumberReader(new FileReader(tokenfile));
		} catch (FileNotFoundException e) {
			System.err.println("cannot open tokenfile '" + tokenfile + "'!");
			throw new DataSourceException(e.getLocalizedMessage());
		}

		// read tokenfile
		try {

			token = Integer.parseInt(tokenf.readLine());
		} catch (IOException e1) {
			System.err.println("cannot read from tokenfile!");
			throw new DataSourceException(e1.getLocalizedMessage());
		} catch (NumberFormatException e2) {
			System.err.println("wrong fileformat in " + tokenfile
					+ "! not an integer!");
			throw new DataSourceException(e2.getLocalizedMessage());
		}

		System.out.println("token (from " + tokenfile + "): " + token);

		return token;
	}

	/**
	 * opens the wordlistfile, read it, parse it, fill the hashmap with the
	 * wordlist's words and calculate the probability
	 * 
	 * @param listfile
	 * @param token
	 * @return
	 * @throws DataSourceException
	 */
	private HashMap parseWordlistFile(String listfile, int tok)
			throws DataSourceException {

		int token = 0;
		HashMap wordmap = new HashMap();
		LineNumberReader wordlist;

		// open files
		try {
			wordlist = new LineNumberReader(new FileReader(listfile));
		} catch (FileNotFoundException e) {
			System.err.println("cannot open wordlistfile '" + listfile + "'!");
			throw new DataSourceException(e.getLocalizedMessage());
		}

		try {
			token = Integer.parseInt(wordlist.readLine());
		} catch (IOException e1) {
			System.err.println("cannot read token!");
			throw new DataSourceException(e1.getLocalizedMessage());
		} catch (NumberFormatException e2) {
			System.err.println("wrong fileformat! not an integer!");
			throw new DataSourceException(e2.getLocalizedMessage());
		}
		if (token <= 0) {
			throw new DataSourceException(
					"token (size of reference corpus) cannot be " + token + "!");
		}
		System.out.println("token (from " + listfile + "): " + token);

		// -------------PB------------------
		int fileSize = (int) new File(listfile).length();
		//NewLanguageTab.setMaxProgress(fileSize - ("" + token).length());
		int count = 0;
		// -------------PB------------------

		// read out
		String line, word;
		String[] splitted_line;
		Integer wordcount;

		try {
			while ((line = wordlist.readLine()) != null) {

				// dont bug me with exceptions on empty lines
				if (line.length() == 0) {
					System.err.println("ignoring empty line...");
					continue;
				}

				wordcount = this.getIntFromLine(line);
				word = this.getWordFromLine(line);

				if (wordmap.containsKey(word)) {
					System.out
							.println("duplicate entry in wordlist: '"
									+ word
									+ "'!\n   keeping the first entry (and ignoring this one)!");
				} else {
					// insert our nice values
					wordmap.put(word, new Double(Double.parseDouble(wordcount
							.toString())
							/ Double.parseDouble((new Integer(token))
									.toString())));

				}
				count += line.length() + 1;
				//NewLanguageTab.progress(count);
			}
		} catch (IOException e) {
			System.err.println("error while reading from file " + listfile);
			throw new DataSourceException(e.getLocalizedMessage());
		}

		return wordmap;
	}

	/**
	 * get the word out of a line
	 * 
	 * @param line
	 * @return
	 * @throws DataSourceException
	 */
	private String getWordFromLine(String line) throws DataSourceException {
		String ret;
		String[] splitline = line.split(this.wordlistseparator);
		if (splitline.length <= 1) {
			System.err
					.println("line '"
							+ line
							+ "' doesn't match required format '<int><sep><word>' (sep='"
							+ this.wordlistseparator + "')!");

		} else {
			ret = line.substring(line.indexOf(this.wordlistseparator) + 1);
			return ret;
		}
		throw new DataSourceException("wrong format in wordlistfile in line '"
				+ line + "'!");

	}

	/**
	 * parse a given line, returns the count of a word (first member of the
	 * line)
	 * 
	 * @param line
	 * @return
	 * @throws DataSourceException
	 */
	private Integer getIntFromLine(String line) throws DataSourceException {
		String[] splitline = line.split(this.wordlistseparator);
		Integer ret = null;

		if (splitline.length <= 1) {
			System.err
					.println("line '"
							+ line
							+ "' doesn't match required format '<int><sep><word>' (sep='"
							+ this.wordlistseparator + "')!");

		} else {
			try {
				ret = new Integer(Integer.parseInt(splitline[0]));
				return ret;
			} catch (NumberFormatException e) {
				System.err.println("error in line '" + line + "', "
						+ splitline[0] + " is not an integer!");
			}
		}
		throw new DataSourceException("wrong format in wordlistfile in line '"
				+ line + "'!");

	}

	/**
	 * returns the languages/datasources managed by this object
	 * 
	 * @return
	 */
	public Set getAvailableLanguages() {
		return this.datasources.keySet();
	}

}
