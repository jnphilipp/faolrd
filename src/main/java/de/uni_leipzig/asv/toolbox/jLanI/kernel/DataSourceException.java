/*
 * $Header: /usr/cvs/toolbox/src/de/uni_leipzig/asv/toolbox/jLanI/kernel/DataSourceException.java,v 1.4 2007/08/10 15:49:49 lsteiner Exp $
 * 
 * Created on 15.04.2005, 10:42:16 by knorke
 * for project jLanI
 */
package de.uni_leipzig.asv.toolbox.jLanI.kernel;

/**
 * @author knorke
 */
public class DataSourceException extends Exception {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 664991027778548483L;

	private int errorcode;

	DataSourceException(String string) {
		super(string);
		this.errorcode = -1;

	}

	DataSourceException(String string, int errorcode) {
		super(string);
		this.errorcode = errorcode;

	}

	/**
	 * @return Returns the errorcode.
	 */
	public int getErrorcode() {
		return errorcode;
	}
}
