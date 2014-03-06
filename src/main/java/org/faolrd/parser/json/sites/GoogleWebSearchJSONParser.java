package org.faolrd.parser.json.sites;

import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import org.faolrd.Manager;
import org.faolrd.parser.PaginatedParser;
import org.faolrd.parser.json.JSONParser;
import org.faolrd.results.Meta;
import org.faolrd.results.Result;
import org.faolrd.results.google.GoogleMeta;
import org.faolrd.results.google.GoogleResult;

/**
*
* @author jnphilipp
* @version 0.0.2
*/
public class GoogleWebSearchJSONParser extends JSONParser implements PaginatedParser {
	private boolean hasNextPage = true;
	private int page = 0;
	private Meta meta;
	private List<Result> results;

	public GoogleWebSearchJSONParser() {
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
		this.hasNextPage = true;
	}

	@Override
	public boolean hasNextPage() {
		return this.hasNextPage;
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
		if ( !this.getJSONContent("responseStatus").equals("200") ) {
			this.hasNextPage = false;
			Manager.debug(GoogleWebSearchJSONParser.class, "Response status: " + this.getJSONContent("responseStatus"));
			return;
		}

		((GoogleMeta)this.meta).setEstimatedCount(Integer.parseInt(this.getJSONContent(this.getJSONObject(this.getJSONObject("responseData"), "cursor"), "estimatedResultCount")));

		//"GsearchResultClass", "unescapedUrl", "url", "visibleUrl", "cacheUrl", "title", "titleNoFormatting", "content");
		List<String[]> results_content = this.getJSONContents(this.getJSONArray(this.getJSONObject("responseData"), "results"), "titleNoFormatting", "url", "content");
		for ( String[] result : results_content )
			this.results.add(new GoogleResult(result[0], result[1], result[2]));
		this.meta.setCount(this.results.size());
		Manager.debug(GoogleWebSearchJSONParser.class, query, "Count: " + this.meta.getCount());
	}
}