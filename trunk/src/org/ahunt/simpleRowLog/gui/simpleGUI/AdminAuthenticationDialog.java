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
 *  11/03/2010: Created,
 */
package org.ahunt.simpleRowLog.gui.simpleGUI;

import javax.swing.JDialog;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.ahunt.simpleRowLog.common.AdminInfo;

public class AdminAuthenticationDialog extends JDialog {

	JTextField usernameEntry = new JTextField(32);
	JPasswordField passwordEntry = new JPasswordField(32);

	public AdminAuthenticationDialog() {

	}

	/**
	 * Do a login, i.e. show the dialog and ask the user to authenticate:
	 * returns the AdminInfo for the admin that has authenticated if the
	 * authentication is successful, otherwise it returns null if the login was
	 * cancelled or unsuccessful.
	 * 
	 * @return The information for the admin that has authenticated.
	 */
	public AdminInfo doLogin() {
		return null;
	}
}
