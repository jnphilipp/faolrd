package org.faolrd.net;

import java.net.InetSocketAddress;
import java.util.Set;
import org.faolrd.Manager;
import org.faolrd.parser.PaginatedParser;
import org.faolrd.parser.Parser;
import org.faolrd.parser.ProxyParser;

/**
 *
 * @author jnphilipp
 * @version 0.0.1
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

	protected java.net.Proxy getProxy(Proxy proxy) {
		java.net.Proxy.Type type = (proxy.getType() == Proxy.SOCKS ? java.net.Proxy.Type.SOCKS : java.net.Proxy.Type.HTTP);
		InetSocketAddress socket = new InetSocketAddress(proxy.getIP(), proxy.getPort());
		return new java.net.Proxy(type, socket);
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

		this.parser.fetch(url, this.getProxy(proxy));
		this.proxies.remove(proxy);
	}

	protected void paginatedFetch(String url) throws Exception {
		this.singleFetch(url);
		((PaginatedParser)this.parser).nextPage();
	}
}