package org.faolrd.parser.html.sites;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.faolrd.parser.PaginatedParser;
import org.faolrd.parser.ProxyParser;
import org.faolrd.parser.html.HTMLParser;
import org.faolrd.results.Meta;
import org.faolrd.results.Result;

/**
 *
 * @author jnphilipp
 * @version 0.0.2
 */
public class HideMyAssHTMLParser extends HTMLParser implements PaginatedParser, ProxyParser {
	private int maxPages = 10;
	private int page = 1;
	private Set<Proxy> proxies;

	/**
	 * @return the maxPages
	 */
	public int getMaxPages() {
		return this.maxPages;
	}

	/**
	 * @param maxPages the maxPages to set
	 */
	public void setMaxPages(int maxPages) {
		this.maxPages = maxPages;
	}

	@Override
	public Meta getMeta() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public List<Result> getResults() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	/**
	 * @return the proxies
	 * @throws Exception
	 */
	@Override
	public Set<Proxy> getProxies() throws Exception {
		this.resetResults();
		this.firstPage();

		while ( this.hasNextPage() ) {
			this.fetch();
			this.nextPage();
		}

		return this.proxies;
	}

	/**
	 * @param proxies the proxies to set
	 */
	public void setProxies(Set<Proxy> proxies) {
		this.proxies = proxies;
	}

	@Override
	public void firstPage() {
		this.page = 1;
	}

	@Override
	public boolean hasNextPage() {
		return this.page <= this.maxPages;
	}

	@Override
	public void nextPage() {
		this.page++;
	}

	@Override
	public void resetResults() {
		this.proxies.clear();
	}

	public HideMyAssHTMLParser() {
		this.proxies = new LinkedHashSet<>();
	}

	public void fetch() throws Exception {
		String url = "https://hidemyass.com/proxy-list/";
		super.fetch(url + this.page);

		List<String> lines = this.getTags("tr");
		for ( String line : lines ) {
			if ( line.contains("theader") )
				continue;

			Matcher cells = Pattern.compile("<td[^>]*>(.*?)</td[^>]*>[^<]*<td[^>]*>(.*?)</td[^>]*>[^<]*<td[^>]*>(.*?)</td[^>]*>[^<]*<td[^>]*>(.*?)</td[^>]*>[^<]*<td[^>]*>(.*?)</td[^>]*>[^<]*<td[^>]*>(.*?)</td[^>]*>[^<]*<td[^>]*>(.*?)</td[^>]*>[^<]*<td[^>]*>(.*?)</td[^>]*>", Pattern.MULTILINE | Pattern.DOTALL).matcher(line);

			if ( cells.find() ) {
				String ip = cells.group(2);
				int port = Integer.parseInt(cells.group(3).trim());
				Proxy.Type type = Proxy.Type.DIRECT;
				if ( cells.group(7).trim().equalsIgnoreCase("HTTP") || cells.group(7).trim().equalsIgnoreCase("HTTPS") )
					type = Proxy.Type.HTTP;
				else if ( cells.group(7).trim().equalsIgnoreCase("socks4/5") )
					type = Proxy.Type.SOCKS;

				Matcher matcher = Pattern.compile("[.](-?[_a-zA-Z]+[_a-zA-Z0-9-]*)\\s*[{]display\\s*[:]\\s*none[}]").matcher(ip);
				while ( matcher.find() )
					ip = ip.replaceAll("<[^\">]+\"" + matcher.group(1) + "\">[^<]+</[^>]+>", "");

				ip = ip.replaceAll("<[^\">]+\"display:[Nn]one\">[^<]+</[^>]+>", "");
				ip = ip.replaceAll("<style>[^<]+</style>", "");
				ip = ip.replaceAll("<[^>]+>", "").trim();

				InetSocketAddress socket;
				try {
					socket = new InetSocketAddress(ip, port);
				}
				catch ( Exception e ) {
					continue;
				}
				this.proxies.add(new Proxy(type, socket));
			}
		}
	}
}