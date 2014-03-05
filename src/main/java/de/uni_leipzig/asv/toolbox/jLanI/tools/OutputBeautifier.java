/*
 * Created on 16.08.2005
 *
 */
package de.uni_leipzig.asv.toolbox.jLanI.tools;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;

import de.uni_leipzig.asv.toolbox.jLanI.kernel.Response;

/**
 * @author Michael Welt
 * 
 */
public class OutputBeautifier {
	public static String beautify(Response response) {
		// get Response
		Map results = response.getResult();
		System.err.println(ToStringTools.hashMapToString(results));
		// iterate over Keyset
		Iterator it = results.keySet().iterator();
		// if it's empty break
		if (!it.hasNext())
			return "empty";
		// top1 value is the first entry
		String top1 = it.next().toString();
		Double top1_count = (Double) results.get(top1);
		// if there are no more entries break
		if (!it.hasNext())
			return top1 + " " + "100%";
		// top2 value is the second entry
		String top2 = it.next().toString();
		Double top2_count = (Double) results.get(top1);
		// possibly the top2 value is almost greater than top1 so change
		if (top2_count.doubleValue() > top1_count.doubleValue()) {
			Double tmp = top1_count;
			top1_count = top2_count;
			top2_count = tmp;

			String tmp_ = top1;
			top1 = top2;
			top2 = tmp_;
		}
		// the counter to calculate the percentages later on
		double count = top1_count.doubleValue() + top2_count.doubleValue();
		// iterate over the rest
		while (it.hasNext()) {
			// current Language
			String currLan = it.next().toString();
			Double currLanCount = (Double) results.get(currLan);
			// if it's greater than top1 so change the top one and put
			// the last top1 on second place
			if (top1_count.doubleValue() < currLanCount.doubleValue()) {
				top2 = top1;
				top2_count = top1_count;
				top1 = currLan;
				top1_count = currLanCount;
			} else {
				// check if it's greater than the current top2 count
				if (top2_count.doubleValue() < currLanCount.doubleValue()) {
					top2 = currLan;
					top2_count = currLanCount;
				}
			}
			// count the sum
			count += currLanCount.doubleValue();
		}
		// calc percentages
		double top1_perc = (top1_count.doubleValue() / count) * (double) 100;
		double top2_perc = (top2_count.doubleValue() / count) * (double) 100;
		// format the doubles on 2 numbers behind the comma
		DecimalFormat df = new DecimalFormat("##0.00");
		return top1 + " " + df.format(top1_perc) + "\t" + top2 + " "
				+ df.format(top2_perc);

	}
}
