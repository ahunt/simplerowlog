/*
 *    This file is part of simple rowLog: the open rowing logbook.
 *    Copyright (C) 2009, 2010  Andrzej JR Hunt
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
 *  24/01/2010: Added splash info, other changes.
 *	23/08/2009:	Changelog added.
 */

package org.ahunt.simpleRowLog.launcher;

import java.io.File;
import java.io.FileNotFoundException;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import org.ahunt.simpleRowLog.Info;
import org.ahunt.simpleRowLog.conf.Configuration;
import org.ahunt.simpleRowLog.db.simpleDB.Database;
import org.ahunt.simpleRowLog.gui.simpleGUI.SimpleGUI;
import org.grlea.log.SimpleLogger;

/**
 * The launcher for simple rowLog.
 * 
 * @author Andrzej JR Hunt
 * @version 0.05 - 24. January 2010
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
		log.info("simple rowLog starting");
		// String for storing alternate config directory, if required.
		String in;
		// Get the language data.
		ResourceBundle rb = ResourceBundle.getBundle("startup");
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
				+ Info.getCopyright(true) + "\n" + Info.getLicence());
		// Load the configuration.
		Configuration conf;
		SplashManager sm = new SplashManager(10);
		try {
			conf = Configuration.getConf("main");
			// Set Desired L&F
			String desiredLaf = conf.getProperty("gui.toolkit");
			javax.swing.UIManager
					.setLookAndFeel((!desiredLaf.equals("") ? desiredLaf
							: javax.swing.UIManager
									.getSystemLookAndFeelClassName()));
			// Tell the user it is loaded
			System.out.println(MessageFormat.format(">> "
					+ rb.getString("tkLoaded"), javax.swing.UIManager
					.getLookAndFeel().getID()));
			log.info(javax.swing.UIManager.getLookAndFeel().getID()
					+ " set as toolkit.");
		} catch (FileNotFoundException e) {
			JOptionPane
					.showMessageDialog(
							null,
							rb
									.getString("Configuration not found. simple rowLog cannot start. Please ensure that you have an undamaged installation before restarting."),
							rb.getString("Fatal error!"),
							JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		} catch (Exception e) {
			System.out.println(rb.getString("preferred_toolkit_unavailable"));
			log.info("Preferred toolit unavailable, default used.");
		}
		sm.setProgress(30); // Splash: 30%
		Database db = Database.getInstance();
		sm.setProgress(50);
		SimpleGUI gui = new SimpleGUI(db);
		sm.setProgress(70);
		gui.setVisible(true);
		// sm.setProgress(50);
		//
		// try{Thread.sleep(500);}catch(Exception e){}
		// sm.setProgress(80);
		// try{Thread.sleep(500);}catch(Exception e){}
		// sm.setProgress(90);
		// try{Thread.sleep(500);}catch(Exception e){}
		// sm.setProgress(100);
		// try{Thread.sleep(500);}catch(Exception e){}

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
		// System.out.println("Usage:\n\tsimplerowlog DATADIRECTORY");
		System.out.println("Simply run simplerowlog to use.");

	}

}
