/*
 * $Header: /usr/cvs/toolbox/src/de/uni_leipzig/asv/toolbox/jLanI/kernel/RequestException.java,v 1.3 2007/08/10 15:49:49 lsteiner Exp $
 * 
 * Created on Apr 20, 2005
 * by knorke
 * 
 * package jLanI
 * for jLanI project
 *
 */
package de.uni_leipzig.asv.toolbox.jLanI.kernel;

/**
 * @author knorke
 */
public class RequestException extends Exception {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3762253049980335159L;

	RequestException(String string) {
		super(string);
	}
}
