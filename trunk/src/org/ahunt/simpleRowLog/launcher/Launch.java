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

package org.ahunt.simpleRowLog.launcher;

import java.io.File;

import java.util.ResourceBundle;

/**
 * The launcher for simple rowLog.
 * 
 * @author Andrzej JR Hunt
 * @version 0.04 - 10. September 2009
 */
public class Launch {


	/**
	 * Start the simple rowLog program.
	 * 
	 * @param args
	 *            The commandline arguments. Currently args[0] is dir.
	 */
	public static void main(String[] args) {
		// String for storing alternate config directory, if required.
		String in;
		// Get the language data.
		ResourceBundle rb = ResourceBundle.getBundle("loc/startup");
		try {
			in = args[0];
			if (in.equals("-h")) {
				printUsage();
				System.exit(0);
			}
		} catch (Exception e) {
			in = "./";
		}
		// Check and store the data directory.
		Util.setDataDir(new File(in));
		if (!isValidDir(Util.getDataDir())) {
			printInvalidDir(in);
			System.exit(1);
		}
		// Tell the user things are happening + copyright
		System.out.println(rb.getString("welcome") + "\n"
				+ rb.getString("starting"));
		printCopyInfo();
		//TODO: Do stuff. (Splash, Load DB, start GUI) Remember the data dir.
	}

	/**
	 * Tests whether the specified path corresponds to a real simple rowLog data
	 * directory.
	 * 
	 * @param dir
	 *            The path of the directory to test.
	 */
	private static boolean isValidDir(File test) {
		if (!test.isDirectory()) {
			return false;
		} else {
			return true;
			// TODO: Check whether contents of directory also correspond to
			// correct structure.
		}
	}

	/**
	 * Prints that a specified path is not a valid data directory.
	 * 
	 * @param dir
	 *            The user-specified path.
	 */
	private static void printInvalidDir(String dir) {
		System.out.println("Sorry, the directory you specified is not a valid"
				+ " directory:\n" + dir);
		System.out.println("\nRun \"simplerowlog -h\" for more information.");
	}

	/**
	 * Prints how to use the program.
	 * 
	 */
	private static void printUsage() {
		printCopyInfo();
		System.out.println("Usage:\n\tsimplerowlog DATADIRECTORY");
		//TODO: Print a whole load more.
	}
	
	/**
	 * Print the copying information for simple rowLog.
	 */
	private static void printCopyInfo() {
		System.out.println("Copyright (c) 2009 Andrzej JR Hunt - Licensed under GNU GPL v3");
	}

}
