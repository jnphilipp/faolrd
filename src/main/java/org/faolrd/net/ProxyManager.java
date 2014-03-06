package org.faolrd.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.net.ssl.SSLException;
import org.faolrd.Manager;
import org.faolrd.parser.PaginatedParser;
import org.faolrd.parser.Parser;
import org.faolrd.parser.ProxyParser;

/**
 *
 * @author jnphilipp
 * @version 0.0.3
 */
public class ProxyManager {
	private Parser parser;
	private ProxyParser proxyParser;
	private Set<Proxy> proxies;

	public ProxyManager() {
		this.proxies = new LinkedHashSet<>();
	}

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
		if ( this.proxyParser == null )
			this.proxies.add(Proxy.NO_PROXY);
		else 
			this.proxies = this.proxyParser.getProxies();
		Manager.debug(ProxyManager.class, "Proxies: " + this.proxies.size());
	}

	public void fetch(String url) throws Exception {
		if ( this.proxies.isEmpty() )
			this.loadProxies();

		if ( this.parser instanceof PaginatedParser ) {
			while ( ((PaginatedParser)this.parser).hasNextPage() ) {
				this.paginatedFetch(url);

				if ( this.proxies.isEmpty() )
					this.loadProxies();
			}
		}
		else
			this.singleFetch(url);
	}

	protected void singleFetch(String url) throws Exception {
		Proxy proxy = this.proxies.iterator().next();
		this.proxies.remove(proxy);
		Manager.debug(ProxyManager.class, proxy.toString());

		try {
			this.parser.fetch(url, proxy);
			if ( this.parser.getResponseCode() != HttpURLConnection.HTTP_OK ) {
				Manager.error(ProxyManager.class, "Response code: " + this.parser.getResponseCode());
				this.singleFetch(url);
			}

			this.proxies.add(proxy);
		}
		catch ( SSLException e ) {
			Manager.error(ProxyManager.class, e.toString());
			this.singleFetch(url);
		}
		catch ( IOException e ) {
			Manager.error(ProxyManager.class, e.toString());
			this.singleFetch(url);
		}
	}

	protected void paginatedFetch(String url) throws Exception {
		this.singleFetch(url);
		((PaginatedParser)this.parser).nextPage();
	}
}