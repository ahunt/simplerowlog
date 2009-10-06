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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

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
		Container pane = this.getContentPane();
		pane.setLayout(null);
		JLabel textLabel = new JLabel("<html>"
				+ srlCopyright + "<br/>"
				+ "<a href=\"" + srlWebSite +"\">" + srlWebSite + "</a><br/>"
				+ rb.getString("dialog.about.srlVersion")
				+ " <i>" + srlVersion + " (" + srlBuildType + ")</i><br/>"
				+ rb.getString("dialog.about.javaVersion")
				+ " <i>" + javaVersion + "</i>"
				+"</html>",
				new ImageIcon("img/logo/logo.png"),
				SwingConstants.LEFT);
		String disp = "";
		String buff;
		// TODO: Load GPL File.
		try {
			BufferedReader b = new BufferedReader(new FileReader("LICENSE"));
			while ((buff= b.readLine())!= null) {
				disp += buff + "\n";
			}
		} catch (Exception e) {
			disp = "GNU GPL couldn't be loaded, please refer to http://www.gnu.org/licenses/gpl-3.0.txt";
		}
		// Set up the text field.
		JScrollPane gplPane = new JScrollPane(new JTextArea(disp));
	
		// Layouting.
		pane.add(textLabel);
		pane.add(gplPane);
		Insets insets = pane.getInsets();
		textLabel.setBounds(25 + insets.left, 5 + insets.top, 500, 100);
		gplPane.setBounds(15 + insets.left, 110 + insets.top, 550, 260);



		//TODO: the rest of the dialog. Add an exit button.
		this.setResizable(false);
		this.setSize(600, 450);
		this.setVisible(true);
	}

}
