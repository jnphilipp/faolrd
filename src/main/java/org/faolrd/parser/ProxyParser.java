package org.faolrd.parser;

import java.net.Proxy;
import java.util.Set;

/**
 *
 * @author jnphilipp
 * @version 0.0.2
 */
public interface ProxyParser {
	/**
	 * @return the proxies
	 * @throws Exception
	 */
	public abstract Set<Proxy> getProxies() throws Exception;
}