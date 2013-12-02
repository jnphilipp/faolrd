package org.lcc.parser.json;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.lcc.parser.Parser;

/**
 *
 * @author jnphilipp
 * @version 0.0.1
 */
public class JSONParser implements Parser {
	/**
	 * JSON code
	 */
	private String json = "";
	/**
	 * JSONObject
	 */
	private JSONObject jsonObject;

	/**
	 * Default constructor.
	 */
	public JSONParser() {}

	/**
	 * Returns the JSON code.
	 * @return json
	 */
	public String getJSON() {
		return this.json;
	}

	/**
	 * Returns JSON code as JSONObject.
	 * @return JSONObject
	 */
	public JSONObject getJSONObject() {
		return this.jsonObject;
	}

	/**
	 * Builds a connection to the given page and retrieves the code.
	 * @param json URL to JSON file
	 * @throws Exception 
	 */
	@Override
	public void fetch(String json) throws Exception {
		URL url = new URL(json);
		HttpURLConnection con = (HttpURLConnection)url.openConnection();
		con.setRequestProperty("Accept-Charset", "UTF-8");
		con.connect();

		String header = con.getHeaderField("Content-Type");
		String charset = "utf-8";
		if ( header.contains("ISO-8859-15") )
			charset = "ISO-8859-15";
		else if ( header.contains("ISO-8859-1") )
			charset = "ISO-8859-1";

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

		this.json = text.toString().replace('\0', ' ');

		this.jsonObject = (JSONObject)new org.json.simple.parser.JSONParser().parse(this.json.toString());
	}

	/**
	 * Returns the JSONArray for the given key.
	 * @param key key
	 * @return JSONArray
	 */
	public JSONArray getJSONArray(String key) {
		return (JSONArray)jsonObject.get(key);
	}

	/**
	 * Returns the content for the given key.
	 * @param key key
	 * @return content
	 */
	public String getJSONContent(String key) {
		return this.jsonObject.get(key).toString();
	}

	/**
	 * Returns the given fields in the JSONArray.
	 * @param array JSONArray
	 * @param keys keys
	 * @return list of contents
	 */
	@SuppressWarnings("unchecked")
	public List<String[]> getJSONContents(JSONArray array, String... keys) {
		List<String[]> contents = new ArrayList<>();

		Iterator<JSONObject> iterator = array.iterator();
		while ( iterator.hasNext() ) {
			String[] content = new String[keys.length];
			JSONObject o = iterator.next();

			for ( int i = 0; i < keys.length; i++ )
				if ( keys[i].equals("raw") )
					content[i] = o.toString();
				else
					content[i] = (o.get(keys[i]) == null ? "" : o.get(keys[i]).toString());

			contents.add(content);
		}

		return contents;
	}
}