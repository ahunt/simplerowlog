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

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.ahunt.simpleRowLog.Info;
import org.ahunt.simpleRowLog.conf.Configuration;
import org.ahunt.simpleRowLog.db.simpleDB.Database;
import org.grlea.log.SimpleLogger;

/**
 * The launcher for simple rowLog.
 * 
 * @author Andrzej JR Hunt
 * @version 0.04 - 10. September 2009
 */
public class Launch {

	private static final SimpleLogger log = new SimpleLogger(Launch.class);

	/**
	 * Start the simple rowLog program.
	 * 
	 * @param args
	 *            The commandline arguments. Currently args[0] is dir.
	 */
	public static void main(String[] args) {
		log.entry("main");
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
		log.info("Checking data directory");
		Util.setDataDir(new File(in));
		if (!isValidDir(Util.getDataDir())) {
			printInvalidDir(in);
			log.error("Invalid data directory.");
			System.exit(1);
		}
		// Tell the user things are happening + copyright
		System.out.println(rb.getString("welcome") + "\n"
				+ rb.getString("starting") + "\n[simple rowLog "
				+ Info.getVersion() + " (" + Info.getBuildType() + ")]\n"
				+ Info.getCopyright() + "\n" + Info.getLicence());
		// Load the configuration.
		Configuration conf = Configuration.getConf("main");
		// Set the desired toolkit.
	    try {
		    // Set Desired L&F
	        javax.swing.UIManager.setLookAndFeel(conf.getProperty("gui.toolkit"));
	        // Tell the user it is loaded
	        System.out.println(MessageFormat.format(rb.getString("tkLoaded"),
	        		javax.swing.UIManager.getLookAndFeel().getID()));
	        log.info(javax.swing.UIManager.getLookAndFeel().getID()
	        		+ " set as toolkit.");
	    } catch (Exception e) {
			// TODO: Add error info (log), but keep running with Motif etc.
	    	System.out.println(rb.getString("noGTK"));
	    	log.info("GTK unavailable, default used.");
	    }
		
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
		log.entry("isValidDir");
		log.debugObject("test", test);
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
		System.out.println("Usage:\n\tsimplerowlog DATADIRECTORY");
		//TODO: Print a whole load more.
	}
	

}
