/*
 * Created on 18.05.2005
 *
 */
package de.uni_leipzig.asv.toolbox.jLanI.tools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael Welt
 * 
 */
public class Encodings {

	public static final String BASIC_ENCODING_SET = "java_encodings_basic";

	public static final String EXTENDED_ENCODING_SET = "java_encodings_extended";

	private Map encodings = null;

	public Encodings() {
		this.encodings = new HashMap();
	}

	public void read(String fileName) throws Exception {
		read(new FileInputStream(fileName));
	}

	public void read(InputStream stream) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				stream, "ASCII"));
		String line = null;
		while ((line = reader.readLine()) != null) {
			String[] parts = line.split("\t");
			encodings.put(parts[0], new Encoding(parts[0], parts[1]));
		}
		reader.close();
	}

	public void loadEncodingSet(String setName) throws Exception {
		read(this.getClass().getClassLoader().getResourceAsStream(
				"tt/de/mai01dzx/data/" + setName + ".txt"));
	}

	public Map getEncodings() {
		return encodings;
	}

	public void addEncoding(Encoding enc) {
		this.encodings.put(enc.getName(), enc);
	}
}
