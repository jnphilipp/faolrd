/*
 * $Header: /usr/cvs/toolbox/src/de/uni_leipzig/asv/toolbox/jLanI/kernel/kernel.java,v 1.4 2007/08/10 15:49:49 lsteiner Exp $
 * 
 * Created on Apr 13, 2005
 * by knorke
 * 
 * package jLanI
 * for jLanI project
 *
 */
package de.uni_leipzig.asv.toolbox.jLanI.kernel;

import java.util.Set;

/**
 * @author knorke
 * @date Apr 13, 2005 2:20:43 PM
 */
public interface kernel {

	/**
	 * identify the language of a given sentence encapsulated in a request
	 * object
	 * 
	 * @param request
	 * @return
	 * @throws RequestException
	 *             with error message
	 * @throws DataSourceException
	 */
	public Response evaluate(Request request) throws RequestException,
			DataSourceException;

	/**
	 * keep the server running, unload unneeded datasources YOU DON'T NEED THIS
	 * FOR NOW!
	 * 
	 * @return
	 */
	public boolean upkeep();

	/**
	 * returns the set of available datasources
	 * 
	 * @return
	 */
	public Set getAvailableDatasources();

}
