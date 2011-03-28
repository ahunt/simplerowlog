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
 *	21/09/2009:	Created.
 *  06/08/2010: Moved to separate package, renamed AdminDialog, started real
 *  			work.
 */
package org.ahunt.simpleRowLog.admin;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;
import javax.swing.LayoutStyle;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.ahunt.simpleRowLog.common.ErrorHandler;
import org.ahunt.simpleRowLog.conf.Configuration;
import org.ahunt.simpleRowLog.interfaces.Database;

/**
 * @author Andrzej JR Hunt
 * 
 */
public class AdminDialog extends JDialog implements ActionListener {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/** The configuration in use */
	private Configuration config;

	private ResourceBundle rb = ResourceBundle.getBundle("admin");

	// UI Buttons
	private JButton applyButton = new JButton();
	private JButton exitButton = new JButton();

	// TODO: check tab placement and remove the call here if unnecessary
	private JTabbedPane tabPane = new JTabbedPane(JTabbedPane.LEFT);

	/**
	 * The database we are working on.
	 */
	private Database db;

	/**
	 * The current admin.
	 */
	private AdminInfo admin;

	private ArrayList<ConfigPanelInterface> configPanels = new ArrayList<ConfigPanelInterface>();

	// TODO: add reset button, loading default configuration.

	/**
	 * Open a new configuration Dialog. This exits once the dialog closes.
	 */
	public AdminDialog(Database db, AdminInfo admin) {
		this.db = db;
		this.admin = admin;
		// TODO: set full permissions on root, and create those permissions.

		try {
			config = Configuration.getConf("simpleGUI");
		} catch (FileNotFoundException e) {
			ErrorHandler.handleError(e);
		}
		// TODO: the rest of the dialog.

		applyButton.addActionListener(this);
		exitButton.addActionListener(this);
		
		this.setModal(true);
		updateLabels();

		// Layout
		GroupLayout l = new GroupLayout(getContentPane());
		getContentPane().setLayout(l);
		l.setAutoCreateGaps(true);
		l.setAutoCreateContainerGaps(true);

		l.setVerticalGroup(l.createSequentialGroup().addComponent(tabPane)
				.addGroup(
						l.createParallelGroup().addComponent(applyButton)
								.addComponent(exitButton)));
		l.setHorizontalGroup(l.createParallelGroup().addComponent(tabPane)
				.addGroup(
						l.createSequentialGroup().addPreferredGap(
								LayoutStyle.ComponentPlacement.RELATED,
								GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(applyButton).addComponent(
										exitButton)));

		// Create all the appropriate panels.
		configPanels.add(new LogbookConfigPanel(db, admin));
		if (admin.getPermissionList().isPermissionSet("member_list")) {
			configPanels.add(new MemberManagementPanel(db, admin));
		}
		if (admin.getPermissionList().isPermissionSet("group_list")) {
			configPanels.add(new GroupManagementPanel(db, admin));
		}
		if (admin.getPermissionList().isPermissionSet("boat_list")) {
			configPanels.add(new BoatManagementPanel(db, admin));
		}
		if (admin.getPermissionList().isPermissionSet("outings_list")) {
			configPanels.add(new OutingManagementPanel(db, admin));
		}
		if (admin.getPermissionList().isPermissionSet("admin_list")) {
			configPanels.add(new AdminManagementPanel(db, admin));
		}

		// Add the panels to the dialog
		for (ConfigPanelInterface c : configPanels) {
			tabPane.addTab(c.getName(), c.getPanel());
		}

		// Set the dialog to 7/10 of screen size and center
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(screenSize.width * 9 / 10, screenSize.height * 9 / 10);
		setLocation(screenSize.width / 2 - getSize().width / 2,
				screenSize.height / 2 - getSize().height / 2);
		// We move the dialog to middle, and move up 1/12 of screen
		
		this.setVisible(true);
	}

	/**
	 * Update the labels on any buttons etc.
	 */
	private void updateLabels() {
		// Buttons
		applyButton.setText(rb.getString("dialog.conf.apply"));
		exitButton.setText(rb.getString("dialog.conf.exit"));
		// Window title
		setTitle(rb.getString("dialog.conf.title"));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == applyButton) {
			configPanels.get(tabPane.getSelectedIndex()).apply();
		} else if (e.getSource() == exitButton) {
			setVisible(false);
		}

	}

}
