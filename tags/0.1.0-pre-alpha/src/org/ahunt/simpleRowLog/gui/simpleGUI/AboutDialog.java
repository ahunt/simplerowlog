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
 *  27/04/2010: Started adding the tabbed version.
 *  07/10/2009: Completed initial version.
 *	05/10/2009:	Created.
 */
package org.ahunt.simpleRowLog.gui.simpleGUI;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;

import org.ahunt.simpleRowLog.Info;

/**
 * The About Dialog showing version information and the GPL Licence for simple
 * rowLog. The dialog is a modal dialog, to use the dialog simply call the
 * constructor: <code>new AboutDialog()</code> The constructor returns once the
 * dialog has been closed.
 * 
 * @author Andrzej JR Hunt
 * @version 2
 */
public class AboutDialog extends JDialog implements ActionListener {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	// Language data
	private ResourceBundle rb = ResourceBundle.getBundle("gui");

	// Get the various data about simple rowLog to display.
	private String srlVersion = Info.getVersion();
	private String srlBuildType = Info.getBuildType();
	private String srlWebSite = Info.getWebSite();
	private String srlCopyright = Info.getCopyright(false);

	// Get the java version.
	private String javaVersion = System.getProperty("java.vm.version");
	private String javaName = System.getProperty("java.vm.name");

	private JTabbedPane tabPane = new JTabbedPane();

	/**
	 * Open a new about Dialog. This exits once the dialog closes. It throws an
	 * error if an about Dialog is already open.
	 */
	public AboutDialog() {
		// Prepare the window.
		this.setModal(true);
		this.setTitle(rb.getString("dialog.about.title"));
		// Prepare the content
		String result = MessageFormat.format(
				// Line 1: Copyright
				"<html><b>{0}</b><br/>"
				// Line 2 Website
						+ "<font size=+2><a href=\"{1}\">{1}</a></font><br/>"
						// Line 3 srl version
						+ "<b>{2}</b> {3} ({4})<br/>"
						// Line 4 java version
						+ "<b>{5}</b> {6} ({7})</html>", srlCopyright,
				srlWebSite, rb.getString("dialog.about.srlVersion"),
				srlVersion, srlBuildType, rb
						.getString("dialog.about.javaVersion"), javaVersion,
				javaName);
		JLabel textLabel = new JLabel(result,
				new ImageIcon("img/logo/logo.png"), SwingConstants.LEFT);
		// Load the GPL file to show.
		String disp = "";
		String buff;
		try {
			BufferedReader b = new BufferedReader(new FileReader("LICENSE"));
			while ((buff = b.readLine()) != null) {
				disp += buff + "\n";
			}
		} catch (Exception e) {
			disp = "GNU GPL couldn't be loaded, please refer to http://www.gnu.org/licenses/gpl-3.0.txt";
		}
		// Set up the text field. Read only appearance.
		JTextArea gplTextArea = new JTextArea(disp, 15, 50);
		gplTextArea.setCursor(null);
		gplTextArea.setEditable(false);
		JScrollPane gplPane = new JScrollPane(gplTextArea);

		// The close button.
		JButton exitButton = new JButton(rb.getString("dialog.about.exit"));

		tabPane.add("License", gplPane);

		exitButton.addActionListener(this);
		// Set positions

		GroupLayout l = new GroupLayout(getContentPane());
		getContentPane().setLayout(l);
		l.setAutoCreateGaps(true);
		l.setAutoCreateContainerGaps(true);
		l.setHorizontalGroup(l.createParallelGroup().addComponent(textLabel)
				.addComponent(tabPane).addGroup(
						l.createSequentialGroup().addPreferredGap(
								LayoutStyle.ComponentPlacement.RELATED,
								GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(exitButton).addPreferredGap(
										LayoutStyle.ComponentPlacement.RELATED,
										GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE))

		);
		l.setVerticalGroup(l.createSequentialGroup().addComponent(textLabel)
				.addComponent(tabPane).addGroup(
						l.createParallelGroup().addComponent(exitButton)));

		this.setAlwaysOnTop(true);
		getRootPane().setDefaultButton(exitButton);
		pack();
		setResizable(false);
		setVisible(false);
	}

	/**
	 * The listener for the exit button. I.e. close the window, which causes the
	 * constructor to return.
	 */
	public void actionPerformed(ActionEvent e) {
		this.setVisible(false);
	}

}
