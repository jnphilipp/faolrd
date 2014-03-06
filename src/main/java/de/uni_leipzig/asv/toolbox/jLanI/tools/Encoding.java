/*
 * Created on 18.05.2005
 *
 */
package de.uni_leipzig.asv.toolbox.jLanI.tools;

/**
 * @author micha
 * 
 */
public class Encoding {

	private String description = null;

	private String name = null;

	public Encoding(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return this.name + "," + this.description;
	}
}
