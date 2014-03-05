/*
 * Created on 28.03.2005
 *
 */
package de.uni_leipzig.asv.toolbox.jLanI.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

/**
 * Just a small ini thing, it doesn't store any defaults. It's also implemented
 * as Singleton for use anywhere. It's write Methods are synchronized for in
 * Thread using!
 * 
 * @author Michael Welt
 * 
 */
public class GlobalPreferences {
	public final static String DEFAULT_PROPERTY_FILE = "properties.ini";

	public final static String DEFAULT_PROPERTY_HEADER = "default";

	private String propertyHeader = DEFAULT_PROPERTY_HEADER;

	private String propertyFile = DEFAULT_PROPERTY_FILE;

	private Properties properties = null;

	private static GlobalPreferences singleton = null;

	private GlobalPreferences() {
		readFrom(propertyFile);
		singleton = this;
	}

	private void readFrom(String fileName) {
		properties = new Properties();
		File f = new File(fileName);

		if (f.exists() && f.isFile() && f.canRead()) {
			try {
				FileInputStream is = new FileInputStream(f);
				properties.load(is);
				is.close();
				this.propertyFile = fileName;
				Log.getInstance().log(
						"Properties loaded successfully from File" + fileName
								+ "!\n");
			} catch (FileNotFoundException e) {
				Log.getInstance().err(
						"Preferences File not found! \n" + e.getMessage()
								+ "\n");
			} catch (IOException e) {
				Log.getInstance().err(
						"IOException during Preferences File init! \n"
								+ e.getMessage() + "\n");
			}
		} else {
			Log.getInstance().log(
					"No Preferencesfile found, using empty thang!");
		}
	}

	public static GlobalPreferences getInstance() {
		if (singleton == null)
			return new GlobalPreferences();
		else
			return singleton;
	}

	public void read(String fileName) {
		readFrom(fileName);
	}

	public void write(String fileName) {
		this.propertyFile = fileName == null ? propertyFile : fileName;
		write();
	}

	public String getProperty(String name) {
		return this.properties.getProperty(name);
	}

	public synchronized void setProperty(String name, String value) {
		this.properties.setProperty(name, value);
	}

	public void setPropertyFile(String fileName) {
		this.propertyFile = fileName == null ? propertyFile : fileName;
	}

	public void setPropertyHeader(String header) {
		this.propertyHeader = header == null ? this.propertyHeader : header;
	}

	public void write() {
		try {
			File f = new File(propertyFile);
			FileOutputStream os = new FileOutputStream(f);
			properties.store(os, propertyHeader);
			os.close();
			Log.getInstance().log("Preferences saved successfully!");
		} catch (FileNotFoundException e) {
			Log.getInstance().err(
					"Error while saving Preferences File Not Found!\n"
							+ e.getMessage());
		} catch (IOException e) {
			Log.getInstance().err(
					"Error while saving Preferences IOError!\n"
							+ e.getMessage());
		}
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (Iterator it = this.properties.keySet().iterator(); it.hasNext();) {
			String currKey = it.next().toString();
			String currVal = System.getProperty(currKey);
			buffer.append(currKey + "\t" + currVal + "\n");
		}
		return buffer.toString();
	}
}
