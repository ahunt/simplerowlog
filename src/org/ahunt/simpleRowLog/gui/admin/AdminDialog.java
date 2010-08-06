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
package org.ahunt.simpleRowLog.gui.admin;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileNotFoundException;
import java.util.ResourceBundle;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.AbstractTableModel;

import org.ahunt.simpleRowLog.common.AdminInfo;
import org.ahunt.simpleRowLog.common.AdminPermissionList;
import org.ahunt.simpleRowLog.common.ErrorHandler;
import org.ahunt.simpleRowLog.common.MemberInfo;
import org.ahunt.simpleRowLog.conf.Configuration;
import org.ahunt.simpleRowLog.interfaces.Database;

/**
 * @author Andrzej JR Hunt
 * 
 */
public class AdminDialog extends JDialog {

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

	/**
	 * The current admin's permissions.
	 */
	private AdminPermissionList permissions;

	// TODO: add reset button, loading default configuration.

	/**
	 * Open a new configuration Dialog. This exits once the dialog closes.
	 */
	public AdminDialog(Database db, AdminInfo admin) {
		this.db = db;
		this.admin = admin;
		// TODO: set full permissions on root, and create those permissions.
		this.permissions = db.getAdminPermissionList(admin.getUsername());
		try {
			config = Configuration.getConf("simpleGUI");
		} catch (FileNotFoundException e) {
			ErrorHandler.handleError(e);
		}
		// TODO: the rest of the dialog.

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

		// TODO: find size
		this.setSize(500, 300);

		// Add all the appropriate panels.
		// if (permissions.isPermissionSet("member_list"))
		tabPane.addTab(rb.getString("dialog.conf.edit_members.title"),
				new MemberManagementPanel(db).getPanel());

		this.setVisible(true);
	}

	/**
	 * Update the labels on any buttons etc.
	 */
	private void updateLabels() {
		// Buttons
		applyButton.setText(rb.getString("dialog.conf.apply"));
		exitButton.setText(rb.getString("dialog.conf.exit"));
	}



}
