package de.uni_leipzig.asv.toolbox.jLanI.kernel;

public class LangResult {
	private int MINWORDCOUNT;

	private double MINCOVERAGE;

	public String lang1;

	public String lang2;

	public int prob1;

	public int prob2;

	public int wordcount1;

	public double coverage1;

	public LangResult(String l1, int p1, String l2, int p2, double c, int wc,
			double mcv, int mco) {
		lang1 = l1;
		lang2 = l2;
		prob1 = p1;
		prob2 = p2;
		wordcount1 = wc;
		coverage1 = c;
		MINWORDCOUNT = mco;
		MINCOVERAGE = mcv;
		// System.out.println(l1+" "+l2+" "+p1+" "+p2+" "+coverage1+"
		// "+wordcount1);
	}

	public String toString() {
		return (isKnown()) ? lang1 + "\t" + prob1 + "\t" + lang2 + "\t" + prob2
				: "unknown";
	}

	public String toTextOutputString() {
		return (isKnown()) ? lang1 + ":" + prob1 + " " + lang2 + ":" + prob2
				: "unknown";
	}

	public boolean isKnown() {
		boolean retval = true;
		// only insert a language value if the value of the winnerkey
		// is at least twice as the value of the second language, exceeds
		// minwordcount, exceeds mincov
		if (prob1 < 2 * prob2) {
			retval = false;
		}
		if (wordcount1 < MINWORDCOUNT) {
			retval = false;
		}
		if (coverage1 < MINCOVERAGE) {
			retval = false;
		}

		return retval;
	}

	public String getLang1() {
		return (isKnown()) ? lang1 : "unknown";
	}

	public String getLang2() {
		return (isKnown()) ? lang2 : "unknown";
	}

	public int getProb1() {
		return (isKnown()) ? prob1 : 0;
	}

	public int getProb2() {
		return (isKnown()) ? prob2 : 0;
	}

}