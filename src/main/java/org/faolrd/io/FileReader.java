package org.faolrd.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;

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
}