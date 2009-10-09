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
 *	23/08/2009:	Changelog added.
 */
package org.ahunt.simpleRowLog.db.simpleDB;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Andrzej Hunt
 *
 */
public class Util {
	
	private static String scriptLocation = "scripts/simpleDB/";

	/**
	 *  Load the specified sql script for running on the database.
	 * @param name The name of the script. The filename to be loaded is
	 * constructed from this by prepending location and appending the suffix.
	 * @return The script.
	 */
	
	public static String loadScript(String name) throws IOException {
		String location = scriptLocation + name + ".sql";
		BufferedReader in = new BufferedReader(new FileReader(location));
		String line;
		StringBuffer buff = new StringBuffer();
		while ((line = in.readLine()) != null) {
			// Only append if not a commented line.
			if ((line.length() > 0) && !line.substring(0,1).equals("#")) {
				buff.append(line);
			}
		}
		return buff.toString();
	}
}
