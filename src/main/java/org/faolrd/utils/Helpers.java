package org.faolrd.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 
 * @author jnphilipp
 * @version 0.0.1
 */
public class Helpers {
	/**
	 * Returns path to the user directory.
	 * @return path to user directory
	 */
	public static String getUserDir() {
		return System.getProperty("user.dir");
	}

	/**
	 * Returns path to the subdirectory of the user directory.
	 * @param dir subdirectory
	 * @return path to subdirectory
	 */
	public static String getSubUserDir(String dir) {
		return System.getProperty("user.dir") + (dir.startsWith("/") ? dir : "/" + dir);
	}

	/**
	 * Joins the given array to a <code>String</code>, separated with the given cement.
	 * @param <T>
	 * @param array array
	 * @param cement cement
	 * @return joined string
	 */
	public static <T> String join(T[] array, String cement) {
		StringBuilder builder = new StringBuilder();

		if ( array == null || array.length == 0 )
			return null;
		

		for ( T t : array )
			builder.append(t).append(cement);

		builder.delete(builder.length() - cement.length(), builder.length());
		return builder.toString();
	}

	/**
	 * Joins the given collection to a <code>String</code>, separated with the given cement.
	 * @param <T>
	 * @param collection collection
	 * @param cement cement
	 * @return joined string
	 */
	public static <T> String join(Collection<T> collection, String cement) {
		StringBuilder builder = new StringBuilder();

		if ( collection == null || collection.isEmpty() )
			return null;

		for ( T t : collection )
			builder.append(t).append(cement);

		builder.delete(builder.length() - cement.length(), builder.length());
		return builder.toString();
	}

	/**
	 * Sorts the given list.
	 * @param toSort list to sort
	 * @param column column which will be used
	 */
	public static void sort(List<String[]> toSort, final int column) {
		Collections.sort(toSort, new Comparator<String[]>() {
			@Override
			public int compare(String[] f, String[] g) {
				return f[column].compareTo(g[column]);
			}
		});
	}

	public static int[] getMinMax(List<String[]> list, int column) {
		int[] minMax = new int[2];

		for ( int i = 1; i < list.size(); i++ ) {
			if ( Integer.parseInt(list.get(i)[column]) < Integer.parseInt(list.get(minMax[0])[column]) )
				minMax[0] = i;
			if ( Integer.parseInt(list.get(i)[column]) > Integer.parseInt(list.get(minMax[1])[column]) )
				minMax[1] = i;
		}
		return minMax;
	}
}