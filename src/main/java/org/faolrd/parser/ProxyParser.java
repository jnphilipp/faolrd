package org.faolrd.parser;

import java.net.Proxy;
import java.util.Set;

/**
 *
 * @author jnphilipp
 * @version 0.0.3
 */
public interface ProxyParser extends Parser {
	/**
	 * @return the proxies
	 * @throws Exception
	 */
	public abstract Set<Proxy> getProxies() throws Exception;
}