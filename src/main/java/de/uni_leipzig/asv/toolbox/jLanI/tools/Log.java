/*
 * Created on 25.04.2005
 *
 */
package de.uni_leipzig.asv.toolbox.jLanI.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Michael Welt Simple Logger, which supports Debugmode, and 3 cases of
 *         Logmode.<br>
 *         case 1: only Stdin / Stderr case 2: only file logging (timestamp.log)
 *         case 3: case 1 and 2 together case 0: no log at all. default: case 3
 *         furthermore it supports bufferLimit, if the given limit exeeds the
 *         buffer is saved automatically.
 * 
 */
public class Log {

	public static final int DEFAULT_LOGMODE = 3;

	public static final boolean DEFAULT_DEBUG_MODE = true;

	public static final boolean DEFAULT_TIMESTAMP_MODE = true;

	public static final String DEFAULT_LOGPREFIX = "";

	private String logPrefix = DEFAULT_LOGPREFIX;

	private static Log singleton = null;

	private int logMode = DEFAULT_LOGMODE;

	private boolean debugMode = DEFAULT_DEBUG_MODE;

	private boolean timeStampMode = DEFAULT_TIMESTAMP_MODE;

	private long initTime = 0;

	private int count = 0;

	private StringBuffer logBuffer = null;

	private int bufferLimit = -1;

	private Log() {
		logBuffer = new StringBuffer();
		singleton = this;
		Date d = new Date();
		initTime = System.currentTimeMillis();
	}

	public static Log getInstance() {
		if (singleton == null)
			return new Log();
		else
			return singleton;
	}

	/**
	 * Is saved to a File timeStamp_count.log if count is zero nothing is
	 * appended. Count counts the saveOps.
	 * 
	 */
	public void saveLog() {
		try {
			File logFile = new File(this.logPrefix + this.initTime
					+ (count++ > 0 ? "_" + count : "") + ".log");
			BufferedWriter logWriter = null;
			if (logFile.exists())
				logWriter = new BufferedWriter(new FileWriter(logFile, true));
			else
				logWriter = new BufferedWriter(new FileWriter(logFile));
			logWriter.write(this.logBuffer.toString());
			logWriter.close();
			this.logBuffer = new StringBuffer();
		} catch (IOException e) {
			System.err.println("Error writing logfile!\n\t" + e.getMessage());
		}
	}

	private synchronized void write(String string, boolean error) {
		switch (this.logMode) {
		case 1: {
			if (error)
				System.err.print(string);
			else
				System.out.print(string);
			break;
		}
		case 2: {
			appendToLogBuffer(string);
			break;
		}
		case 3: {
			if (error)
				System.err.print(string);
			else
				System.out.print(string);
			appendToLogBuffer(string);
			break;
		}
		default: {
			break;
		}
		}
	}

	private void appendToLogBuffer(String string) {
		if (this.bufferLimit != -1
				&& this.logBuffer.toString().length() + string.length() > this.bufferLimit)
			saveLog();

		logBuffer.append(string);

	}

	public synchronized void log(String string) {
		String logString = (this.timeStampMode ? "(" + getTime() + ")" : "")
				+ " LOG : " + string + "\n";
		write(logString, false);
	}

	public synchronized void err(String string) {
		String logString = (this.timeStampMode ? "(" + getTime() + ")" : "")
				+ " ERR : " + string + "\n";
		write(logString, true);
	}

	public synchronized void debug(String string) {
		if (this.debugMode) {
			String logString = (this.timeStampMode ? "(" + getTime() + ")" : "")
					+ " DEBUG : " + string + "\n";
			write(logString, false);
		}
	}

	public void setDebug(boolean b) {
		this.debugMode = b;
	}

	public void setTimeStampMode(boolean b) {
		this.debugMode = b;
	}

	public void setLogMode(int i) {
		this.logMode = i;
	}

	public void setLogPrefix(String prefix) {
		this.logPrefix = prefix == null ? "" : prefix;
	}

	public void setBufferLimit(int limit) {
		this.bufferLimit = limit > 0 ? limit : this.bufferLimit;
	}

	private String getTime() {
		Date now;
		SimpleDateFormat formatter;
		formatter = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");// :SSS");
		now = new Date();
		return formatter.format(now);
	}

}
