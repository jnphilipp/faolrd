/*
 * Created on 19.04.2005
 *
 */
package de.uni_leipzig.asv.toolbox.jLanI.tools;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * @author Michael Welt
 * 
 */
public class ToStringTools {

	public static String hashMapToString(Map map) {
		StringBuffer buffer = new StringBuffer();
		int i = 0;
		buffer.append("[");
		for (Iterator it = map.keySet().iterator(); it.hasNext();) {
			String currKey = it.next().toString();
			String currVal = map.get(currKey).toString();
			buffer.append("{" + currKey + "," + currVal + "}"
					+ (i++ < map.keySet().size() - 1 ? "\n" : ""));
		}
		buffer.append("]");
		return buffer.toString();
	}

	public static String propertiesToString(Properties map) {
		StringBuffer buffer = new StringBuffer();
		int i = 0;
		buffer.append("[");
		for (Iterator it = map.keySet().iterator(); it.hasNext();) {
			String currKey = it.next().toString();
			String currVal = System.getProperty(currKey);
			buffer.append("{" + currKey + "," + currVal + "}"
					+ (i++ < map.keySet().size() - 1 ? "\n" : ""));
		}
		buffer.append("]");
		return buffer.toString();
	}

	public static String arrayToString(Object[] array) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		for (int i = 0; i < array.length; i++) {
			buffer.append(array[i].toString()
					+ (i < array.length - 1 ? "\n" : ""));
		}
		buffer.append("]");
		return buffer.toString();
	}

	public static String doubleMatrixToString(double[][] matrix) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++)
				buffer.append(matrix[i][j]
						+ (j < matrix[i].length - 1 ? "," : ""));
			buffer.append((i < matrix.length - 1 ? "\n" : ""));
		}
		buffer.append("]");
		return buffer.toString();
	}
}
