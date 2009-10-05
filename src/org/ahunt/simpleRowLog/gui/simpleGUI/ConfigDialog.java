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
 *	21/09/2009:	Created.
 */
package org.ahunt.simpleRowLog.gui.simpleGUI;

import java.util.Properties;

import javax.swing.JDialog;

/**
 * @author Andrzej JR Hunt
 *
 */
class ConfigDialog extends JDialog {
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/** The configuration in use */
	private Properties config;
	
	/**
	 * Open a new configuration Dialog. This exits once the dialog closes. 
	 * @param config The current configuration. A default configuration will be
	 * created if this is null.
	 */
	public ConfigDialog(Properties config) {
		if (config == null) {
			config = getDefaultConfig();
		}
		this.config = config;
		//TODO: the rest of the dialog.
	}

	/**
	 * Returns the default configuration for the simpleGUI.
	 * @return The default configuration.
	 */
	private Properties getDefaultConfig() {
		//TODO: implement.
		return null;
	}
}
