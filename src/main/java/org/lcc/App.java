package org.lcc;

import java.net.URLEncoder;
import java.util.List;

import org.lcc.parser.json.JSONParser;
import org.lcc.parser.json.sites.GoogleWebSearchJSONParser;
import org.lcc.results.google.GoogleResult;

/**
*
* @author jnphilipp
* @version 0.0.1
*/
public class App {
	public static void main(String[] args) throws Exception {
		System.out.println("lcc");
		//JSONParser json = new JSONParser();
		//String SERVER_KEY = "AIzaSyBvhNin0Ao5MPsl_7119mqBsxUnvNKQ2t4";
		//String BROWSER_KEY = "AIzaSyDCKRnV8rIAktKIrYkdL_50WXga50sbSQQ";
		//json.fetch("https://www.googleapis.com/customsearch/v1?key=" + SERVER_KEY + "&cx=017576662512468239146:omuauf_lfve&q=lectures");
		//System.out.println(json.getJSON());

		/*for ( int i = 0; i < 100; i++ ) {
			String google = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&start=" + i * 4 + "&q=";
			String search = "stackoverflow";
			String charset = "UTF-8";

			JSONParser json = new JSONParser();
			json.fetch(google + URLEncoder.encode(search, charset));

			List<String[]> results = json.getJSONContents(json.getJSONArray(json.getJSONObject("responseData"), "results"), "titleNoFormatting", "url");//"GsearchResultClass", "unescapedUrl", "url", "visibleUrl", "cacheUrl", "title", "titleNoFormatting", "content");
			for ( String[] result : results ) {
				for ( int j = 0; j < result.length; j++ )
					System.out.println(result[j]);
				System.out.println();
			}
		}*/

		GoogleWebSearchJSONParser g = new GoogleWebSearchJSONParser();
		g.fetchAll("stackoverflow");
		System.out.println(g.getMeta().getUrl() + "\nEstimated Count: " + g.getMeta().getEstimatedCount() + "\nCount: " + g.getMeta().getCount() + "\n");
		for ( GoogleResult result : g.getResults() )
			System.out.println(result.getUrl() + "\n" + result.getTitle());
	}
}