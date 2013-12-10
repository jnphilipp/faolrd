package org.lcc.parser.json.sites;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.lcc.parser.json.JSONParser;
import org.lcc.results.google.GoogleMeta;
import org.lcc.results.google.GoogleResult;

/**
*
* @author jnphilipp
* @version 0.0.1
*/
public class GoogleWebSearchJSONParser extends JSONParser {
	private GoogleMeta meta;
	private List<GoogleResult> results;

	public GoogleMeta getMeta() {
		return this.meta;
	}

	public List<GoogleResult> getResults() {
		return this.results;
	}

	@Override
	public void fetch(String query) throws UnsupportedEncodingException, Exception {
		String google = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=";
		String charset = "UTF-8";
		super.fetch(google + URLEncoder.encode(query, charset));

		if ( !this.getJSONContent("responseStatus").equals("200") )
			throw new Exception("Response code is: " + this.getJSONContent("responseStatus"));

		this.meta = new GoogleMeta(google + URLEncoder.encode(query, charset), Integer.parseInt(this.getJSONContent(this.getJSONObject(this.getJSONObject("responseData"), "cursor"), "estimatedResultCount")));

		//"GsearchResultClass", "unescapedUrl", "url", "visibleUrl", "cacheUrl", "title", "titleNoFormatting", "content");
		this.results = new ArrayList<>();
		List<String[]> results_content = this.getJSONContents(this.getJSONArray(this.getJSONObject("responseData"), "results"), "titleNoFormatting", "url", "content");
		for ( String[] result : results_content )
			this.results.add(new GoogleResult(result[0], result[1], result[2]));
		this.meta.setCount(this.results.size());
	}

	public void fetchAll(String query) throws UnsupportedEncodingException, Exception {
		String charset = "UTF-8";
		this.meta = new GoogleMeta("http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=" + URLEncoder.encode(query, charset));

		int i = 0;
		this.results = new ArrayList<>();
		while ( true ) {
			System.out.print(".");
			String google = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&start=" + i * 4 + "&q=";
			System.out.println(google + URLEncoder.encode(query, charset));
			super.fetch(google + URLEncoder.encode(query, charset));

			if ( !this.getJSONContent("responseStatus").equals("200") )
				break;

			if ( this.meta.getEstimatedCount() == 0 )
				this.meta.setEstimatedCount(Integer.parseInt(this.getJSONContent(this.getJSONObject(this.getJSONObject("responseData"), "cursor"), "estimatedResultCount")));

				List<String[]> results_content = this.getJSONContents(this.getJSONArray(this.getJSONObject("responseData"), "results"), "titleNoFormatting", "url", "content");
				for ( String[] result : results_content )
					this.results.add(new GoogleResult(result[0], result[1], result[2]));

			i++;
		}

		System.out.println();
		this.meta.setCount(this.results.size());
	}
}