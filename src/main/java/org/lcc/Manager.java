package org.lcc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.lcc.utils.Helpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jnphilipp
 * @version 0.0.1
 */
public class Manager {
	/**
	 * default database configuration file
	 */
	private static final String DEFAULT_CONFIGURATION_FILE = Helpers.getUserDir() + "/config/config.properties";
	/**
	 * default logging configuration file
	 */
	private static final String DEFAULT_LOG4J_FILE = Helpers.getUserDir() + "/config/log4j.properties";
	/**
	 * logger
	 */
	private static Logger logger;
	/**
	 * manager
	 */
	private static Manager manager;
	/**
	 * properties
	 */
	private Properties properties;

	private Manager (String configurationFile, String log4jFile) throws FileNotFoundException, IOException {
		if ( !new File(configurationFile).exists() ) {
			System.err.println("The configuration file does not exists.");
			System.exit(1);
		}
		else if ( !new File(log4jFile).exists() ) {
			System.err.println("The log4j configuration file does not exists.");
			System.exit(1);
		}

		Properties prop = new Properties();
		prop.load(new FileInputStream(log4jFile));
		PropertyConfigurator.configure(prop);
		logger = LoggerFactory.getLogger(Manager.class);

		this.properties = new Properties();
		this.properties.load(new FileInputStream(configurationFile));
	}

	/**
	 * Returns an instance of this class.
	 * @return instance
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static synchronized Manager getInstance() throws FileNotFoundException, IOException {
		if ( manager == null )
			manager = new Manager(DEFAULT_CONFIGURATION_FILE, DEFAULT_LOG4J_FILE);

		return manager;
	}

	/**
	 * Returns an instance of this class.
	 * @param configurationFile configuration file
	 * @param log4jFile log4j configuration file
	 * @return instance
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static synchronized Manager getInstance(String configurationFile, String log4jFile) throws FileNotFoundException, IOException {
		if ( manager == null )
			manager = new Manager((configurationFile.isEmpty() ? DEFAULT_CONFIGURATION_FILE : configurationFile), (log4jFile.isEmpty() ? DEFAULT_LOG4J_FILE : log4jFile));

		return manager;
	}

	/**
	 * @return the manager
	 */
	public static synchronized Manager getManager() {
		return manager;
	}

	/**
	 * @return the properties
	 */
	public Properties getProperties() {
		return this.properties;
	}

	/**
	 * @param key key
	 * @return values of the given key
	 */
	public String getProperty(String key) {
		switch ( key ) {
			case "GoogleWebSearchJSONParser.sleeptime":
				return this.properties.getProperty(key, "0");
			default:
				return this.properties.getProperty(key, "");
		}
	}

	/**
	 * Returns the value of the given key as integer.
	 * @param key key
	 * @return integer value
	 */
	public int getIntegerProperty(String key) {
		return Integer.parseInt(this.getProperty(key));
	}

	/**
	 * Returns the value of the given key as long.
	 * @param key key
	 * @return long value
	 */
	public long getLongProperty(String key) {
		return Long.parseLong(this.getProperty(key));
	}

	/**
	 * Logs debug message.
	 * @param msg message
	 */
	public static void debug(final String msg) {
		if ( logger.isDebugEnabled() )
			logger.debug(msg);
	}

	/**
	 * Logs debug message.
	 * @param msg messages
	 */
	public static void debug(final String... msg) {
		if ( logger.isDebugEnabled() )
			logger.debug(Helpers.join(msg, "\n\t- "));
	}

	/**
	 * Logs debug message,
	 * @param clazz Class
	 * @param msg message
	 */
	public static void debug(Class<?> clazz, final String msg) {
		if ( LoggerFactory.getLogger(clazz).isDebugEnabled() )
			LoggerFactory.getLogger(clazz).debug(msg);
	}

	/**
	 * Logs debug message.
	 * @param clazz Class
	 * @param msg messages
	 */
	public static void debug(Class<?> clazz, final String... msg) {
		if ( LoggerFactory.getLogger(clazz).isDebugEnabled() )
			LoggerFactory.getLogger(clazz).debug(Helpers.join(msg, "\n\t- "));
	}

	/**
	 * Logs error message.
	 * @param msg message
	 */
	public static void error(final String msg) {
		if ( logger.isErrorEnabled() )
			logger.error(msg);
	}

	/**
	 * Logs error message.
	 * @param msg messages
	 */
	public static void error(final String... msg) {
		if ( logger.isErrorEnabled() )
			logger.error(Helpers.join(msg, "\n\t- "));
	}

	/**
	 * Logs error message.
	 * @param clazz Class
	 * @param msg message
	 */
	public static void error(Class<?> clazz, final String msg) {
		if ( LoggerFactory.getLogger(clazz).isErrorEnabled() )
			LoggerFactory.getLogger(clazz).error(msg);
	}

	/**
	 * Logs error message.
	 * @param clazz Class
	 * @param msg messages
	 */
	public static void error(Class<?> clazz, final String... msg) {
		if ( LoggerFactory.getLogger(clazz).isErrorEnabled() )
			LoggerFactory.getLogger(clazz).error(Helpers.join(msg, "\n\t- "));
	}

	/**
	 * Logs info message.
	 * @param msg message
	 */
	public static void info(final String msg) {
		if ( logger.isInfoEnabled() )
			logger.info(msg);
	}

	/**
	 * Logs info message.
	 * @param msg messages
	 */
	public static void info(final String... msg) {
		if ( logger.isInfoEnabled() )
			logger.info(Helpers.join(msg, "\n\t- "));
	}

	/**
	 * Logs info message.
	 * @param clazz Class
	 * @param msg message
	 */
	public static void info(Class<?> clazz, final String msg) {
		if ( LoggerFactory.getLogger(clazz).isInfoEnabled() )
			LoggerFactory.getLogger(clazz).info(msg);
	}

	/**
	 * Logs info message.
	 * @param clazz Class
	 * @param msg messages
	 */
	public static void info(Class<?> clazz, final String... msg) {
		if ( LoggerFactory.getLogger(clazz).isInfoEnabled() )
			LoggerFactory.getLogger(clazz).info(Helpers.join(msg, "\n\t- "));
	}

	/**
	 * Logs warn message.
	 * @param msg message
	 */
	public static void warn(final String msg) {
		if ( logger.isWarnEnabled() )
			logger.warn(msg);
	}

	/**
	 * Logs warn message.
	 * @param msg messages
	 */
	public static void warn(final String... msg) {
		if ( logger.isWarnEnabled() )
			logger.warn(Helpers.join(msg, "\n\t- "));
	}

	/**
	 * Logs warn message.
	 * @param clazz Class
	 * @param msg message
	 */
	public static void warn(Class<?> clazz, final String msg) {
		if ( LoggerFactory.getLogger(clazz).isWarnEnabled() )
			LoggerFactory.getLogger(clazz).warn(msg);
	}

	/**
	 * Logs warn message.
	 * @param clazz Class
	 * @param msg messages
	 */
	public static void warn(Class<?> clazz, final String... msg) {
		if ( LoggerFactory.getLogger(clazz).isWarnEnabled() )
			LoggerFactory.getLogger(clazz).warn(Helpers.join(msg, "\n\t- "));
	}
}