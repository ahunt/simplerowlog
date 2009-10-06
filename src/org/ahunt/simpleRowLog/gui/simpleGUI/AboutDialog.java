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
 *	05/10/2009:	Created.
 */
package org.ahunt.simpleRowLog.gui.simpleGUI;

import java.util.ResourceBundle;

import javax.swing.JDialog;
import javax.swing.JLabel;
import org.ahunt.simpleRowLog.Info;
/**
 * @author Andrzej JR Hunt
 *
 */
public class AboutDialog extends JDialog {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	
	private ResourceBundle rb = ResourceBundle.getBundle("loc/gui");
	
	// Get the various data about simple rowLog to display.
	private String srlVersion = Info.getVersion();
	private String srlBuildType = Info.getBuildType();
	private String srlWebSite = Info.getWebSite();
	private String srlCopyright = Info.getCopyright();
	
	// Get the java version.
	private String javaVersion = System.getProperty("java.version");
	
	/**
	 * Open a new about Dialog. This exits once the dialog closes. 
	 */
	public AboutDialog() {
		// Prepare the window.
		this.setModal(true);
		this.setTitle(rb.getString("dialog.about.title"));
		this.add(new JLabel("<html>"
				+ srlCopyright + "<br/>"
				+ "<a href=\"" + srlWebSite +"\">" + srlWebSite + "</a><br/>"
				+ rb.getString("dialog.about.srlVersion")
				+ " <i>" + srlVersion + " (" + srlBuildType + ")</i><br/>"
				+ rb.getString("dialog.about.javaVersion")
				+ " <i>" + javaVersion + "</i>"
				+"</html>"
		));
		
		//TODO: the rest of the dialog. (Remember to show GPL.)
		this.setSize(600, 400);
		this.setVisible(true);
	}

}
