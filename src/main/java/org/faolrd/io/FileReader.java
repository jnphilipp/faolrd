package org.faolrd.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;

/**
 *
 * @author jnphilipp
 * @version 0.0.2
 */
public class FileReader {
	public static String read(String file) throws FileNotFoundException, IOException {
		Reader reader = null;
		String content = "";

		try {
			reader = new BufferedReader(new java.io.FileReader(file));

			while ( true ) {
				int c = reader.read();
				if ( c == -1 )
					break;

				content += (char)c;
			}
		}
		finally {
			if ( reader != null )
				reader.close();
		}

		return content;
	}

	public static String[] readLines(String file) throws FileNotFoundException, IOException {
		String content = read(file);

		if ( content.isEmpty() )
			return new String[0];

		return content.split(System.lineSeparator());
	}

	/**
	 * Reads the given file as CSV.
	 * @param file file
	 * @param csv CSV output
	 * @param cement cement
	 * @throws IOException 
	 */
	public static void readCSV(String file, Collection<String[]> csv, String cement) throws IOException {
		String[] lines = readLines(file);
		for ( String line : lines )
			csv.add(line.split(cement));
	}
}