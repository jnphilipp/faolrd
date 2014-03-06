package org.faolrd.parser.json.sites;

import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import org.faolrd.Manager;
import org.faolrd.parser.PaginatedParser;
import org.faolrd.parser.json.JSONParser;
import org.faolrd.results.google.GoogleMeta;
import org.faolrd.results.google.GoogleResult;

/**
*
* @author jnphilipp
* @version 0.0.2
*/
public class GoogleWebSearchJSONParser extends JSONParser implements PaginatedParser {
	private long sleep = 1000;
	private GoogleMeta meta;
	private List<GoogleResult> results;
	private int page = 64;

	public GoogleWebSearchJSONParser() {
		this.results = new LinkedList<>();
	}

	public GoogleMeta getMeta() {
		return this.meta;
	}

	public List<GoogleResult> getResults() {
		return this.results;
	}

	public long getSleep() {
		return sleep;
	}

	public void setSleep(long sleep) {
		this.sleep = sleep;
	}

	@Override
	public void firstPage() {
		this.page = 64;
	}

	@Override
	public boolean hasNextPage() {
		if ( this.meta == null )
			return true;

		return this.meta.getCount() < this.meta.getEstimatedCount();
	}

	@Override
	public void nextPage() {
		this.page += 8;
	}

	@Override
	public void resetResults() {
		this.meta = null;
		this.results.clear();
	}

	@Override
	public void fetch(String query) throws UnsupportedEncodingException, Exception {
		this.fetch(query, Proxy.NO_PROXY);
	}

	@Override
	public void fetch(String query, Proxy proxy) throws UnsupportedEncodingException, Exception {
		String charset = "UTF-8";
		this.meta = new GoogleMeta("http://ajax.googleapis.com/ajax/services/search/web?v=1.0&rsz=8&q=" + URLEncoder.encode(query, charset));

		super.fetch("http://ajax.googleapis.com/ajax/services/search/web?v=1.0&rsz=8&start=" + this.page + "&q=" + URLEncoder.encode(query, charset), proxy);
		this.responseCode = Integer.parseInt(this.getJSONContent("responseStatus"));
		if ( !this.getJSONContent("responseStatus").equals("200") )
			throw new Exception("Response code is: " + this.getJSONContent("responseStatus"));

		this.meta.setEstimatedCount(Integer.parseInt(this.getJSONContent(this.getJSONObject(this.getJSONObject("responseData"), "cursor"), "estimatedResultCount")));

		//"GsearchResultClass", "unescapedUrl", "url", "visibleUrl", "cacheUrl", "title", "titleNoFormatting", "content");
		List<String[]> results_content = this.getJSONContents(this.getJSONArray(this.getJSONObject("responseData"), "results"), "titleNoFormatting", "url", "content");
		for ( String[] result : results_content )
			this.results.add(new GoogleResult(result[0], result[1], result[2]));
		this.meta.setCount(this.results.size());
		Manager.debug(query, "Count: " + this.meta.getCount());
		Thread.sleep(this.sleep);
	}
}