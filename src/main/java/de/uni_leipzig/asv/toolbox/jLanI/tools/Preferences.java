/*
 * Created on 19.05.2005
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
 * @author Michael Welt
 * 
 */
public class Preferences {

	private Properties properties = null;

	private String name = null;

	public Preferences(String name) throws IllegalArgumentException {
		if (name == null)
			throw new IllegalArgumentException("name can not be null!");

		this.name = name;

		File f = new File(this.name + ".ini");
		properties = new Properties();

		if (f.exists() && f.canRead())
			readFrom(f.getAbsolutePath());
	}

	private void readFrom(String fileName) {
		try {
			FileInputStream is = new FileInputStream(fileName);
			properties.load(is);
			is.close();

			Log.getInstance().log(
					"Properties (" + this.name
							+ ")loaded successfully from File" + fileName
							+ "!\n");
		} catch (FileNotFoundException e) {
			Log.getInstance().err(
					"Preferences File not found! \n" + e.getMessage() + "\n");
		} catch (IOException e) {
			Log.getInstance().err(
					"IOException during Preferences File init! \n"
							+ e.getMessage() + "\n");
		}
	}

	public String getProperty(String name) {
		return this.properties.getProperty(name);
	}

	public synchronized void setProperty(String name, String value) {
		this.properties.setProperty(name, value);
	}

	public void write() {
		try {
			File f = new File(this.name + ".ini");
			FileOutputStream os = new FileOutputStream(f);
			properties.store(os, name);
			os.close();
			Log.getInstance().log(
					"Preferences (" + name + ") saved successfully!");
		} catch (FileNotFoundException e) {
			Log.getInstance().err(
					"Error while saving Preferences File (" + name
							+ ") Not Found!\n" + e.getMessage());
		} catch (IOException e) {
			Log.getInstance().err(
					"Error while saving Preferences (" + name + ") IOError!\n"
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
