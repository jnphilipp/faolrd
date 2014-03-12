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
 * @version 0.0.4
 */
public class ProxyManager {
	private static ProxyManager instance;
	private ProxyParser proxyParser;
	private Set<Proxy> proxies;

	private ProxyManager() {
		this.proxies = new LinkedHashSet<>();
	}

	public static synchronized ProxyManager getInstance() {
		if ( instance == null)
			instance = new ProxyManager();

		return instance;
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

	public void fetch(String url, Parser parser) throws Exception {
		if ( this.proxies.isEmpty() )
			this.loadProxies();

		if ( parser instanceof PaginatedParser ) {
			while ( ((PaginatedParser)parser).hasNextPage() ) {
				this.paginatedFetch(url, parser);

				if ( this.proxies.isEmpty() )
					this.loadProxies();
			}
		}
		else
			this.singleFetch(url, parser);
	}

	protected void singleFetch(String url, Parser parser) throws Exception {
		Proxy proxy = this.proxies.iterator().next();
		Manager.debug(ProxyManager.class, proxy.toString());

		try {
			parser.fetch(url, proxy);
			if ( parser.getResponseCode() != HttpURLConnection.HTTP_OK ) {
				Manager.error(ProxyManager.class, "URL: " + url, "Response code: " + parser.getResponseCode());

				if ( proxy == Proxy.NO_PROXY )
					throw new IOException("Response code ist: " + parser.getResponseCode() + " without any proxy.");

				this.proxies.remove(proxy);
				this.singleFetch(url, parser);
			}

			this.proxies.add(proxy);
		}
		catch ( SSLException e ) {
			Manager.error(ProxyManager.class, e.toString());
			this.proxies.remove(proxy);
			this.singleFetch(url, parser);
		}
		catch ( IOException e ) {
			Manager.error(ProxyManager.class, e.toString());
			this.proxies.remove(proxy);
			this.singleFetch(url, parser);
		}
	}

	protected void paginatedFetch(String url, Parser parser) throws Exception {
		this.singleFetch(url, parser);
		((PaginatedParser)parser).nextPage();
	}
}