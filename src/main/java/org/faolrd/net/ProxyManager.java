package org.faolrd.net;

import java.net.Proxy;
import java.util.Set;
import org.faolrd.Manager;
import org.faolrd.parser.PaginatedParser;
import org.faolrd.parser.Parser;
import org.faolrd.parser.ProxyParser;

/**
 *
 * @author jnphilipp
 * @version 0.0.2
 */
public class ProxyManager {
	private Parser parser;
	private ProxyParser proxyParser;
	private Set<Proxy> proxies;

	/**
	 * @return the parser
	 */
	public Parser getParser() {
		return this.parser;
	}

	/**
	 * @param parser the parser to set
	 */
	public void setParser(Parser parser) {
		this.parser = parser;
	}

	/**
	 * @return the proxyParser
	 */
	public ProxyParser getProxyParser() {
		return this.proxyParser;
	}

	/**
	 * @param proxyParser the proxyParser to set
	 */
	public void setProxyParser(ProxyParser proxyParser) {
		this.proxyParser = proxyParser;
	}

	public void loadProxies() throws Exception {
		this.proxies = this.proxyParser.getProxies();
	}

	public void fetch(String url) throws Exception {
		this.proxies = this.proxyParser.getProxies();
		Manager.debug(ProxyManager.class, "Proxies: " + this.proxies.size());

		if ( this.parser instanceof PaginatedParser ) {
			while ( ((PaginatedParser)this.parser).hasNextPage() ) {
				this.paginatedFetch(url);

				if ( this.proxies.isEmpty() )
					this.proxies = this.proxyParser.getProxies();
			}
		}
		else
			this.singleFetch(url);
	}

	protected void singleFetch(String url) throws Exception {
		Proxy proxy = this.proxies.iterator().next();
		Manager.debug(ProxyManager.class, proxy.toString());

		this.parser.fetch(url, proxy);
		this.proxies.remove(proxy);
	}

	protected void paginatedFetch(String url) throws Exception {
		this.singleFetch(url);
		((PaginatedParser)this.parser).nextPage();
	}
}