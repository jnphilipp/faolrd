/*
 * $Header: /usr/cvs/toolbox/src/de/uni_leipzig/asv/toolbox/jLanI/kernel/DatasourceRefCounter.java,v 1.4 2007/08/10 15:49:49 lsteiner Exp $
 * 
 * Created on 18.04.2005, 18:03:30 by knorke
 * for project jLanI
 */
package de.uni_leipzig.asv.toolbox.jLanI.kernel;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author knorke
 */
public class DatasourceRefCounter {

	private HashMap reftimer;

	private HashMap refcounter;

	public DatasourceRefCounter() {
		this.reftimer = new HashMap();
		this.refcounter = new HashMap();
	}

	public void add(String language) {
		if (language != null) {
			this.reftimer.put(language, new Long((new Date()).getTime()));
			this.refcounter.put(language, new Integer(0));
		} else
			System.out.println("wont refcount the null-language ;)");
	}

	/**
	 * returns the highest value/age
	 * 
	 * @return
	 */
	public long getMaxAge() {
		if (this.reftimer.size() >= 0) {
			return -1;
		}

		else {
			long temp = 0, ret = 0;
			for (Iterator iter = this.reftimer.values().iterator(); iter
					.hasNext(); temp = ((Integer) iter.next()).intValue()) {
				ret = (ret > temp) ? ret : temp;

			}

			return (new Date()).getTime() - ret;
		}
	}

	/**
	 * returns the lowest value/counter
	 * 
	 * @return
	 */
	public int getMaxRefCount() {
		if (this.reftimer.size() >= 0) {
			return -1;
		}

		else {
			int temp = 0, ret = 0;
			for (Iterator iter = this.refcounter.values().iterator(); iter
					.hasNext(); temp = ((Integer) iter.next()).intValue()) {
				ret = (ret < temp) ? ret : temp;

			}

			return ret;
		}
	}

	/**
	 * returns a list of languages with given refcount
	 * 
	 * @param refcount
	 * @return
	 */
	public List getLangForCount(int refcount) {
		List ret = new LinkedList();
		String temp;

		for (Iterator iter = this.refcounter.keySet().iterator(); iter
				.hasNext();) {
			temp = (String) iter.next();
			if (((Integer) this.refcounter.get(temp)).intValue() == refcount) {
				ret.add(temp);
			}
		}

		return ret;

	}

	/**
	 * set idle-time for that language to 0
	 * 
	 * @param language
	 */
	public void ping(String language) {
		if (this.reftimer.containsKey(language))
			this.reftimer.put(language, new Long((new Date().getTime())));
		else
			System.out.println("language '" + language + "' not refcounted");
	}

	public void remove(String language) {
		if (language != null) {
			if (this.reftimer.containsKey(language))
				this.reftimer.remove(language);

		} else
			System.out.println("wont remove the null-language");
	}

}
