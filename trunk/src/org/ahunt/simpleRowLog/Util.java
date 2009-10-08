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
package org.ahunt.simpleRowLog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Properties;

/**
 * A utility class for getting various data the simple rowLog may need.
 * @author Andrzej JR Hunt
 *
 */
public class Util {
	
	/** Stores the Properties files that are opened. */
	private static ArrayList<Properties> propList = new ArrayList<Properties>();

	// Temporary test method.
	public static void main(String[] args) {
		getConfig("main");
		saveConfig();
	}
	/**
	 * Get the configuration file of a specific name. This method tries to load
	 * the configuration file from conf/{file}.properties, if this fails, the 
	 * default properties file is loaded from conf/default/{file}.properties.
	 * Note that all properties files are accounted for by this class, and to
	 * save the configuration files it is only necessary to call
	 * <cdoe>Util.saveConfig()</code>.
	 * @param desired The name of the desired configuration file.
	 * @return The desired properties.
	 */
	public static Properties getConfig(String desired) {
		Properties out = new Properties();
		try {
			out.load(new BufferedReader(new FileReader("conf/" + desired
					+ ".properties")));
			} catch (Exception e) {
			try {
				out.load(new BufferedReader(new FileReader("conf/default/" + desired
						+ ".properties")));
			} catch (Exception f) {
				// TODO: handle exception
			}
		}
		propList.add(out);
		return out;	
	}
	
	public static void saveConfig() {
		for(Properties p : propList) {
			try {
				p.store(new BufferedWriter(new PrintWriter("conf/"
						+ p.getProperty("FILENAME"))), "Auto stored file");
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
}
