package org.faolrd.parser;

import java.util.Set;
import org.faolrd.net.Proxy;

/**
 *
 * @author jnphilipp
 * @version 0.0.1
 */
public interface ProxyParser {
	/**
	 * @return the proxies
	 * @throws Exception
	 */
	public abstract Set<Proxy> getProxies() throws Exception;
}