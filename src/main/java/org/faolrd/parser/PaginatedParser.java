package org.faolrd.parser;

/**
 *
 * @author jnphilipp
 * @version 0.0.1
 */
public interface PaginatedParser {
	/**
	 * The first page.
	 */
	public abstract void firstPage();

	/**
	 * Checks if there is a next page.
	 * @return <code>True</code> if a next page exists else <code>false</code>.
	 */
	public abstract boolean hasNextPage();

	/**
	 * Iterates to the next page.
	 */
	public abstract void nextPage();

	/**
	 * Resets the results.
	 */
	public abstract void resetResults();
}