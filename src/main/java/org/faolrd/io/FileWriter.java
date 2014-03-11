package org.faolrd.io;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import org.faolrd.utils.Helpers;

/**
 *
 * @author jnphilipp
 * @version 0.0.1
 */
public class FileWriter {
	public static void write(String file, boolean append, String content) throws FileNotFoundException, IOException {
		Writer writer = null;

		try {
			writer = new BufferedWriter(new java.io.FileWriter(file));
			if ( append )
				writer.append(content);
			else
				writer.write(content);
		}
		finally {
			if ( writer != null )
				writer.close();
		}
	}

	public static void writeCSV(String file, Collection<String[]> lines) throws FileNotFoundException, IOException {
		FileWriter.writeCSV(file, lines, ";");
	}

	public static void writeCSV(String file, Collection<String[]> lines, String cement) throws FileNotFoundException, IOException {
		String content = "";
		for ( String[] line : lines )
			content += System.lineSeparator() + Helpers.join(line, cement);
		FileWriter.write(file, false, content);
	}
}