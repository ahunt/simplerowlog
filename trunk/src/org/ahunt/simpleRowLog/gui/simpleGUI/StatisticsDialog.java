/*
 *    This file is part of simple rowLog: the open rowing logbook.
 *    Copyright (C) 2011  Andrzej JR Hunt
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
 *  27/04/2011: Created.
 */

package org.ahunt.simpleRowLog.gui.simpleGUI;

import java.util.ResourceBundle;

import javax.swing.JDialog;

import org.ahunt.simpleRowLog.interfaces.Database;

/**
 * Dialog allowing the user to view the statistics for a specific member, group
 * of members, boat, or club.
 * 
 * @author Andrzej J.R. Hunt
 * 
 */
public class StatisticsDialog extends JDialog {

	private Database db;

	private ResourceBundle loc = ResourceBundle.getBundle("gui");

	public StatisticsDialog(Database db) {
		this.db = db;
	}
}
