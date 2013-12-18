package org.lcc.utils;

import java.util.Collection;

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
}