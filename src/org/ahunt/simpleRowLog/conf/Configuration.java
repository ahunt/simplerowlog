/*
 *    This file is part of simple rowLog: the open rowing logbook.
 *    Copyright (C) 2009  Andrzej JR Hunt
 *    
 *    simple rowLog is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    simple rowLog is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with simple rowLog.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *	Changelog:
 *	08/10/2009:	Changelog added.
 */
package org.ahunt.simpleRowLog.conf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Properties;

/**
 * A wrapper class for properties, which deals with the chores such as finding
 * where the file is, loading a default if unavailable, and saving on
 * modification. Caching is also done.
 * @author Andrzej JR Hunt
 *
 */
public class Configuration {

	/** The properties file. */
	private Properties prop;
	/** Whether the properties should be saved on modification. */
	private Boolean storeOnModify = true;

	/** Cache storing opened  configurations. */
	private static Hashtable<String, Configuration> cache = new Hashtable<String, Configuration>();
		  
	/**
	 * Get the desired configuration.
	 * @param desired Name of config.
	 * @param doCache Whether the configuration should be cached.
	 * @see #getConf(String)
	 */
	private Configuration(String desired, boolean doCache) {
		prop = new Properties();
		try {
			prop.load(new BufferedReader(new FileReader("conf/" + desired
					+ ".properties")));
			} catch (Exception e) {
			try {
				prop.load(new BufferedReader(new FileReader("conf/default/" + desired
						+ ".properties")));
			} catch (Exception f) {
				// TODO: handle exception
			}
		}
		if (doCache) {
			cache.put(prop.getProperty("FILENAME"),this);
		}
	}

	/**
	 * Get the configuration file of specified name. The configuration will be
	 * cached.
	 * @param desired The configuration you want.
	 * @return The Configuration file.
	 * @see #getConf(String, boolean)
	 */
	public static Configuration getConf(String desired) {
		return getConf(desired, true);
	}
	
	/**
	 * Get the configuration file of specified name. Path and prefix will be
	 * added automatically, and should not be specified. I.e. to load
	 * main.properties, use <code>Configuration.getConf("main")</code>,
	 * which will load the file conf/main.properties, and if it doesn't exist,
	 * the default file will be loaded from conf/default/main.properties.
	 * @param desired The configuration you want.
	 * @param doCache Whether the configuration should be cached.
	 * @return The Configuration file.
	 */
	public static Configuration getConf(String desired, boolean doCache) {
		if (cache.containsKey(desired +".properties")) {
			return cache.get(desired +".properties");
		} else {
			return new Configuration(desired, doCache);
		}
	}
	
	/**
	 * Get the property with key <code>key</code>. A simple wrapper for
	 * {@link java.util.Properties#getProperty(String)}.
	 * @param key The property's key.
	 * @return The property's value.
	 */
	public String getProperty(String key) {
		return prop.getProperty(key);
	}
	
	/**
	 * Select whether the properties file should automativally be saved on
	 * modification. By default this is on.
	 * @param storeOnModify Whether or not the file should be automatically
	 * 			saved.
	 */
	public synchronized void setStoreOnModify(boolean storeOnModify) {
		this.storeOnModify = storeOnModify;
	}
	
	/**
	 * Set the property with key <code>key</code> to value <code>value</value>.
	 * A simple wrapper for {@link java.util.Properties#setProperty(String)}.
	 * The file will be automatically saved if <code>storeOnModify</code> is set.
	 * @param key The property's key.
	 * @param value The property's new value.
	 */
	public synchronized void setProperty(String key, String value) {
		// Don't do modify the Filename, since that would cause chaos.
		if (!key.equals("FILENAME")) {
			prop.setProperty(key, value);
			if (storeOnModify) {
				save();
			}			
		}
	}
	
	/**
	 * Save the configuration file to it's default location.
	 */
	public synchronized void save() {
		try {
			prop.store(new BufferedWriter(new PrintWriter("conf/"
					+ prop.getProperty("FILENAME"))), "Auto stored file");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * Force the config file to reload the default configuration.
	 */
	public void loadDefault() {
		try {
			prop.load(new BufferedReader(new FileReader("conf/default/"
					+ prop.getProperty("FILENAME"))));
		} catch (Exception f) {
			// TODO: handle exception
		}
	}
}
