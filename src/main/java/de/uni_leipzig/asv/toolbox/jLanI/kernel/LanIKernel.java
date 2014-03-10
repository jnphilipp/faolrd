/**
 * $Header: /usr/cvs/toolbox/src/de/uni_leipzig/asv/toolbox/jLanI/kernel/LanIKernel.java,v 1.8 2007/08/10 15:49:49 lsteiner Exp $
 * 
 * Created on 15.04.2005, 09:36:13 by knorke
 * for project jLanI
 */
package de.uni_leipzig.asv.toolbox.jLanI.kernel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import de.uni_leipzig.asv.toolbox.jLanI.tools.Log;
import de.uni_leipzig.asv.toolbox.jLanI.tools.Preferences;

/**
 * @author knorke - Sven Teresniak
 */
public class LanIKernel implements kernel {

	public static final String version = "$Revision: 1.8 $";

	public static String propertyFile = "./config/jlani/lanikernel";

	/**
	 * the kernel-instance itself. the kernel is a singleton
	 */
	private static LanIKernel kernelinstance = null;

	/**
	 * holds all datasources (wordlists) and manages them
	 */
	public DatasourceManager datasourcemanager;

	/**
	 * log-instance
	 */
	public Log log;

	/**
	 * configfile-instance
	 */
	public Preferences prefs;

	/**
	 * the blacklist with words to ignore in evaluate()
	 */
	private HashSet blacklist;

	private Set _allLangs;

	private String specialChars;

	// ===================={method part}======================================

	/**
	 * constructor
	 * 
	 * @throws DataSourceException
	 */
	private LanIKernel() throws DataSourceException {

		this.prefs = new Preferences(LanIKernel.propertyFile);

		this.log = Log.getInstance();

		// getting the logmode from prefs
		if (this.prefs.getProperty("KernelLogMode") != null) {
			try {
				this.log.setLogMode(Integer.parseInt(this.prefs
						.getProperty("KernelLogMode")));
			} catch (Exception e) {
				this.log.err("LanIKernel: wrong setting in '"
						+ LanIKernel.propertyFile + "' KernelLogMode");
				this.log.log("LanIKernel: setting KernelLogMode to default: "
						+ Log.DEFAULT_LOGMODE);
			}
		} else {
			this.log.err("LanIKernel: missing setting 'KernelLogMode' in '"
					+ LanIKernel.propertyFile + "'");
			this.log.log("LanIKernel: setting KernelLogMode to default: "
					+ Log.DEFAULT_LOGMODE);
		}

		this.log.debug("LanIKernel: logging facility enabled");
		this.log.log("LanIKernel: instantiate the LanIKernel object");

		this.datasourcemanager = DatasourceManager.getInstance();
		try {
			this.setupLanguages();
		} catch (DataSourceException e) {
		}
		;

		// read where the blacklist file is located
		if (this.prefs.getProperty("BlacklistFile") == null
				|| this.prefs.getProperty("BlacklistFile").equals("")) {
			this.log.log("LanIKernel: no blacklist-file given");
			this.blacklist = new HashSet();
		} else {
			this.setupBlacklist(this.prefs.getProperty("BlacklistFile"));
		}

		// search for special chars
		if (this.prefs.getProperty("SpecialChars") == null
				|| this.prefs.getProperty("SpecialChars").equals("")) {
			this.log.log("LanIKernel: no special chars to remove found");
			this.specialChars = null;
		} else {
			this.setupSpecialChars(this.prefs.getProperty("SpecialChars"));
		}

		this._allLangs = this.getAvailableDatasources();

	}

	/**
	 * sets the special chars to remove from sentences in preprocessing phase
	 * 
	 * @param chars
	 */
	public void setupSpecialChars(String chars) {
		if (chars == null) {
			this.specialChars = "";
		} else {
			this.specialChars = chars;
		}
	}

	/**
	 * static method to get back the singleton instance
	 * 
	 * @throws DataSourceException
	 */
	public synchronized static LanIKernel getInstance()
			throws DataSourceException {
		if (LanIKernel.kernelinstance == null)
			LanIKernel.kernelinstance = new LanIKernel();
		return kernelinstance;
	}

	/**
	 * load and add a previous dumped datasource
	 * 
	 * @param filename
	 * @return
	 */
	public boolean loadDumpedDatasource(String filename) {
		this.log.debug("LanIKernel: try to load serialized datasource '"
				+ filename + "'");
		return this.datasourcemanager.addSerializedDatasource(filename);
	}

	/**
	 * writes the datasource lang to disc
	 * 
	 * @param lang
	 * @return
	 */
	public boolean dumpDatasource(String lang) {
		this.log.debug("LanIKernel: try to write datasource '" + lang
				+ "' into '" + lang + ".ser.gz'");
		return this.dumpDatasource(lang, lang + ".ser.gz");
	}

	/**
	 * removing all reverences and running the garbage collector you must call
	 * getInstance() afterwards to use lani
	 * 
	 */
	public void reset() {

		if (LanIKernel.kernelinstance == null) {
			this.log.debug("LanIKernel: not instanciated, nothing to do.");
			return;
		}

		this.log.log("LanIKernel: destroying lani kernel instance");

		LanIKernel.kernelinstance = null;
		this.prefs = null;
		this.log.debug("LanIKernel: removing reference to datasource manager");
		this.datasourcemanager.reset();
		this.datasourcemanager = null;
		this.log.debug("LanIKernel: disconnecting from log instance");
		this.log = null;
		// a good time for cleaning
		System.gc();
	}

	/**
	 * wrapper to easily dump all datasources (wordlists) to files
	 * 
	 * @return true if everything is okay
	 */
	public boolean dumpAllDatasources() {

		this.log.debug("LanIKernel: dumping all datasources...");

		if (this.getAvailableDatasources().isEmpty()) {
			this.log
					.err("LanIKernel: cannot serialize wordlists; no wordlists loaded.");
			return false;
		}

		boolean ret = true;
		for (Iterator iter = this.getAvailableDatasources().iterator(); iter
				.hasNext()
				& ret;) {
			String lang = (String) iter.next();
			ret &= this.dumpDatasource(lang, lang + ".ser.gz");
			this.log.debug("LanIKernel: wrote wordlist '" + lang + "' to '"
					+ lang + ".ser.gz': " + ret);
		}

		return ret;
	}

	/**
	 * dump datasource 'lang' to disc in file 'filename'
	 * 
	 * @param lang =
	 *            language to dump
	 * @param filename =
	 *            file to dump the datasource into
	 * @return
	 */
	public boolean dumpDatasource(String lang, String filename) {
		if (this.datasourcemanager.hasLanguage(lang)) {
			return this.datasourcemanager.serializeDatasource(lang, filename);
		}
		this.log.err("LanIKernel: cannot find language '" + lang
				+ "' for serialization");
		return false;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @throws RequestException
	 * @throws DataSourceException
	 * 
	 * @see jLanI.kernel#evaluate(jLanI.Request)
	 */
	public Response evaluate(Request request) throws RequestException,
			DataSourceException {

		this.log.debug("LanIKernel: evaluate() called with request '" + request
				+ "'");

		if (request == null) {
			this.log
					.err("LanIKernel: you cannot evaluate an empty (null) request object");
			throw new RequestException(
					"evaluate() cannot evaluate empty (null) request objects");
		}

		// new languages to load?
		if (request.getLanguages().size() <= 0) {
			this.log
					.debug("LanIKernel: languagelist in request is empty, checking sentence against all languages available");
			this.log.debug(new StringBuffer(
					"LanIKernel: and available langs are: ").append(
					this._allLangs).toString());
			request.setLanguages(this.getAvailableDatasources());
		}

		HashMap ret, seenWords = null;
		HashMap temp = new HashMap<String, Double>();

		// the sentence to check
		String sentence = request.getSentence();

		// the response-object to return
		Response response = new Response();

		// set the correct id
		response.setId(request.getId());

		// building result-map, initializing all languages with 1.0
		// to avoid zero-multiplications
		if (request.isReduced()) {
			for (Iterator iter = request.getLanguages().iterator(); iter
					.hasNext();) {
				temp.put((String) iter.next(), 1.0);
			}
		} else {
			seenWords = new HashMap();
			String langtemp;
			for (Iterator iter = request.getLanguages().iterator(); iter
					.hasNext();) {
				langtemp = (String) iter.next();
				temp.put(langtemp, 1.0);
				seenWords.put(langtemp, new LinkedList());
			}
		}

		String lang;
		this.log.debug(new StringBuffer("LanIKernel: checking sentence '")
				.append(sentence).append("'").toString());

		if (sentence == null) {
			this.log.err("LanIKernel: the sentence shouldn't be null");
			return response;
		}

		sentence = this.cleanSentence(sentence);

		String[] splittedsentence = sentence.split(" ");
		Double tempdouble;
		int sentenceLength = 0;
		boolean reduced = request.isReduced();

		// check all words or just a few?
		if (request.getWordsToCheck() > 0) {
			// okay, let's reduce the data...
			int count = request.getWordsToCheck();

			// are there more words than given wordsToCheckCount?
			if (splittedsentence.length > count) {
				String[] newsplittedsentence = new String[count];
				int stepping = splittedsentence.length / count;
				int j = 0;
				for (int i = 0; i < count; i += stepping)
					newsplittedsentence[j++] = splittedsentence[i];
				splittedsentence = newsplittedsentence;
			}
		}

		for (int i = 0; i < splittedsentence.length; i++) {

			// ignoring blacklisted words
			// if ( this.isBlacklisted( sentence[i] ) ) continue;
			if (this.blacklist.contains(splittedsentence[i]))
				continue;
			// ignore ALL blacklisted words and don't count it
			else
				sentenceLength++;

			// evaluate it otherwise
			HashMap tempmap[] = this.datasourcemanager.getEvaluationData(
					request.getLanguages(), splittedsentence[i], reduced);
			ret = tempmap[0];

			// save the words seen in the sentence and known by the
			// datasourcemanager, but only if reduced is false!
			if (!reduced) {
				for (Iterator iter = tempmap[1].keySet().iterator(); iter
						.hasNext();) {
					((LinkedList) seenWords.get((String) iter.next()))
							.add(splittedsentence[i]);
				}
			}

			for (Iterator iter = request.getLanguages().iterator(); iter
					.hasNext();) {
				lang = (String) iter.next();
				tempdouble = (Double) ret.get(lang);
				// if (tempdouble == null) continue;
				// if(tempdouble.doubleValue() == Double.NaN ) continue;

				double tmpValue = tempdouble.doubleValue()
						* ((Double) temp.get(lang)).doubleValue();
				temp.put(lang, new Double(tmpValue));
			}
		}

		response.setSentenceLength(sentenceLength);
		response.setResult(temp);
		response.setSeenWords(seenWords);
		this.log.debug(new StringBuffer(
				"LanIKernel: returning response object: '").append(response)
				.append("'").toString());

		// calculate the covering of words
		if (!reduced) {
			HashMap coverage = new HashMap();
			HashMap wordcount = new HashMap();
			double sentencelength = (double) splittedsentence.length;
			for (Iterator iter = response.getSeenWords().keySet().iterator(); iter
					.hasNext();) {
				lang = (String) iter.next();
				double coverageVal = ((double) (((LinkedList) response
						.getSeenWords().get(lang)).size()))
						/ sentencelength;
				int countVal = ((LinkedList) response.getSeenWords().get(lang))
						.size();
				coverage.put(lang,
						(coverageVal == Double.NaN) ? new Double(0.0)
								: new Double(coverageVal));
				wordcount.put(lang, new Integer(countVal));
			}
			response.setCoverage(coverage);
			response.setWordCount(wordcount);
		}

		return response;
	}

	/**
	 * remove special chars and do pre-processing, before evaluation
	 * 
	 * @param sentence
	 *            the data to check
	 * @return the cleaned data
	 */
	public String cleanSentence(String sentence) {

		/**
		 * PRE PROCESSING
		 */
		if (this.specialChars == null || this.specialChars.equals(""))
			return sentence;

		sentence = sentence.replaceAll(this.specialChars, "");

		return sentence;
	}

	/**
	 * 
	 * @see jLanI.kernel#getAvailableDatasources()
	 */
	public Set getAvailableDatasources() {
		return this.datasourcemanager.getAvailableLanguages();
	}

	public boolean isBlacklisted(String word) {
		return this.blacklist.contains(word);
	}

	private boolean setupLanguages() throws DataSourceException {

		this.log.debug("LanIKernel: setting up languages");

		// first checking the directory which contains the wordlists
		File wordlistdir;
		if (this.prefs.getProperty("WordlistDir") == null) {
			this.log
					.err("LanIKernel: wrong or missing parameter 'WordlistDir'");
			throw new DataSourceException(
					"cannot find 'WordlistDir' in preferences-file");
		}

		// okay, the parameter exists, so lets check the directory itself
		wordlistdir = new File(this.prefs.getProperty("WordlistDir"));
		if (!wordlistdir.canRead()) {
			this.log.err("LanIKernel: cannot read wordlistdir '"
					+ wordlistdir.getName() + "'");
			throw new DataSourceException("cannot read WordlistDir '"
					+ wordlistdir.getName() + "'");
		}
		if (!wordlistdir.isDirectory()) {
			this.log.err("LanIKernel: the wordlistdir '"
					+ wordlistdir.getName() + "' is not a directory!");
			throw new DataSourceException("the wordlistdir '"
					+ wordlistdir.getName() + "' is not a directory!");
		}

		// now search for files/wordlists
		this.log.debug("LanIKernel: searching for files now...");
		File wordlist[] = wordlistdir.listFiles();
		if (wordlist.length <= 0) {
			this.log.err("LanIKernel: cannot find any file in '"
					+ wordlistdir.getName() + "'");
			throw new DataSourceException("cannot find wordlists in '"
					+ wordlistdir.getName() + "'");
		}

		// first search for serialized datasources
		for (int i = 0; i < wordlist.length; i++) {
			if (wordlist[i].getName().endsWith(".ser.gz")
					& wordlist[i].getName().length() > ".ser.gz".length()) {
				this.log.debug("LanIKernel: found wordlist file '"
						+ wordlist[i] + "'");
				this.loadDumpedDatasource(wordlist[i].getAbsolutePath());
			}
		}

		// and now search for .txt and .tok files
		String tempstring;
		for (int i = 0; i < wordlist.length; i++) {

			// found .txt extension
			if (wordlist[i].getName().endsWith(".txt")
					& wordlist[i].getName().length() > ".txt".length()) {
				this.log.debug("LanIKernel: found wordlist file '"
						+ wordlist[i] + "'");

				// search the .tok-file for this .txt file
				tempstring = wordlist[i].getName().substring(0,
						wordlist[i].getName().length() - ".txt".length());

				// File tokenfile = new File( wordlistdir
				// + System.getProperty( "file.separator" ) + tempstring
				// + ".tok" );
				// if ( tokenfile.canRead() ) {
				this.datasourcemanager.addDatasource(tempstring, wordlistdir
						+ System.getProperty("file.separator") + tempstring
						+ ".txt", wordlistdir
						+ System.getProperty("file.separator") + tempstring
						+ ".tok");
				// } else {
				// this.log
				// .err( "LanIKernel: cannot find the tokenfile for wordlist '"
				// + tempstring
				// + ".txt'. remove the wordlist or add a token file." );
				// throw new DataSourceException( "cannot find '"
				// + wordlistdir
				// + System.getProperty( "file.separator" )
				// + tempstring + ".tok'" );
				//
				// }
			}
		}

		System.out.println(this.datasourcemanager);

		return true;
	}

	private void setupBlacklist(String filename) throws DataSourceException {
		this.blacklist = new HashSet();
		this.log.debug("LanIKernel: try to open the blacklist-file '"
				+ filename + "'");

		LineNumberReader lnr = null;
		try {
			lnr = new LineNumberReader(new BufferedReader(new FileReader(
					filename)));
		} catch (FileNotFoundException e) {
			this.log.err("LanIKernel: blacklist-file '" + filename
					+ "' not found!");
			throw new DataSourceException("blacklist-file '" + filename
					+ "' not found");

		}
		String line;
		try {
			while ((line = lnr.readLine()) != null) {
				this.blacklist.add(line);
			}
		} catch (IOException e1) {
			this.log.err("LanIKernel: cannot read from blacklist-file: "
					+ e1.getMessage());
			throw new DataSourceException(
					"error while reading from blacklist-file");
		}

		try {
			lnr.close();
		} catch (Throwable e) {
			// nothing. ignore that.
		}
		this.log.log("LanIKernel: " + this.blacklist.size()
				+ " entries from blacklist-file '" + filename + "' read");
	}

	public String toString() {
		StringBuffer ret = new StringBuffer();
		ret.append("[LanIKernel Object, ").append(
				this.datasourcemanager.toString().replaceAll("\n", "")).append(
				"]");
		return ret.toString();
	}

	/**
	 * method to check periodically for future versions only, not implemented
	 * yet.
	 */
	public boolean upkeep() {

		System.gc();
		System.err
				.println("upkeep() not implemented yet... does nothing but a slow System.gc() call...");
		return true;
	}

	/**
	 * test method
	 * 
	 * @throws RequestException
	 * @throws DataSourceException
	 */
	public void test() throws RequestException, DataSourceException {

		LanIKernel kernel = LanIKernel.getInstance();
		kernel.log.setLogMode(1);
		kernel.log.setDebug(true);

		Request req = new Request();
		Set temp = new HashSet();
		/*
		 * temp.add( "de" ); temp.add( "en" ); temp.add( "fr" ); temp.add( "ee" );
		 * temp.add( "it" ); temp.add( "se" ); temp.add( "fi" ); temp.add( "es" );
		 */
		req.setLanguages(temp);
		req.setReduce(false);
		req.setSentence("my pony is over the ocean, my bonny is over the see");
		System.out.println("\"" + req.getSentence() + "\"");
		System.out.println(kernel.evaluate(req));
		kernel.upkeep();

		// req.setWordsToCheck(5);
		req
				.setSentence(" und last but not least, bin ich ein _kurzer_ deutscher Satz (hubergel)!");
		System.out.println("\"" + req.getSentence() + "\"");
		Response resp = kernel.evaluate(req);
		System.out.println("response: " + resp);
		System.out.println("   coverage: " + resp.getCoverage());

		req.setWordsToCheck(0);

		int n = 30000;
		System.out.println("testing " + n + " times evaluate()...");
		kernel.log.setDebug(false);

		// req.setWordsToCheck(5);

		// warming up the hotspot engine
		for (int i = 0; i < 10000; i++)
			kernel.evaluate(req);

		long delta = (new Date()).getTime();

		for (int i = 0; i < n; i++)
			kernel.evaluate(req);
		kernel.log.setDebug(true);
		long d = 0;
		System.out.println("... done in "
				+ (d = ((new Date()).getTime() - delta)) + "ms, " + (float) n
				/ ((float) d / 1000.0) + " 1/s");

		System.out.println("serialize de");
		kernel.dumpDatasource("de");
		System.out.println("done");
		System.out.println("Deserialize de");
		kernel.loadDumpedDatasource("de.ser.gz");
		System.out.println("done");
		kernel.upkeep();
		System.out.println("dump all datasources to files...");
		kernel.dumpAllDatasources();
		System.out.println("done");
		System.out.println("calling reset()");
		kernel.reset();
		// kernel.evaluate(req);
		// kernel = null;
		System.out.println("get a new instance");
		kernel = LanIKernel.getInstance();
		System.out.println("done");
		System.out.println(kernel);
		System.out.println("version: " + kernel.version);
	}

	public static void main(String[] args) throws RequestException,
			DataSourceException {
		LanIKernel.getInstance().test();
	}
}
