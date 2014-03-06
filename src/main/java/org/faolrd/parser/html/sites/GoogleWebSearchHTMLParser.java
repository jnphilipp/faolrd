package org.faolrd.parser.html.sites;

import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.faolrd.Manager;
import org.faolrd.parser.PaginatedParser;
import org.faolrd.parser.html.HTMLParser;
import org.faolrd.results.Meta;
import org.faolrd.results.Result;
import org.faolrd.results.google.GoogleMeta;
import org.faolrd.results.google.GoogleResult;

/**
 *
 * @author jnphilipp
 * @version 0.0.1
 */
public class GoogleWebSearchHTMLParser extends HTMLParser implements PaginatedParser {
	private int page = 0;
	private int numPerPage = 1000;
	private GoogleMeta meta;
	private List<Result> results;

	public GoogleWebSearchHTMLParser() {
		super();
		this.results = new LinkedList<>();
	}

	@Override
	public Meta getMeta() {
		return this.meta;
	}

	@Override
	public List<Result> getResults() {
		return this.results;
	}

	@Override
	public void firstPage() {
		this.page = 0;
	}

	@Override
	public boolean hasNextPage() {
		return this.page < 1000;
	}

	@Override
	public void nextPage() {
		this.page += this.numPerPage;
	}

	@Override
	public void resetResults() {
		this.results.clear();
	}

	@Override
	public void fetch(String query) throws UnsupportedEncodingException, Exception {
		this.fetch(query, Proxy.NO_PROXY);
	}

	@Override
	public void fetch(String query, Proxy proxy) throws UnsupportedEncodingException, Exception {
		String url = "https://www.google.com/search?q={0}&start={1}&num={2}&ie=utf-8";
		super.fetch(MessageFormat.format(url, URLEncoder.encode(query, "UTF-8"), String.valueOf(this.page), String.valueOf(this.numPerPage)), true, proxy);

		if ( this.meta == null )
			this.meta = new GoogleMeta(MessageFormat.format("https://www.google.com/search?q={0}&ie=utf-8", URLEncoder.encode(query, "UTF-8")));

		List<String> tags = this.getTags("li", "class=\"g\"");
		for ( String tag : tags ) {
			GoogleResult result = new GoogleResult();
			Matcher title = Pattern.compile("<h3 class=\"r\">[^<]*<a href=\"/url\\?q=([^\"]*)&sa=U&ei=[^\"]*\"[^>]*>(.*?)</a>", Pattern.DOTALL | Pattern.MULTILINE).matcher(tag);
			if ( title.find() ) {
				result.setUrl(URLDecoder.decode(title.group(1).trim(), "UTF-8"));
				result.setTitle(title.group(2).replaceAll("<[^>]*>", "").trim());
			}

			Matcher snippet = Pattern.compile("<span class=\"st\">(?<snippet>.*?)</span>", Pattern.DOTALL | Pattern.MULTILINE).matcher(tag);
			if ( snippet.find() )
				result.setContent(snippet.group("snippet").trim().replaceAll("<[^>]*>", ""));

			this.results.add(result);
		}
		Manager.info(GoogleWebSearchHTMLParser.class, "Results: " + this.results.size());
	}
}