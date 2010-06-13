/*
 *    This file is part of simple rowLog: the open rowing logbook.
 *    Copyright (C) 2010  Andrzej JR Hunt
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
 *	27/04/2010: Corrected bug in capitaliseName(String) where the method would
 *				cause an exception if a zero length string was used.
 *  07/03/2010: Created.
 */
package org.ahunt.simpleRowLog.common;

/**
 * A utility class containing various static methods.
 * 
 * @author Andrzej JR Hunt
 * 
 */
public class Util {

	/**
	 * Capitalise a name, i.e. make every word's first letter capitalised. Also
	 * capitalises after apostrophes.
	 * 
	 * @param name
	 *            The name to be processed.
	 * @return
	 */
	public static String capitaliseName(String name) {
		int l = name.length();
		if (l == 0) return name; // Do nothing if string is empty.
		StringBuffer sb = new StringBuffer(name);
		sb.setCharAt(0, Character.toUpperCase(name.charAt(0)));
		for (int i = 1; i < l - 1; i++) {
			char c = name.charAt(i);
			if (c == ' ' || c == '\'') {
				sb.setCharAt(i + 1, Character.toUpperCase(name.charAt(i + 1)));
			}
		}
		return sb.toString();
	}
}
