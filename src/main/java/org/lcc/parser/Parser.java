package org.lcc.parser;

/**
 *
 * @author jnphilipp
 * @version 0.0.1
 */
public interface Parser {
	/**
	 * Builds a connection to the given URL and retrieves it. If a user-agent is given it will be used.
	 * @param url URL which will be fetched
	 * @throws Exception 
	 */
	public abstract void fetch(String url) throws Exception;
}