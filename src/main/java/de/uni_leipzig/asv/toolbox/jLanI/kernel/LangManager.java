package de.uni_leipzig.asv.toolbox.jLanI.kernel;

import java.io.*;
import java.util.*;

import de.uni_leipzig.asv.toolbox.jLanI.tools.Log;
import de.uni_leipzig.asv.toolbox.jLanI.tools.Preferences;

public class LangManager {
	static HashMap inactiv = new HashMap();

	static LanIKernel kern = null;

	public static boolean loaded = false;

	public static int addNewLanguage(String lang, String file)
			throws DataSourceException {
		// LanIKernel kern = LanIKernel.getInstance();
		String wordlistDir = kern.prefs.getProperty("WordlistDir");

		kern.datasourcemanager.addDatasource(lang, file, null);
		kern.dumpDatasource(lang, wordlistDir
				+ System.getProperty("file.separator") + lang + ".ser.gz");
		if (inactiv.containsKey(lang))
			activ(lang, false);

		return getSourceSize(lang);
	}

	private static int getSourceSize(String lang) {
		return (kern.datasourcemanager.hasLanguage(lang)) ? ((DataSource) kern.datasourcemanager.datasources
				.get(lang)).size()
				: ((DataSource) inactiv.get(lang)).size();
	}

	public static int addNewLanguageFromDb(String lang, HashMap wordlist)
			throws DataSourceException {
		String wordlistDir = kern.prefs.getProperty("WordlistDir");

		kern.datasourcemanager.datasources.put(lang, new DataSource(lang,
				wordlist));
		kern.dumpDatasource(lang, wordlistDir
				+ System.getProperty("file.separator") + lang + ".ser.gz");
		if (inactiv.containsKey(lang))
			activ(lang, false);

		return getSourceSize(lang);
	}

	public static HashMap getAllLanguages() {
		loaded = true;
		HashMap allLanguages = new HashMap();
		try {
			kern = LanIKernel.getInstance();
			Iterator it = kern.datasourcemanager.datasources.values()
					.iterator();
			while (it.hasNext()) {
				DataSource ds = (DataSource) it.next();
				allLanguages.put(ds.getName(), new Integer(ds.size()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return allLanguages;
	}

	public static void delete(String lang) {
		try {
			// LanIKernel kern = LanIKernel.getInstance();
			String wordlistDir = kern.prefs.getProperty("WordlistDir");
			File delFile1 = new File(wordlistDir
					+ System.getProperty("file.separator") + lang + ".ser.gz");
			File delFile2 = new File(wordlistDir
					+ System.getProperty("file.separator") + lang + ".txt");
			delFile1.delete();
			delFile2.delete();
			kern.datasourcemanager.datasources.remove(lang);
			inactiv.remove(lang);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void activ(String lang, boolean act) {
		// LanIKernel kern = LanIKernel.getInstance();
		if (act)
			kern.datasourcemanager.datasources.put(lang, inactiv.remove(lang));
		else
			inactiv.put(lang, kern.datasourcemanager.datasources.remove(lang));
	}

	public static boolean containsLanguage(String lang) {
		boolean retval = false;
		if (!(kern.datasourcemanager == null)) {
			retval = kern.datasourcemanager.datasources.containsKey(lang)
					|| inactiv.containsKey(lang);
		}

		return retval;
	}
}
