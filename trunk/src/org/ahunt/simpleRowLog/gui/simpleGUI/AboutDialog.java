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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
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
public class AboutDialog extends JDialog implements ActionListener {
	
	/**
	 * The currently open dialog. If a dialog is open, this is defined, else
	 * it is null.
	 */
	private static AboutDialog dialog;

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	// TODO: Test function, remove once ready
	public static void main(String[] args) {
	    try {
		    // Set System L&F
	        javax.swing.UIManager.setLookAndFeel(
	            "com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
	    } catch (Exception e) {
			// TODO: handle exception
		}

		new AboutDialog();
		System.exit(0);
	}
	
	
	private ResourceBundle rb = ResourceBundle.getBundle("loc/gui");
	
	// Get the various data about simple rowLog to display.
	private String srlVersion = Info.getVersion();
	private String srlBuildType = Info.getBuildType();
	private String srlWebSite = Info.getWebSite();
	private String srlCopyright = Info.getCopyright();
	
	// Get the java version.
	private String javaVersion = System.getProperty("java.version");
	
	//TODO: Implement error throwing if dialog already open.
	/**
	 * Open a new about Dialog. This exits once the dialog closes. It throws an
	 * error if an about Dialog is already open.
	 */
	public AboutDialog() {
		// Prepare the window.
		this.setModal(true);
		this.setTitle(rb.getString("dialog.about.title"));
		// Prepare the content
		JLabel textLabel = new JLabel("<html><b>"
				+ srlCopyright + "</b><br/>"
				+ "<a href=\"" + srlWebSite +"\">" + srlWebSite + "</a><br/>"
				+ "<b>" + rb.getString("dialog.about.srlVersion") + "</b>"
				+ " <i>" + srlVersion + " (" + srlBuildType + ")</i><br/>"
				+ "<b>" + rb.getString("dialog.about.javaVersion") +"</b"
				+ " <i>" + javaVersion + "</i>"
				+"</html>",
				new ImageIcon("img/logo/logo.png"),
				SwingConstants.LEFT);
		// Load the GPL file to show.
		String disp = "";
		String buff;
		try {
			BufferedReader b = new BufferedReader(new FileReader("LICENSE"));
			while ((buff= b.readLine())!= null) {
				disp += buff + "\n";
			}
		} catch (Exception e) {
			disp = "GNU GPL couldn't be loaded, please refer to http://www.gnu.org/licenses/gpl-3.0.txt";
		}
		// Set up the text field. Read only appearance.
		JTextArea gplTextArea = new JTextArea(disp);
		gplTextArea.setCursor(null);
		gplTextArea.setEditable(false);
		JScrollPane gplPane = new JScrollPane(gplTextArea);
		// The close button.
		JButton exitButton = new JButton(rb.getString("dialog.about.exit"));
	
		// Layouting.		
		Container pane = this.getContentPane();
		pane.setLayout(null);
		pane.add(textLabel);
		pane.add(gplPane);
		pane.add(exitButton);
		getRootPane().setDefaultButton(exitButton);
		exitButton.addActionListener(this);
		// Set positions
		Insets insets = pane.getInsets();
		textLabel.setBounds(25 + insets.left, 5 + insets.top, 500, 100);
		gplPane.setBounds(15 + insets.left, 110 + insets.top, 550, 260);
		Dimension size = exitButton.getPreferredSize();
		exitButton.setBounds(250 + insets.left, 380 + insets.top,
				size.width + 30, size.height + 5);
		//TODO: the rest of the dialog. Add an exit button + center.
		// Finish off the window.
		this.setResizable(false);
		this.setSize(590, 450);
		this.setVisible(true);
	}

	/**
	 * The listener for the exit button.
	 */
	public void actionPerformed(ActionEvent e) {
		this.setVisible(false);
	}

}
