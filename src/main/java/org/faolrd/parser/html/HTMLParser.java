package org.faolrd.parser.html;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.faolrd.parser.Parser;

/**
 *
 * @author jnphilipp
 * @version 1.7.4
 */
public class HTMLParser implements Parser {
	public static final String DEFAULT_USER_AGENT = "Mozilla/5.0";
	/**
	 * response code
	 */
	protected int responseCode = 0;
	/**
	 * Code of the web page.
	 */
	protected String code = "";
	/**
	 * User-Agent which is used when connecting to the page.
	 */
	protected String userAgent = "";
	/**
	 * content type
	 */
	protected String contentType = "";

	/**
	 * Default constructor.
	 */
	public HTMLParser() {
		this.setDefaultUserAgent();
	}

	/**
	 * Creates a new HTMLParser and fetches the given site.
	 * @param url URL which will be fetched
	 */
	public HTMLParser(String url) throws Exception {
		this.setDefaultUserAgent();
		this.fetch(url);
	}

	/**
	 * Creates a new HTMLParser and fetches the given site.
	 * @param url URL which will be fetched
	 * @param decodeHTML if <code>true</code> HTML encoded characters will be decoded.
	 */
	public HTMLParser(String url, boolean decodeHTML) throws Exception {
		this.setDefaultUserAgent();
		this.fetch(url, decodeHTML);
	}

	/**
	 * Sets the User-Agent to the given agent.
	 * @param agent New User-Agent to use.
	 */
	public void setUserAgent(String agent) {
		this.userAgent = agent;
	}

	/**
	 * Sets the User-Agent to the default User-Agent.
	 */
	public void setDefaultUserAgent() {
		this.userAgent = HTMLParser.DEFAULT_USER_AGENT;
	}

	/**
	 * Returns the web pages code.
	 * @return code
	 */
	public String getCode() {
		return this.code;
	}

	/**
	 * Returns the content type.
	 * @return content type
	 */
	public String getContentType() {
		return this.contentType;
	}

	@Override
	public int getResponseCode() {
		return this.responseCode;
	}

	@Override
	public boolean isResponseCodeOK() {
		return this.responseCode == HttpURLConnection.HTTP_OK;
	}

	/**
	 * Builds a connection to the given URL and retrieves it. If a user-agent is given it will be used.
	 * @param url URL which will be fetched
	 * @throws Exception 
	 */
	@Override
	public void fetch(String url) throws Exception {
		this.fetch(url, false, Proxy.NO_PROXY);
	}

	/**
	 * Builds a connection to the given URL and retrieves it. If a user-agent is given it will be used.
	 * @param url URL which will be fetched
	 * @param docodeHTML if <code>true</code> HTML encoded characters will be decoded.
	 * @param proxy proxy
	 * @throws Exception 
	 */
	public void fetch(String url, boolean decodeHTML) throws Exception {
		this.fetch(url, decodeHTML, Proxy.NO_PROXY);
	}

	/**
	 * Builds a connection to the given URL using the given proxy and retrieves it. If a user-agent is given it will be used.
	 * @param url URL which will be fetched
	 * @param proxy proxy
	 * @throws Exception 
	 */
	@Override
	public void fetch(String url, Proxy proxy) throws Exception {
		this.fetch(url, false, proxy);
	}

	/**
	 * Builds a connection to the given URL and retrieves it. If a user-agent is given it will be used.
	 * @param url URL which will be fetched
	 * @param docodeHTML if <code>true</code> HTML encoded characters will be decoded.
	 * @param proxy proxy
	 * @throws Exception 
	 */
	public void fetch(String url, boolean decodeHTML, Proxy proxy) throws Exception {
		if ( proxy == null )
			proxy = Proxy.NO_PROXY;

		URL u = new URL(url);
		HttpURLConnection con = (HttpURLConnection)u.openConnection(proxy);

		if ( !this.userAgent.equals("") )
			con.setRequestProperty("User-Agent", this.userAgent);

		con.setRequestProperty("Accept-Charset", "UTF-8");
		con.connect();
		String header = con.getHeaderField("Content-Type");
		String charset = "utf-8";

		if ( header != null ) {
			if ( header.contains("ISO-8859-15") )
  			charset = "ISO-8859-15";
			else if ( header.contains("ISO-8859-1") )
				charset = "ISO-8859-1";
		}

		this.contentType = header;
		this.responseCode = con.getResponseCode();

		if ( this.responseCode == HttpURLConnection.HTTP_OK ) {
			InputStreamReader in = new InputStreamReader(con.getInputStream(), charset);
			BufferedReader buff = new BufferedReader(in);

			String line;
			StringBuilder text = new StringBuilder();

			while ( (line = buff.readLine()) != null ) {
				text.append(line);
				text.append("\n");
			}

			buff.close();
			in.close();

			this.code = text.toString().replace("\0", " ").replace("\u2028", "\n").replace(String.valueOf((char)160), " ");
		}
		con.disconnect();

		if ( decodeHTML )
			this.code = this.decode();
	}

	/**
	 * Returns a list of the entire code beginning with the given tag and ending with the corresponding closing tag. By nested tags the most outer tag with all containing code will be returns.
	 * @param tag HTML-tag like b, i, div
	 * @return list of tags
	 */
	public List<String> getTags(String tag) {
		return getTags(tag, "", false, this.code);
	}

	/**
	 * Returns a list of the entire code beginning with the given tag and ending with the corresponding closing tag. By nested tags the most outer tag with all containing code will be returns.
	 * @param tag HTML-tag like b, i, div
	 * @return first occurrence for given tag
	 */
	public String getFirstTag(String tag) {
		List<String> tags = getTags(tag, "", false, this.code);

		if ( tags.isEmpty() )
			return "";

		return tags.get(0);
	}

	/**
	 * Returns a list of the entire code beginning with the given tag and ending with the corresponding closing tag. By nested tags the most outer tag with all containing code will be returns.
	 * @param tag HTML-tag like b, i, div
	 * @param clean If <code>true</code> all HTML-code will be cleaned.
	 * @return list of tags
	 */
	public List<String> getTags(String tag, boolean clean) {
		return getTags(tag, "", clean, this.code);
	}

	/**
	 * Returns a list of the entire code beginning with the given tag and ending with the corresponding closing tag. By nested tags the most outer tag with all containing code will be returns.
	 * @param tag HTML-tag like b, i, div
	 * @param clean If <code>true</code> all HTML-code will be cleaned.
	 * @return first occurrence for given tag
	 */
	public String getFirstTag(String tag, boolean clean) {
		List<String> tags = getTags(tag, "", clean, this.code);

		if ( tags.isEmpty() )
			return "";

		return tags.get(0);
	}

	/**
	 * Returns a list of the entire code beginning with the given tag and ending with the corresponding closing tag. By nested tags the most outer tag with all containing code will be returns.
	 * @param tag HTML-tag like b, i, div
	 * @param param A parameter the tags must contain.
	 * @return list of tags
	 */
	public List<String> getTags(String tag, String param) {
		return getTags(tag, param, false, this.code);
	}

	/**
	 * Returns a list of the entire code beginning with the given tag and ending with the corresponding closing tag. By nested tags the most outer tag with all containing code will be returns.
	 * @param tag HTML-tag like b, i, div
	 * @param param A parameter the tags must contain.
	 * @return first occurrence for given tag
	 */
	public String getFirstTag(String tag, String param) {
		List<String> tags = getTags(tag, param, false, this.code);

		if ( tags.isEmpty() )
			return "";

		return tags.get(0);
	}

	/**
	 * Returns a list of the entire code beginning with the given tag and ending with the corresponding closing tag. By nested tags the most outer tag with all containing code will be returns.
	 * @param tag HTML-tag like b, i, div
	 * @param param A parameter the tags must contain.
	 * @param clean If <code>true</code> all HTML-code will be cleaned.
	 * @return list of tags
	 */
	public List<String> getTags(String tag, String param, boolean clean) {
		return getTags(tag, param, clean, this.code);
	}

	/**
	 * Returns a list of the entire code beginning with the given tag and ending with the corresponding closing tag. By nested tags the most outer tag with all containing code will be returns.
	 * @param tag HTML-tag like b, i, div
	 * @param param A parameter the tags must contain.
	 * @param clean If <code>true</code> all HTML-code will be cleaned.
	 * @return first occurrence for given tag
	 */
	public String getFirstTag(String tag, String param, boolean clean) {
		List<String> tags = getTags(tag, param, clean, this.code);

		if ( tags.isEmpty() )
			return "";

		return tags.get(0);
	}

	/**
	 * Returns a list of the entire code beginning with the given tag and ending with the corresponding closing tag. By nested tags the most outer tag with all containing code will be returns.
	 * @param tag HTML-tag like b, i, div
	 * @param param A parameter the tags must contain.
	 * @param code The HTML-code which will be searched.
	 * @return list of tags
	 */
	public static List<String> getTags(String tag, String param, String code) {
		return getTags(tag, param, false, code);
	}

	/**
	 * Returns a list of the entire code beginning with the given tag and ending with the corresponding closing tag. By nested tags the most outer tag with all containing code will be returns.
	 * @param tag HTML-tag like b, i, div
	 * @param param A parameter the tags must contain.
	 * @param code The HTML-code which will be searched.
	 * @return first occurrence for given tag
	 */
	public static String getFirstTag(String tag, String param, String code) {
		List<String> tags = getTags(tag, param, false, code);

		if ( tags.isEmpty() )
			return "";

		return tags.get(0);
	}

	/**
	 * Returns a list of the entire code beginning with the given tag and ending with the corresponding closing tag. By nested tags the most outer tag with all containing code will be returns.
	 * @param tag HTML-tag like b, i, div
	 * @param clean If <code>true</code> all HTML-code will be cleaned.
	 * @param code The HTML-code which will be searched.
	 * @return list of tags
	 */
	public static List<String> getTags(String tag, boolean clean, String code) {
		return getTags(tag, "", clean, code);
	}

	/**
	 * Returns a list of the entire code beginning with the given tag and ending with the corresponding closing tag. By nested tags the most outer tag with all containing code will be returns.
	 * @param tag HTML-tag like b, i, div
	 * @param clean If <code>true</code> all HTML-code will be cleaned.
	 * @param codes The HTML-codes which will be searched.
	 * @return list of tags
	 */
	public static List<String> getTags(String tag, boolean clean, Collection<String> codes) {
		List<String> tags = new ArrayList<>();
		for ( String code : codes )
			tags.addAll(getTags(tag, "", clean, code));

		return tags;
	}

	/**
	 * Returns a list of the entire code beginning with the given tag and ending with the corresponding closing tag. By nested tags the most outer tag with all containing code will be returns.
	 * @param tag HTML-tag like b, i, div
	 * @param clean If <code>true</code> all HTML-code will be cleaned.
	 * @param code The HTML-code which will be searched.
	 * @return first occurrence for given tag
	 */
	public static String getFirstTag(String tag, boolean clean, String code) {
		List<String> tags = getTags(tag, "", clean, code);

		if ( tags.isEmpty() )
			return "";

		return tags.get(0);
	}

	/**
	 * Returns a list of the entire code beginning with the given tag and ending with the corresponding closing tag. By nested tags the most outer tag with all containing code will be returns.
	 * @param tag HTML-tag like b, i, div
	 * @param param A parameter the tags must contain.
	 * @param clean If <code>true</code> all HTML-code will be cleaned.
	 * @param code The HTML-code which will be searched.
	 * @return list of tags
	 */
	public static List<String> getTags(String tag, String param, boolean clean, String code) {
		ArrayList<String> l = new ArrayList<>();

		int i = -1;

		while ( (i = code.indexOf("<" + tag, i + 1)) != -1 ) {
			if ( !param.equals("") ) {
				if ( !code.substring(i, code.indexOf(">", i)).contains(param) ) {
					i = code.indexOf(">", i);
					continue;
				}
			}

			int j = code.indexOf("</" + tag, i);
			String s = code.substring(i, code.indexOf(">", j) + 1);

			int k = tag.length();

			while ( (k = s.indexOf("<" + tag, k)) != -1 ) {
				k = s.indexOf(">", k);
				j = code.indexOf("</" + tag, j + tag.length());

				if ( j < 0 )
					break;

				s = code.substring(i, code.indexOf(">", j) + 1);
			}

			if ( j < 0 ) {
				i += tag.length() + param.length();
				continue;
			}

			if ( clean )
				s = s.replaceAll("<.*?>", "");

			if ( !s.equals("") )
				l.add(s);

			i = j;
		}

		return l;
	}

	/**
	 * 
	 * Returns a list of the entire code beginning with the given tag and ending with the corresponding closing tag. By nested tags the most outer tag with all containing code will be returns.
	 * @param param A parameter the tags must contain.
	 * @param clean If <code>true</code> all HTML-code will be cleaned.
	 * @param code The HTML-code which will be searched.
	 * @return first occurrence for given tag
	 */
	public static String getFirstTag(String tag, String param, boolean clean, String code) {
		List<String> tags = getTags(tag, param, clean, code);

		if ( tags.isEmpty() )
			return "";

		return tags.get(0);
	}

	/**
	 * Returns a list of the entire code beginning with the given tag and ending with the corresponding closing tag. By nested tags the most outer tag with all containing code will be returns. Makes sure that the HTML-tags only contains the given value.
	 * @param tag HTML-tag like b, i, div
	 * @return list of tags
	 */
	public List<String> getOnlyTags(String tag) {
		return getOnlyTags(tag, false, this.code);
	}

	/**
	 * Returns a list of the entire code beginning with the given tag and ending with the corresponding closing tag. By nested tags the most outer tag with all containing code will be returns. Makes sure that the HTML-tags only contains the given value.
	 * @param tag HTML-tag like b, i, div
	 * @return first occurrence for given tag
	 */
	public String getFirstOnlyTag(String tag) {
		List<String> tags = getOnlyTags(tag, false, this.code);

		if ( tags.isEmpty() )
			return "";

		return tags.get(0);
	}

	/**
	 * Returns a list of the entire code beginning with the given tag and ending with the corresponding closing tag. By nested tags the most outer tag with all containing code will be returns. Makes sure that the HTML-tags only contains the given value.
	 * @param tag HTML-tag like b, i, div
	 * @param clean If <code>true</code> all HTML-code will be cleaned.
	 * @return list of tags
	 */
	public List<String> getOnlyTags(String tag, boolean clean) {
		return getOnlyTags(tag, clean, this.code);
	}

	/**
	 * Returns a list of the entire code beginning with the given tag and ending with the corresponding closing tag. By nested tags the most outer tag with all containing code will be returns. Makes sure that the HTML-tags only contains the given value.
	 * @param tag HTML-tag like b, i, div
	 * @param clean If <code>true</code> all HTML-code will be cleaned.
	 * @return first occurrence for given tag
	 */
	public String getFirstOnlyTag(String tag, boolean clean) {
		List<String> tags = getOnlyTags(tag, clean, this.code);

		if ( tags.isEmpty() )
			return "";

		return tags.get(0);
	}

	/**
	 * Returns a list of the entire code beginning with the given tag and ending with the corresponding closing tag. By nested tags the most outer tag with all containing code will be returns. Makes sure that the HTML-tags only contains the given value.
	 * @param tag HTML-tag like b, i, div
	 * @param code The HTML-code which will be searched.
	 * @return list of tags
	 */
	public static List<String> getOnlyTags(String tag, String code) {
		return getOnlyTags(tag, false, code);
	}

	/**
	 * Returns a list of the entire code beginning with the given tag and ending with the corresponding closing tag. By nested tags the most outer tag with all containing code will be returns. Makes sure that the HTML-tags only contains the given value.
	 * @param tag HTML-tag like b, i, div
	 * @param code The HTML-code which will be searched.
	 * @return first occurrence for given tag
	 */
	public String getFirstOnlyTag(String tag, String code) {
		List<String> tags = getOnlyTags(tag, false, code);

		if ( tags.isEmpty() )
			return "";

		return tags.get(0);
	}

	/**
	 * Returns a list of the entire code beginning with the given tag and ending with the corresponding closing tag. By nested tags the most outer tag with all containing code will be returns. Makes sure that the HTML-tags only contains the given value.
	 * @param tag HTML-tag like b, i, div
	 * @param clean If <code>true</code> all HTML-code will be cleaned.
	 * @param code The HTML-code which will be searched.
	 * @return list of tags
	 */
	public static List<String> getOnlyTags(String tag, boolean clean, String code) {
		ArrayList<String> l = new ArrayList<>();

		int i = -1;

		while ( (i = code.indexOf("<" + tag + ">", i + 1)) != -1 ) {
			int j = code.indexOf("</" + tag + ">", i);
			String s = code.substring(i, code.indexOf(">", j) + 1);

			int k = tag.length();

			while ( (k = s.indexOf("<" + tag + ">", k)) != -1 ) {
				k = s.indexOf(">", k);
				j = code.indexOf("</" + tag + ">", j + tag.length());

				if ( j < 0 )
					break;

				s = code.substring(i, code.indexOf(">", j) + 1);
			}

			if ( clean )
				s = s.replaceAll("<.*?>", "");

			if ( !s.equals("") )
				l.add(s);

			i = j;
		}

		return l;
	}

	/**
	 * Returns a list of the entire code beginning with the given tag and ending with the corresponding closing tag. By nested tags the most outer tag with all containing code will be returns. Makes sure that the HTML-tags only contains the given value.
	 * @param tag HTML-tag like b, i, div
	 * @param clean If <code>true</code> all HTML-code will be cleaned.
	 * @param code The HTML-code which will be searched.
	 * @return first occurrence for given tag
	 */
	public String getFirstOnlyTag(String tag, boolean clean, String code) {
		List<String> tags = getOnlyTags(tag, clean, code);

		if ( tags.isEmpty() )
			return "";

		return tags.get(0);
	}

	/**
	 * Returns a list of the entire code of the given tag.
	 * @param tag HTML-tag like b, i, div
	 * @return list of tag occurrences
	 */
	public List<String> getTagsWithoutEnd(String tag) {
		return getTagsWithoutEnd(tag, "", "", this.code);
	}

	/**
	 * Returns a list of the entire code of the given tag.
	 * @param tag HTML-tag like b, i, div
	 * @return first occurrences of given tag
	 */
	public String getFirstTagWithoutEnd(String tag) {
		List<String> tags = getTagsWithoutEnd(tag, "", "", this.code);

		if ( tags.isEmpty() )
			return "";

		return tags.get(0);
	}

	/**
	 * Returns a list of the entire code of the given tag.
	 * @param tag HTML-tag like b, i, div
	 * @param param A parameter the tags must contain.
	 * @return list of tag occurrences
	 */
	public List<String> getTagsWithoutEnd(String tag, String param) {
		return getTagsWithoutEnd(tag, param, "", this.code);
	}

	/**
	 * Returns a list of the entire code of the given tag.
	 * @param tag HTML-tag like b, i, div
	 * @param param A parameter the tags must contain.
	 * @return first occurrences of given tag
	 */
	public String getFirstTagWithoutEnd(String tag, String param) {
		List<String> tags = getTagsWithoutEnd(tag, param, "", this.code);

		if ( tags.isEmpty() )
			return "";

		return tags.get(0);
	}

	/**
	 * Returns a list of the entire code of the given tag.
	 * @param tag HTML-tag like b, i, div
	 * @param param A parameter the tags must contain.
	 * @param returnContent regular expression for the content that should be returned
	 * @return list of tag occurrences
	 */
	public List<String> getTagsWithoutEnd(String tag, String param, String returnContent) {
		return getTagsWithoutEnd(tag, param, returnContent, this.code);
	}

	/**
	 * Returns a list of the entire code of the given tag.
	 * @param tag HTML-tag like b, i, div
	 * @param param A parameter the tags must contain.
	 * @param returnContent regular expression for the content that should be returned
	 * @return first occurrences of given tag
	 */
	public String getFirstTagWithoutEnd(String tag, String param, String returnContent) {
		List<String> tags = getTagsWithoutEnd(tag, param, returnContent, this.code);

		if ( tags.isEmpty() )
			return "";

		return tags.get(0);
	}

	/**
	 * Returns a list of the entire code of the given tag.
	 * @param tag HTML-tag like b, i, div
	 * @param param A parameter the tags must contain.
	 * @param returnContent regular expression for the content that should be returned
	 * @param code The HTML-code which will be searched.
	 * @return list of tag occurrences
	 */
	public static List<String> getTagsWithoutEnd(String tag, String param, String returnContent, String code) {
		List<String> l = new ArrayList<>();

		Pattern p = Pattern.compile("<" + Pattern.quote(tag) + "[^>]*?" + Pattern.quote(param) + "[^>]*?>");
		Matcher m = p.matcher(code);
		while ( m.find() ) {
			if ( returnContent.isEmpty() )
				l.add(m.group());
			else {
				p = Pattern.compile(returnContent);
				Matcher mrc = p.matcher(m.group());
				while ( mrc.find() )
					l.add(mrc.group(mrc.groupCount()));
			}
		}

		return l;
	}

	/**
	 * Returns a list of the entire code of the given tag.
	 * @param tag HTML-tag like b, i, div
	 * @param param A parameter the tags must contain.
	 * @param returnContent regular expression for the content that should be returned
	 * @param code The HTML-code which will be searched.
	 * @return first occurrences of given tag
	 */
	public static String getFirstTagWithoutEnd(String tag, String param, String returnContent, String code) {
		List<String> tags = getTagsWithoutEnd(tag, param, returnContent, code);

		if ( tags.isEmpty() )
			return "";

		return tags.get(0);
	}

	/**
	 * Removes all tags from the parsed code.
	 * @return clean code
	 */
	public String removeAllTags() {
		return removeAllTags(this.code);
	}

	/**
	 * Removes all tags from the given code.
	 * @param code code
	 * @return clean code
	 */
	public static String removeAllTags(String code) {
		return code.replaceAll("<[^>]*>", "");
	}

	/**
	 * Remove all occurrences of the given tag in the code.
	 * @param tag HTML-tag like b, i, div
	 * @return cleaned code
	 */
	public String replaceTagContent(String tag) {
		return this.replaceTagContent(tag, "", this.code);
	}

	/**
	 * Remove all occurrences of the given tag in the code.
	 * @param tag HTML-tag like b, i, div
	 * @param param A parameter the tags must contain.
	 * @return cleaned code
	 */
	public String replaceTagContent(String tag, String param) {
		return this.replaceTagContent(tag, param, this.code);
	}

	/**
	 * Remove all occurrences of the given tag in the code.
	 * @param tag HTML-tag like b, i, div
	 * @param param A parameter the tags must contain.
	 * @param code The HTML-code which will be searched.
	 * @return cleaned code
	 */
	public String replaceTagContent(String tag, String param, String code) {
		String returnCode = code;
		List<String> tags = getTags(tag, param, code);
		for ( String r : tags )
			returnCode = returnCode.replaceAll(Pattern.quote(r), "");

		return returnCode;
	}

	/**
	 * Returns all links this site contains.
	 * @return list of all links
	 */
	public List<String> getLinkURLs() {
		return getLinkURLs("", this.code);
	}

	/**
	 * Returns the first link this site contains.
	 * @return first link
	 */
	public String getFirstLinkURL() {
		List<String> links = getLinkURLs("", this.code);

		if ( links.isEmpty() )
			return "";

		return links.get(0);
	}

	/**
	 * Returns all links this site contains.
	 * @param base add these URL to the found one, if it starts either with / or ?
	 * @return list of all links
	 */
	public List<String> getLinkURLs(String base) {
		return getLinkURLs(base, this.code);
	}

	/**
	 * Returns first link this site contains.
	 * @param base add these URL to the found one, if it starts either with / or ?
	 * @return first link
	 */
	public String getFirstLinkURL(String base) {
		List<String> links = getLinkURLs(base, this.code);

		if ( links.isEmpty() )
			return "";

		return links.get(0);
	}

	/**
	 * Returns all links in the given code.
	 * @param base add these URL to the found one, if it starts either with / or ?
	 * @param code the HTML-code which will be searched
	 * @return list of all links
	 */
	public static List<String> getLinkURLs(String base, String code) {
		List<String> a = new ArrayList<>();

		Pattern p = Pattern.compile("href=\"?.[^\" >]*(.*?)\"?");
		Matcher m = p.matcher(code);

		while ( m.find() ) {
			//a.add(this.code.substring(m.start() + 6, m.end()-1).startsWith("?") || this.code.substring(m.start() + 6, m.end()-1).startsWith("/") ? base + this.code.substring(m.start() + 6, m.end()-1) : this.code.substring(m.start() + 6, m.end()-1));
			String s = code.substring(m.start() + (m.group().startsWith("href=\"") ? 6 : 5), m.end() - (m.group().endsWith("\"") ? 1 : 0));
			if ( s.contains("javascript") || s.equals("#") )
				continue;
			else if ( s.startsWith("?") || s.startsWith("/") )
				s = base + s.replaceAll(" ", "");

			a.add(s);
		}

		return a;
	}

	/**
	 * Returns first link in the given code.
	 * @param base add these URL to the found one, if it starts either with / or ?
	 * @param code the HTML-code which will be searched
	 * @return first link
	 */
	public static String getFirstLinkURL(String base, String code) {
		List<String> links = getLinkURLs(base, code);

		if ( links.isEmpty() )
			return "";

		return links.get(0);
	}

	/**
	 * Returns all links in the given code.
	 * @param base add these URL to the found one, if it starts either with / or ?
	 * @param codes multiple HTML-codes which will be searched
	 * @return list of all links
	 */
	public static Collection<String> getLinkURLs(String base, List<String> codes) {
		Collection<String> links = new ArrayList<>();
		for ( String code : codes )
			links.addAll(getLinkURLs(base, code));

		return links;
	}

	/**
	 * Returns all links and link texts in the given code.
	 * @param base add these URL to the found one, if it starts either with / or ?
	 * @param code the HTML-code which will be searched
	 * @return list of all links and link texts
	 */
	public static Collection<String[]> getLinkURLsAndTexts(String base, String code) {
		Collection<String[]> links = new ArrayList<>();

		Matcher matcher = Pattern.compile("<a[^>]*href=\"([^\"]+)\"[^>]*>(.+)</a>").matcher(code);

		while ( matcher.find() ) {
			String[] s = {matcher.group(1), matcher.group(2)};

			if ( s[0].contains("javascript") || s[0].equals("#") )
				continue;
			else if ( s[0].startsWith("?") || s[0].startsWith("/") )
				s[0] = base + s[0].replaceAll(" ", "");

			links.add(s);
		}

		return links;
	}

	/**
	 * Returns all links and link texts in the given code.
	 * @param base add these URL to the found one, if it starts either with / or ?
	 * @param codes multiple HTML-codes which will be searched
	 * @return list of all links and link texts
	 */
	public static Collection<String[]> getLinkURLsAndTexts(String base, Collection<String> codes) {
		Collection<String[]> links = new ArrayList<>();

		for ( String code : codes )
			links.addAll(getLinkURLsAndTexts(base, code));

		return links;
	}

	/**
	 * Returns the base URL of the given URL.
	 * @param url URL
	 * @return base URL or an empty string
	 */
	public static String getBaseURL(String url) {
		Pattern p = Pattern.compile("(^https?://(www\\.)?[^/]+\\.[^/\\.]+)/?.*$");
		Matcher m = p.matcher(url);
		if ( m.find() )
			return m.group(1);
		else
			return "";
	}

	/**
	 * Check if the given URL is a base URL.
	 * @param url URL
	 * @return <code>true</code> if given URL is a base URL
	 */
	public static boolean isBaseURL(String url) {
		return Pattern.compile("^https?://(www\\.)?[^/]+\\.[^/\\.]+/?$").matcher(url).find();
	}

	/**
	 * Replaces all HTML special characters in the page code with the original character.
	 * @return replace String
	 */
	public String decode() {
		return decode(this.code);
	}

	/**
	 * Replaces all HTML special characters in the given String with the original character.
	 * @param toDecode List of String with HTML characters to replace
	 * @return replace List of String
	 */
	public static List<String> decode(List<String> toDecode) {
		List<String> decoded = new ArrayList<>();

		for ( String s : toDecode )
			decoded.add(decode(s));

		return decoded;
	}

	/**
	 * Replaces all HTML special characters in the given String with the original character.
	 * @param toDecode String with HTML characters to replace
	 * @return replace String
	 */
	public static String decode(String toDecode) {
		toDecode = toDecode.replaceAll("&quot;", "\"");
		toDecode = toDecode.replaceAll("&#34;", "\"");
		toDecode = toDecode.replaceAll("&amp;", "&");
		toDecode = toDecode.replaceAll("&#38;", "&");
		toDecode = toDecode.replaceAll("&#39;", "'");
		toDecode = toDecode.replaceAll("&#039;", "'");
		toDecode = toDecode.replaceAll("&lt;", "<");
		toDecode = toDecode.replaceAll("&#60;", "<");
		toDecode = toDecode.replaceAll("&gt;", ">");
		toDecode = toDecode.replaceAll("&#62;", ">");
		toDecode = toDecode.replaceAll("&nbsp;", " ");
		toDecode = toDecode.replaceAll("&#160;", " ");
		toDecode = toDecode.replaceAll("&#xa0;", " ");
		toDecode = toDecode.replaceAll("&iexcl;", "¡");
		toDecode = toDecode.replaceAll("&#161;", "¡");
		toDecode = toDecode.replaceAll("&cent;", "¢");
		toDecode = toDecode.replaceAll("&#162;", "¢");
		toDecode = toDecode.replaceAll("&pound;", "£");
		toDecode = toDecode.replaceAll("&#163;", "£");
		toDecode = toDecode.replaceAll("&curren;", "¤");
		toDecode = toDecode.replaceAll("&#164;", "¤");
		toDecode = toDecode.replaceAll("&yen;", "¥");
		toDecode = toDecode.replaceAll("&#165;", "¥");
		toDecode = toDecode.replaceAll("&brvbar;", "¦");
		toDecode = toDecode.replaceAll("&#166;", "¦");
		toDecode = toDecode.replaceAll("&sect;", "§");
		toDecode = toDecode.replaceAll("&#167;", "§");
		toDecode = toDecode.replaceAll("&uml;", "¨");
		toDecode = toDecode.replaceAll("&#168;", "¨");
		toDecode = toDecode.replaceAll("&copy;", "©");
		toDecode = toDecode.replaceAll("&#169;", "©");
		toDecode = toDecode.replaceAll("&ordf;", "ª");
		toDecode = toDecode.replaceAll("&#170;", "ª");
		toDecode = toDecode.replaceAll("&laquo;", "«");
		toDecode = toDecode.replaceAll("&#171;", "«");
		toDecode = toDecode.replaceAll("&not;", "¬");
		toDecode = toDecode.replaceAll("&#172;", "¬");
		toDecode = toDecode.replaceAll("&shy;", "­");
		toDecode = toDecode.replaceAll("&#173;", "­");
		toDecode = toDecode.replaceAll("&reg;", "®");
		toDecode = toDecode.replaceAll("&#174;", "®");
		toDecode = toDecode.replaceAll("&macr;", "¯");
		toDecode = toDecode.replaceAll("&#175;", "¯");
		toDecode = toDecode.replaceAll("&deg;", "°");
		toDecode = toDecode.replaceAll("&#176;", "°");
		toDecode = toDecode.replaceAll("&plusmn;", "±");
		toDecode = toDecode.replaceAll("&#177;", "±");
		toDecode = toDecode.replaceAll("&sup2;", "²");
		toDecode = toDecode.replaceAll("&#178;", "²");
		toDecode = toDecode.replaceAll("&sup3;", "³");
		toDecode = toDecode.replaceAll("&#179;", "³");
		toDecode = toDecode.replaceAll("&acute;", "´");
		toDecode = toDecode.replaceAll("&#180;", "´");
		toDecode = toDecode.replaceAll("&micro;", "µ");
		toDecode = toDecode.replaceAll("&#181;", "µ");
		toDecode = toDecode.replaceAll("&para;", "¶");
		toDecode = toDecode.replaceAll("&#182;", "¶");
		toDecode = toDecode.replaceAll("&middot;", "·");
		toDecode = toDecode.replaceAll("&#183;", "·");
		toDecode = toDecode.replaceAll("&cedil;", "¸");
		toDecode = toDecode.replaceAll("&#184;", "¸");
		toDecode = toDecode.replaceAll("&sup1;", "¹");
		toDecode = toDecode.replaceAll("&#185;", "¹");
		toDecode = toDecode.replaceAll("&ordm;", "º");
		toDecode = toDecode.replaceAll("&#186;", "º");
		toDecode = toDecode.replaceAll("&raquo;", "»");
		toDecode = toDecode.replaceAll("&#187;", "»");
		toDecode = toDecode.replaceAll("&frac14;", "¼");
		toDecode = toDecode.replaceAll("&#188;", "¼");
		toDecode = toDecode.replaceAll("&frac12;", "½");
		toDecode = toDecode.replaceAll("&#189;", "½");
		toDecode = toDecode.replaceAll("&frac34;", "¾");
		toDecode = toDecode.replaceAll("&#190;", "¾");
		toDecode = toDecode.replaceAll("&iquest;", "¿");
		toDecode = toDecode.replaceAll("&#191;", "¿");
		toDecode = toDecode.replaceAll("&Agrave;", "À");
		toDecode = toDecode.replaceAll("&#192;", "À");
		toDecode = toDecode.replaceAll("&Aacute;", "Á");
		toDecode = toDecode.replaceAll("&#193;", "Á");
		toDecode = toDecode.replaceAll("&Acirc;", "Â");
		toDecode = toDecode.replaceAll("&#194;", "Â");
		toDecode = toDecode.replaceAll("&Atilde;", "Ã");
		toDecode = toDecode.replaceAll("&#195;", "Ã");
		toDecode = toDecode.replaceAll("&Auml;", "Ä");
		toDecode = toDecode.replaceAll("&#196;", "Ä");
		toDecode = toDecode.replaceAll("&Aring;", "Å");
		toDecode = toDecode.replaceAll("&#197;", "Å");
		toDecode = toDecode.replaceAll("&AElig;", "Æ");
		toDecode = toDecode.replaceAll("&#198;", "Æ");
		toDecode = toDecode.replaceAll("&Ccedil;", "Ç");
		toDecode = toDecode.replaceAll("&#199;", "Ç");
		toDecode = toDecode.replaceAll("&Egrave;", "È");
		toDecode = toDecode.replaceAll("&#200;", "È");
		toDecode = toDecode.replaceAll("&Eacute;", "É");
		toDecode = toDecode.replaceAll("&#201;", "É");
		toDecode = toDecode.replaceAll("&Ecirc;", "Ê");
		toDecode = toDecode.replaceAll("&#202;", "Ê");
		toDecode = toDecode.replaceAll("&Euml;", "Ë");
		toDecode = toDecode.replaceAll("&#203;", "Ë");
		toDecode = toDecode.replaceAll("&Igrave;", "Ì");
		toDecode = toDecode.replaceAll("&#204;", "Ì");
		toDecode = toDecode.replaceAll("&Iacute;", "Í");
		toDecode = toDecode.replaceAll("&#205;", "Í");
		toDecode = toDecode.replaceAll("&Icirc;", "Î");
		toDecode = toDecode.replaceAll("&#206;", "Î");
		toDecode = toDecode.replaceAll("&Iuml;", "Ï");
		toDecode = toDecode.replaceAll("&#207;", "Ï");
		toDecode = toDecode.replaceAll("&ETH;", "Ð");
		toDecode = toDecode.replaceAll("&#208;", "Ð");
		toDecode = toDecode.replaceAll("&Ntilde;", "Ñ");
		toDecode = toDecode.replaceAll("&#209;", "Ñ");
		toDecode = toDecode.replaceAll("&Ograve;", "Ò");
		toDecode = toDecode.replaceAll("&#210;", "Ò");
		toDecode = toDecode.replaceAll("&Oacute;", "Ó");
		toDecode = toDecode.replaceAll("&#211;", "Ó");
		toDecode = toDecode.replaceAll("&Ocirc;", "Ô");
		toDecode = toDecode.replaceAll("&#212;", "Ô");
		toDecode = toDecode.replaceAll("&Otilde;", "Õ");
		toDecode = toDecode.replaceAll("&#213;", "Õ");
		toDecode = toDecode.replaceAll("&Ouml;", "Ö");
		toDecode = toDecode.replaceAll("&#214;", "Ö");
		toDecode = toDecode.replaceAll("&times;", "×");
		toDecode = toDecode.replaceAll("&#215;", "×");
		toDecode = toDecode.replaceAll("&Oslash;", "Ø");
		toDecode = toDecode.replaceAll("&#216;", "Ø");
		toDecode = toDecode.replaceAll("&Ugrave;", "Ù");
		toDecode = toDecode.replaceAll("&#217;", "Ù");
		toDecode = toDecode.replaceAll("&Uacute;", "Ú");
		toDecode = toDecode.replaceAll("&#218;", "Ú");
		toDecode = toDecode.replaceAll("&Ucirc;", "Û");
		toDecode = toDecode.replaceAll("&#219;", "Û");
		toDecode = toDecode.replaceAll("&Uuml;", "Ü");
		toDecode = toDecode.replaceAll("&#220;", "Ü");
		toDecode = toDecode.replaceAll("&Yacute;", "Ý");
		toDecode = toDecode.replaceAll("&#221;", "Ý");
		toDecode = toDecode.replaceAll("&THORN;", "Þ");
		toDecode = toDecode.replaceAll("&#222;", "Þ");
		toDecode = toDecode.replaceAll("&szlig;", "ß");
		toDecode = toDecode.replaceAll("&#223;", "ß");
		toDecode = toDecode.replaceAll("&agrave;", "à");
		toDecode = toDecode.replaceAll("&#224;", "à");
		toDecode = toDecode.replaceAll("&aacute;", "á");
		toDecode = toDecode.replaceAll("&#225;", "á");
		toDecode = toDecode.replaceAll("&acirc;", "â");
		toDecode = toDecode.replaceAll("&#226;", "â");
		toDecode = toDecode.replaceAll("&atilde;", "ã");
		toDecode = toDecode.replaceAll("&#227;", "ã");
		toDecode = toDecode.replaceAll("&auml;", "ä");
		toDecode = toDecode.replaceAll("&#228;", "ä");
		toDecode = toDecode.replaceAll("&aring;", "å");
		toDecode = toDecode.replaceAll("&#229;", "å");
		toDecode = toDecode.replaceAll("&aelig;", "æ");
		toDecode = toDecode.replaceAll("&#230;", "æ");
		toDecode = toDecode.replaceAll("&ccedil;", "ç");
		toDecode = toDecode.replaceAll("&#231;", "ç");
		toDecode = toDecode.replaceAll("&egrave;", "è");
		toDecode = toDecode.replaceAll("&#232;", "è");
		toDecode = toDecode.replaceAll("&eacute;", "é");
		toDecode = toDecode.replaceAll("&#233;", "é");
		toDecode = toDecode.replaceAll("&ecirc;", "ê");
		toDecode = toDecode.replaceAll("&#234;", "ê");
		toDecode = toDecode.replaceAll("&euml;", "ë");
		toDecode = toDecode.replaceAll("&#235;", "ë");
		toDecode = toDecode.replaceAll("&igrave;", "ì");
		toDecode = toDecode.replaceAll("&#236;", "ì");
		toDecode = toDecode.replaceAll("&iacute;", "í");
		toDecode = toDecode.replaceAll("&#237;", "í");
		toDecode = toDecode.replaceAll("&icirc;", "î");
		toDecode = toDecode.replaceAll("&#238;", "î");
		toDecode = toDecode.replaceAll("&iuml;", "ï");
		toDecode = toDecode.replaceAll("&#239;", "ï");
		toDecode = toDecode.replaceAll("&eth;", "ð");
		toDecode = toDecode.replaceAll("&#240;", "ð");
		toDecode = toDecode.replaceAll("&ntilde;", "ñ");
		toDecode = toDecode.replaceAll("&#241;", "ñ");
		toDecode = toDecode.replaceAll("&ograve;", "ò");
		toDecode = toDecode.replaceAll("&#242;", "ò");
		toDecode = toDecode.replaceAll("&oacute;", "ó");
		toDecode = toDecode.replaceAll("&#243;", "ó");
		toDecode = toDecode.replaceAll("&ocirc;", "ô");
		toDecode = toDecode.replaceAll("&#244;", "ô");
		toDecode = toDecode.replaceAll("&otilde;", "õ");
		toDecode = toDecode.replaceAll("&#245;", "õ");
		toDecode = toDecode.replaceAll("&ouml;", "ö");
		toDecode = toDecode.replaceAll("&#246;", "ö");
		toDecode = toDecode.replaceAll("&divide;", "÷");
		toDecode = toDecode.replaceAll("&#247;", "÷");
		toDecode = toDecode.replaceAll("&oslash;", "ø");
		toDecode = toDecode.replaceAll("&#248;", "ø");
		toDecode = toDecode.replaceAll("&ugrave;", "ù");
		toDecode = toDecode.replaceAll("&#249;", "ù");
		toDecode = toDecode.replaceAll("&uacute;", "ú");
		toDecode = toDecode.replaceAll("&#250;", "ú");
		toDecode = toDecode.replaceAll("&ucirc;", "û");
		toDecode = toDecode.replaceAll("&#251;", "û");
		toDecode = toDecode.replaceAll("&uuml;", "ü");
		toDecode = toDecode.replaceAll("&#252;", "ü");
		toDecode = toDecode.replaceAll("&#xfc;", "ü");
		toDecode = toDecode.replaceAll("&yacute;", "ý");
		toDecode = toDecode.replaceAll("&#253;", "ý");
		toDecode = toDecode.replaceAll("&thorn;", "þ");
		toDecode = toDecode.replaceAll("&#254;", "þ");
		toDecode = toDecode.replaceAll("&yuml;", "ÿ");
		toDecode = toDecode.replaceAll("&#255;", "ÿ");
		toDecode = toDecode.replaceAll("&Alpha;", "Α");
		toDecode = toDecode.replaceAll("&#913;", "Α");
		toDecode = toDecode.replaceAll("&alpha;", "α");
		toDecode = toDecode.replaceAll("&#945;", "α");
		toDecode = toDecode.replaceAll("&Beta;", "Β");
		toDecode = toDecode.replaceAll("&#914;", "Β");
		toDecode = toDecode.replaceAll("&beta;", "β");
		toDecode = toDecode.replaceAll("&#946;", "β");
		toDecode = toDecode.replaceAll("&Gamma;", "Γ");
		toDecode = toDecode.replaceAll("&#915;", "Γ");
		toDecode = toDecode.replaceAll("&gamma;", "γ");
		toDecode = toDecode.replaceAll("&#947;", "γ");
		toDecode = toDecode.replaceAll("&Delta;", "Δ");
		toDecode = toDecode.replaceAll("&#916;", "Δ");
		toDecode = toDecode.replaceAll("&delta;", "δ");
		toDecode = toDecode.replaceAll("&#948;", "δ");
		toDecode = toDecode.replaceAll("&Epsilon;", "Ε");
		toDecode = toDecode.replaceAll("&#917;", "Ε");
		toDecode = toDecode.replaceAll("&epsilon;", "ε");
		toDecode = toDecode.replaceAll("&#949;", "ε");
		toDecode = toDecode.replaceAll("&Zeta;", "Ζ");
		toDecode = toDecode.replaceAll("&#918;", "Ζ");
		toDecode = toDecode.replaceAll("&zeta;", "ζ");
		toDecode = toDecode.replaceAll("&#950;", "ζ");
		toDecode = toDecode.replaceAll("&Eta;", "Η");
		toDecode = toDecode.replaceAll("&#919;", "Η");
		toDecode = toDecode.replaceAll("&eta;", "η");
		toDecode = toDecode.replaceAll("&#951;", "η");
		toDecode = toDecode.replaceAll("&Theta;", "Θ");
		toDecode = toDecode.replaceAll("&#920;", "Θ");
		toDecode = toDecode.replaceAll("&theta;", "θ");
		toDecode = toDecode.replaceAll("&#952;", "θ");
		toDecode = toDecode.replaceAll("&Iota;", "Ι");
		toDecode = toDecode.replaceAll("&#921;", "Ι");
		toDecode = toDecode.replaceAll("&iota;", "ι");
		toDecode = toDecode.replaceAll("&#953;", "ι");
		toDecode = toDecode.replaceAll("&Kappa;", "Κ");
		toDecode = toDecode.replaceAll("&#922;", "Κ");
		toDecode = toDecode.replaceAll("&kappa;", "κ");
		toDecode = toDecode.replaceAll("&#954;", "κ");
		toDecode = toDecode.replaceAll("&Lambda;", "Λ");
		toDecode = toDecode.replaceAll("&#923;", "Λ");
		toDecode = toDecode.replaceAll("&lambda;", "λ");
		toDecode = toDecode.replaceAll("&#955;", "λ");
		toDecode = toDecode.replaceAll("&Mu;", "Μ");
		toDecode = toDecode.replaceAll("&#924;", "Μ");
		toDecode = toDecode.replaceAll("&mu;", "μ");
		toDecode = toDecode.replaceAll("&#956;", "μ");
		toDecode = toDecode.replaceAll("&Nu;", "Ν");
		toDecode = toDecode.replaceAll("&#925;", "Ν");
		toDecode = toDecode.replaceAll("&nu;", "ν");
		toDecode = toDecode.replaceAll("&#957;", "ν");
		toDecode = toDecode.replaceAll("&Xi;", "Ξ");
		toDecode = toDecode.replaceAll("&#926;", "Ξ");
		toDecode = toDecode.replaceAll("&xi;", "ξ");
		toDecode = toDecode.replaceAll("&#958;", "ξ");
		toDecode = toDecode.replaceAll("&Omicron;", "Ο");
		toDecode = toDecode.replaceAll("&#927;", "Ο");
		toDecode = toDecode.replaceAll("&omicron;", "ο");
		toDecode = toDecode.replaceAll("&#959;", "ο");
		toDecode = toDecode.replaceAll("&Pi;", "Π");
		toDecode = toDecode.replaceAll("&#928;", "Π");
		toDecode = toDecode.replaceAll("&pi;", "π");
		toDecode = toDecode.replaceAll("&#960;", "π");
		toDecode = toDecode.replaceAll("&Rho;", "Ρ");
		toDecode = toDecode.replaceAll("&#929;", "Ρ");
		toDecode = toDecode.replaceAll("&rho;", "ρ");
		toDecode = toDecode.replaceAll("&#961;", "ρ");
		toDecode = toDecode.replaceAll("&Sigma;", "Σ");
		toDecode = toDecode.replaceAll("&#931;", "Σ");
		toDecode = toDecode.replaceAll("&sigmaf;", "ς");
		toDecode = toDecode.replaceAll("&#962;", "ς");
		toDecode = toDecode.replaceAll("&sigma;", "σ");
		toDecode = toDecode.replaceAll("&#963;", "σ");
		toDecode = toDecode.replaceAll("&Tau;", "Τ");
		toDecode = toDecode.replaceAll("&#932;", "Τ");
		toDecode = toDecode.replaceAll("&tau;", "τ");
		toDecode = toDecode.replaceAll("&#964;", "τ");
		toDecode = toDecode.replaceAll("&Upsilon;", "Υ");
		toDecode = toDecode.replaceAll("&#933;", "Υ");
		toDecode = toDecode.replaceAll("&upsilon;", "υ");
		toDecode = toDecode.replaceAll("&#965;", "υ");
		toDecode = toDecode.replaceAll("&Phi;", "Φ");
		toDecode = toDecode.replaceAll("&#934;", "Φ");
		toDecode = toDecode.replaceAll("&phi;", "φ");
		toDecode = toDecode.replaceAll("&#966;", "φ");
		toDecode = toDecode.replaceAll("&Chi;", "Χ");
		toDecode = toDecode.replaceAll("&#935;", "Χ");
		toDecode = toDecode.replaceAll("&chi;", "χ");
		toDecode = toDecode.replaceAll("&#967;", "χ");
		toDecode = toDecode.replaceAll("&Psi;", "Ψ");
		toDecode = toDecode.replaceAll("&#936;", "Ψ");
		toDecode = toDecode.replaceAll("&psi;", "ψ");
		toDecode = toDecode.replaceAll("&#968;", "ψ");
		toDecode = toDecode.replaceAll("&Omega;", "Ω");
		toDecode = toDecode.replaceAll("&#937;", "Ω");
		toDecode = toDecode.replaceAll("&omega;", "ω");
		toDecode = toDecode.replaceAll("&#969;", "ω");
		toDecode = toDecode.replaceAll("&thetasym;", "ϑ");
		toDecode = toDecode.replaceAll("&#977;", "ϑ");
		toDecode = toDecode.replaceAll("&upsih;", "ϒ");
		toDecode = toDecode.replaceAll("&#978;", "ϒ");
		toDecode = toDecode.replaceAll("&piv;", "ϖ");
		toDecode = toDecode.replaceAll("&#982;", "ϖ");
		toDecode = toDecode.replaceAll("&forall;", "∀");
		toDecode = toDecode.replaceAll("&#8704;", "∀");
		toDecode = toDecode.replaceAll("&part;", "∂");
		toDecode = toDecode.replaceAll("&#8706;", "∂");
		toDecode = toDecode.replaceAll("&exist;", "∃");
		toDecode = toDecode.replaceAll("&#8707;", "∃");
		toDecode = toDecode.replaceAll("&empty;", "∅");
		toDecode = toDecode.replaceAll("&#8709;", "∅");
		toDecode = toDecode.replaceAll("&nabla;", "∇");
		toDecode = toDecode.replaceAll("&#8711;", "∇");
		toDecode = toDecode.replaceAll("&isin;", "∈");
		toDecode = toDecode.replaceAll("&#8712;", "∈");
		toDecode = toDecode.replaceAll("&notin;", "∉");
		toDecode = toDecode.replaceAll("&#8713;", "∉");
		toDecode = toDecode.replaceAll("&ni;", "∋");
		toDecode = toDecode.replaceAll("&#8715;", "∋");
		toDecode = toDecode.replaceAll("&prod;", "∏");
		toDecode = toDecode.replaceAll("&#8719;", "∏");
		toDecode = toDecode.replaceAll("&sum;", "∑");
		toDecode = toDecode.replaceAll("&#8721;", "∑");
		toDecode = toDecode.replaceAll("&minus;", "−");
		toDecode = toDecode.replaceAll("&#8722;", "−");
		toDecode = toDecode.replaceAll("&lowast;", "∗");
		toDecode = toDecode.replaceAll("&#8727;", "∗");
		toDecode = toDecode.replaceAll("&radic;", "√");
		toDecode = toDecode.replaceAll("&#8730;", "√");
		toDecode = toDecode.replaceAll("&prop;", "∝");
		toDecode = toDecode.replaceAll("&#8733;", "∝");
		toDecode = toDecode.replaceAll("&infin;", "∞");
		toDecode = toDecode.replaceAll("&#8734;", "∞");
		toDecode = toDecode.replaceAll("&ang;", "∠");
		toDecode = toDecode.replaceAll("&#8736;", "∠");
		toDecode = toDecode.replaceAll("&and;", "∧");
		toDecode = toDecode.replaceAll("&#8743;", "∧");
		toDecode = toDecode.replaceAll("&or;", "∨");
		toDecode = toDecode.replaceAll("&#8744;", "∨");
		toDecode = toDecode.replaceAll("&cap;", "∩");
		toDecode = toDecode.replaceAll("&#8745;", "∩");
		toDecode = toDecode.replaceAll("&cup;", "∪");
		toDecode = toDecode.replaceAll("&#8746;", "∪");
		toDecode = toDecode.replaceAll("&int;", "∫");
		toDecode = toDecode.replaceAll("&#8747;", "∫");
		toDecode = toDecode.replaceAll("&there4;", "∴");
		toDecode = toDecode.replaceAll("&#8756;", "∴");
		toDecode = toDecode.replaceAll("&sim;", "∼");
		toDecode = toDecode.replaceAll("&#8764;", "∼");
		toDecode = toDecode.replaceAll("&cong;", "≅");
		toDecode = toDecode.replaceAll("&#8773;", "≅");
		toDecode = toDecode.replaceAll("&asymp;", "≈");
		toDecode = toDecode.replaceAll("&#8776;", "≈");
		toDecode = toDecode.replaceAll("&ne;", "≠");
		toDecode = toDecode.replaceAll("&#8800;", "≠");
		toDecode = toDecode.replaceAll("&equiv;", "≡");
		toDecode = toDecode.replaceAll("&#8801;", "≡");
		toDecode = toDecode.replaceAll("&le;", "≤");
		toDecode = toDecode.replaceAll("&#8804;", "≤");
		toDecode = toDecode.replaceAll("&ge;", "≥");
		toDecode = toDecode.replaceAll("&#8805;", "≥");
		toDecode = toDecode.replaceAll("&sub;", "⊂");
		toDecode = toDecode.replaceAll("&#8834;", "⊂");
		toDecode = toDecode.replaceAll("&sup;", "⊃");
		toDecode = toDecode.replaceAll("&#8835;", "⊃");
		toDecode = toDecode.replaceAll("&nsub;", "⊄");
		toDecode = toDecode.replaceAll("&#8836;", "⊄");
		toDecode = toDecode.replaceAll("&sube;", "⊆");
		toDecode = toDecode.replaceAll("&#8838;", "⊆");
		toDecode = toDecode.replaceAll("&supe;", "⊇");
		toDecode = toDecode.replaceAll("&#8839;", "⊇");
		toDecode = toDecode.replaceAll("&oplus;", "⊕");
		toDecode = toDecode.replaceAll("&#8853;", "⊕");
		toDecode = toDecode.replaceAll("&otimes;", "⊗");
		toDecode = toDecode.replaceAll("&#8855;", "⊗");
		toDecode = toDecode.replaceAll("&perp;", "⊥");
		toDecode = toDecode.replaceAll("&#8869;", "⊥");
		toDecode = toDecode.replaceAll("&sdot;", "⋅");
		toDecode = toDecode.replaceAll("&#8901;", "⋅");
		toDecode = toDecode.replaceAll("&loz;", "◊");
		toDecode = toDecode.replaceAll("&#9674;", "◊");
		toDecode = toDecode.replaceAll("&lceil;", "⌈");
		toDecode = toDecode.replaceAll("&#8968;", "⌈");
		toDecode = toDecode.replaceAll("&rceil;", "⌉");
		toDecode = toDecode.replaceAll("&#8969;", "⌉");
		toDecode = toDecode.replaceAll("&lfloor;", "⌊");
		toDecode = toDecode.replaceAll("&#8970;", "⌊");
		toDecode = toDecode.replaceAll("&rfloor;", "⌋");
		toDecode = toDecode.replaceAll("&#8971;", "⌋");
		toDecode = toDecode.replaceAll("&lang;", "⟨");
		toDecode = toDecode.replaceAll("&#9001;", "⟨");
		toDecode = toDecode.replaceAll("&rang;", "⟩");
		toDecode = toDecode.replaceAll("&#9002;", "⟩");
		toDecode = toDecode.replaceAll("&larr;", "←");
		toDecode = toDecode.replaceAll("&#8592;", "←");
		toDecode = toDecode.replaceAll("&uarr;", "↑");
		toDecode = toDecode.replaceAll("&#8593;", "↑");
		toDecode = toDecode.replaceAll("&rarr;", "→");
		toDecode = toDecode.replaceAll("&#8594;", "→");
		toDecode = toDecode.replaceAll("&darr;", "↓");
		toDecode = toDecode.replaceAll("&#8595;", "↓");
		toDecode = toDecode.replaceAll("&harr;", "↔");
		toDecode = toDecode.replaceAll("&#8596;", "↔");
		toDecode = toDecode.replaceAll("&crarr;", "↵");
		toDecode = toDecode.replaceAll("&#8629;", "↵");
		toDecode = toDecode.replaceAll("&lArr;", "⇐");
		toDecode = toDecode.replaceAll("&#8656;", "⇐");
		toDecode = toDecode.replaceAll("&uArr;", "⇑");
		toDecode = toDecode.replaceAll("&#8657;", "⇑");
		toDecode = toDecode.replaceAll("&rArr;", "⇒");
		toDecode = toDecode.replaceAll("&#8658;", "⇒");
		toDecode = toDecode.replaceAll("&dArr;", "⇓");
		toDecode = toDecode.replaceAll("&#8659;", "⇓");
		toDecode = toDecode.replaceAll("&hArr;", "⇔");
		toDecode = toDecode.replaceAll("&#8660;", "⇔");
		toDecode = toDecode.replaceAll("&lsquo;", "‘");
		toDecode = toDecode.replaceAll("&rsquo;", "’");
		toDecode = toDecode.replaceAll("&#8217;", "’");
		toDecode = toDecode.replaceAll("&#8220;", "“");
		toDecode = toDecode.replaceAll("&#8221;", "”");
		toDecode = toDecode.replaceAll("&#8211;", "–");
		toDecode = toDecode.replaceAll("&#8230;", "…");
		toDecode = toDecode.replaceAll("&#8212;", "—");
		toDecode = toDecode.replaceAll("&#8216;", "‘");
		toDecode = toDecode.replaceAll("&#8218;", "‚");
		toDecode = toDecode.replaceAll("&#8222;", "„");
		toDecode = toDecode.replaceAll("&#8224;", "†");
		toDecode = toDecode.replaceAll("&#8225;", "‡");
		toDecode = toDecode.replaceAll("&#8226;", "•");
		toDecode = toDecode.replaceAll("&#8240;", "‰");
		toDecode = toDecode.replaceAll("&#8364;", "€");
		toDecode = toDecode.replaceAll("&#8482;", "™");
		toDecode = toDecode.replaceAll("&mdash;", "—");
		toDecode = toDecode.replaceAll("&ldquo;", "“");
		toDecode = toDecode.replaceAll("&rdquo;", "”");
		toDecode = toDecode.replaceAll("&ndash;", "–");

		return toDecode;
	}
}