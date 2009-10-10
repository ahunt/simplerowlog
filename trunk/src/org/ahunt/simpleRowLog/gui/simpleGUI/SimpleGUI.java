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

package org.ahunt.simpleRowLog.gui.simpleGUI;

import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;
import java.util.Properties;
import java.util.Date;

import java.io.FileInputStream;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.ahunt.simpleRowLog.conf.Configuration;
import org.ahunt.simpleRowLog.interfaces.Database;

import sun.awt.X11.Screen;


/**
 * A simple graphical user Interface for simple rowLog.
 * 
 * @author Andrzej JR Hunt
 * @version 0.01 - 03. November 2008
 */
public class SimpleGUI extends JFrame {

	static final long serialVersionUID = 1l;

	// Get the language file
	private ResourceBundle rb = ResourceBundle.getBundle("gui");
	
	// File Menu
	private JMenu menuFile = new JMenu();

	// File Menu: Exit
	private JMenuItem menuFileExit = new JMenuItem();

	// File Menu: New Member
	private JMenuItem menuFileNewMember = new JMenuItem();

	// Options Menu
	private JMenu menuOptions = new JMenu();

	// Options Menu: Admin mode
	private JMenuItem menuOptionsAdmin = new JMenuItem();

	// Help menu
	private JMenu menuHelp = new JMenu();

	// Help Menu: Help contents
	private JMenuItem menuHelpHelp = new JMenuItem();

	// Help Menu: About
	private JMenuItem menuHelpAbout = new JMenuItem();

	// The database
	private Database db;
	
	// The current date selected in the window. (Not specifically today's date.)
	private Date current = new Date();

	// Configuration file.
	Configuration conf;
	
	public SimpleGUI(Database db) {
		conf = Configuration.getConf("simpleGUI");
		this.db = db;
		setupMenus();
		reloadConfig();
		updateOutings();
		setVisible(true);
	}

	/**
	 * Tells the window to take all chaged settings into account.
	 * 
	 */
	private void reloadConfig() {
		if (conf.getProperty("fullscreen").equals("true")) {
			setFullScreen(true);
		}
	}

	/**
	 * Sets up the menus: Adds them, localises them.
	 * 
	 */
	private void setupMenus() {
		setJMenuBar(new JMenuBar());
		JMenuBar mb = getJMenuBar();
		// File menu
		mb.add(menuFile);
		menuFile.add(menuFileExit);
		menuFile.add(menuFileNewMember);
		// Options menu
		mb.add(menuOptions);
		menuOptions.add(menuOptionsAdmin);
		// Help menu
		mb.add(menuHelp);
		menuHelp.add(menuHelpHelp);
		menuHelp.add(menuHelpAbout);

		updateMenus();
	}

	/**
	 * Set the window to be full screen.
	 * 
	 * @param setFullScreen
	 *            True if the window is to be fullscreen, false if not to be
	 *            full screen.
	 */
	private void setFullScreen(boolean setFullScreen) {
		setUndecorated(setFullScreen);
		setResizable(!setFullScreen);
		setLocation(0, 0);
	}

	/**
	 * Localises the menus, also hides/shows any items that have been edited in
	 * the settings.
	 * 
	 */
	private void updateMenus() {
		// File menu
		menuFile.setText(rb.getString("file"));
		menuFileExit.setText(rb.getString("file.exit"));
		menuFileNewMember.setText(rb.getString("newMember"));
		// Options menu
		menuOptions.setText(rb.getString("options"));
		menuOptionsAdmin.setText(rb.getString("options.admin"));
		// Help menu
		menuHelp.setText(rb.getString("help"));
		menuHelpHelp.setText(rb.getString("help.help"));
		menuHelpAbout.setText(rb.getString("help.about"));
	}

	/**
	 * Updates the displayed outings using the database.
	 * 
	 */
	private void updateOutings() {
		// TODO: Complete
		//db.getOutings(current);
	}

	class ExitListener extends WindowAdapter {
		// TODO: Also implement exit button + behaviour
		public void windowClosing(WindowEvent e) {
			onClose();
		}

		private void onClose() {
			// TODO: stuff
			// TODO: some method has to be called to clean up and save
			//       data that changes e.g. config.
		}
	}
}
