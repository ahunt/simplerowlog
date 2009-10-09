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
 *	06/10/2009:	Changelog added.
 */
package org.ahunt.simpleRowLog;

/**
 * Static class providing various information about the program and build.
 * @author Andrzej JR Hunt
 *
 */
public final class Info {
	
	/**
	 * Get the version number of this build.
	 * @return The version number.
	 */
	public static String getVersion() {
		return "0.1.0";
	}
	
	/**
	 * Get the status of the build.
	 * @return The status of the build, e.g "Pre-Alpha", "Alpha", "Beta",
	 * 			"Final"...
	 */
	public static String getBuildType() {
		return "Pre-Alpha";
	}
	
	/**
	 * Get the homepage for this release.
	 * @return The homepage.
	 */
	public static String getWebSite() {
		return "http://srl.ahunt.org";
	}
	
	/**
	 * Get the authors.
	 * @return The authors.
	 */
	public static String getAuthors() {
		return "Andrzej JR Hunt";
	}
	
	/**
	 * Get the copyright information.
	 * @return Copyright string.
	 */
	public static String getCopyright() {
		return "Copyright \u00a9 2009 Andrzej JR Hunt";
	}
	
	/**
	 * Get the licence information.
	 * @return Licence string.
	 */
	public static String getLicence() {
		return "Released under GNU GPL v3, see LICENSE for details.";
	}
	
}
