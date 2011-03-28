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
 *	01/12/2010: Create, based on MemberManagementPanel.
 */
package org.ahunt.simpleRowLog.admin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.AbstractTableModel;

import org.ahunt.simpleRowLog.common.ErrorHandler;
import org.ahunt.simpleRowLog.interfaces.Database;

/**
 * Panel allowing access to the list of admins. Depending on the permissions the
 * administrator has, they may either be able to only view the names (and
 * database id) of admins, or also add, modify and delete admins, or view their
 * other details (permissions). (Check the manual under Administrator
 * Permissions for specific information on the permissions.) Before calling this
 * panel the code should check whether the permission <code>admin_list</code> is
 * set.
 * 
 * @author Andrzej JR Hunt
 * 
 */
public class AdminManagementPanel extends AbstractTableModel implements
		ConfigPanelInterface, MouseListener, ActionListener {

	// TODO: add a make root button?

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	/**
	 * The current administrator who is working with the panel. Is used to
	 * determine permissions.
	 */
	private AdminInfo admin;

	/** The language files for use. */
	private ResourceBundle locAdmin = ResourceBundle.getBundle("admin");
	private ResourceBundle locCommon = ResourceBundle.getBundle("common");

	/** The database we are working upon. */
	private Database db;

	/**
	 * The list of admins currently in the database. Is updated after any
	 * changes. Use this to access any data as opposed to requesting from the
	 * database.
	 */
	private AdminInfo[] admins;

	/** The panel containing all the graphical components. */
	private JPanel displayPanel = new JPanel();

	private JButton addAdminButton = new JButton();
	private JButton editAdminButton = new JButton();
	private JButton editAdminPermissionsButton = new JButton();
	private JButton deleteAdminButton = new JButton();

	/** Table displaying the list of admins. */
	private JTable adminTable;
	private JScrollPane adminTablePane;

	/** Dialog allowing editing of admins. */
	private EditAdminDialog adminDialog;

	/**
	 * Create the AdminManagementPanel, allowing access to and modification of
	 * the list of admin currently in the database.
	 * 
	 * @param db
	 *            The database to be used.
	 * @param admin
	 *            The current administrator accessing the panel (used to
	 *            determine permissions).
	 */
	public AdminManagementPanel(Database db, AdminInfo admin) {
		super();
		this.db = db;
		this.admin = admin;

		// Setup the editing dialog (used throughout)
		adminDialog = new EditAdminDialog(db);

		adminTable = new JTable(this);
		adminTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		adminTablePane = new JScrollPane(adminTable);
		adminTablePane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		adminTablePane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		// Listeners to detect when someone wants to edit (for double clicks on
		// table).
		adminTable.addMouseListener(this);
		adminTablePane.addMouseListener(this);

		// Setup the display
		addAdminButton.setText(locAdmin.getString("admin.add"));
		editAdminButton.setText(locAdmin.getString("admin.edit"));
		editAdminPermissionsButton.setText(locAdmin
				.getString("admin.edit_permissions"));
		deleteAdminButton.setText(locAdmin.getString("admin.delete"));

		// Listeners for the buttons
		addAdminButton.addActionListener(this);
		editAdminButton.addActionListener(this);
		editAdminPermissionsButton.addActionListener(this);
		deleteAdminButton.addActionListener(this);

		// Disable functions this admin can't use.
		if (admin.getPermissionList().isPermissionSet("admin_list.modify")) {
			editAdminButton.setEnabled(true);
			editAdminPermissionsButton.setEnabled(true);
		} else {
			editAdminButton.setEnabled(false);
			editAdminPermissionsButton.setEnabled(false);
		}
		if (admin.getPermissionList().isPermissionSet("admin_list.remove")) {
			deleteAdminButton.setEnabled(true);
		} else {
			deleteAdminButton.setEnabled(false);
		}

		// Layouting
		GroupLayout l = new GroupLayout(displayPanel);
		displayPanel.setLayout(l);
		l.setAutoCreateGaps(true);
		l.setAutoCreateContainerGaps(true);
		l.setVerticalGroup(l.createSequentialGroup().addComponent(
				adminTablePane).addGroup(
				l.createParallelGroup().addComponent(addAdminButton)
						.addComponent(editAdminButton).addComponent(
								editAdminPermissionsButton).addComponent(
								deleteAdminButton)));
		l.setHorizontalGroup(l.createParallelGroup().addComponent(
				adminTablePane).addGroup(
				l.createSequentialGroup().addPreferredGap(
						LayoutStyle.ComponentPlacement.RELATED,
						GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(addAdminButton).addComponent(
								editAdminButton).addComponent(
								editAdminPermissionsButton).addComponent(
								deleteAdminButton)));
		// Get the data loaded in from the db.
		updateAdmins();
	}

	/**
	 * The columns in the table.
	 */
	private String[] columnNames = { locAdmin.getString("admin.username"),
			locCommon.getString("name"), locAdmin.getString("admin.isRoot"),
			locAdmin.getString("admin.comment") };

	/**
	 * {@inheritDoc}
	 */
	public int getColumnCount() {
		// No details on admins if not allowed, i.e only show username,
		// root and name columns.
		if (!admin.getPermissionList().isPermissionSet("admin_list.details"))
			return 3;
		return columnNames.length;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getColumnName(int col) {
		return columnNames[col];
	}

	/**
	 * {@inheritDoc}
	 */
	public int getRowCount() {
		return admins.length;
	}

	/**
	 * Edit the admin in a specific row by calling the EditAdminDialog. Only
	 * works if the current admin has the required permissions.
	 * 
	 * @param row
	 *            The row in which the admin is on the table.
	 */
	private void editAdminAt(int row) {
		// Exit if not allowed.
		if (!admin.getPermissionList().isPermissionSet("admin_list.modify"))
			return;
		adminDialog.editAdmin(admins[row], admin);
	}

	/**
	 * Edit the permissions for an admin in a specific row by calling the
	 * EditAdminPermissions dialog. Only works if the current admin has the
	 * required permissions.
	 * 
	 * @param row
	 *            The row in which the admin is on the table.
	 */
	private void editAdminPermissionsAt(int row) {
		// Exit if not allowed.
		if (!admin.getPermissionList().isPermissionSet("admin_list.modify"))
			return;
		new EditAdminPermissions(db, admins[row]);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getValueAt(int row, int col) {
		switch (col) {
		case 0:
			return admins[row].getUsername();
		case 1:
			return admins[row].getName();
		case 2:
			return admins[row].isRoot();
		case 3:
			return admins[row].getComment();
		default:
			return null;
		}
	}

	/**
	 * Updates the displayed outings using the database.
	 * 
	 */
	public void updateAdmins() {
		try {
			admins = db.getAdmins();
			fireTableDataChanged();
		} catch (Exception e) {
			ErrorHandler.handleError(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void apply() {
		// We don't need to do anything here since everything "autosaves".
		// Insert a philosophical discussion on autosave, and implied save, and
		// [...] here...
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (arg0.getClickCount() != 2) {
			return; // Return unless we have a double click
		}
		if (arg0.getSource() == adminTablePane) { // A click on blank area
			adminDialog.addAdmin(admin);
		} else if (arg0.getSource() == adminTable) {
			if (adminTable.getSelectedRow() >= 0)
				editAdminAt(adminTable.getSelectedRow());
		}
		updateAdmins();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPanel getPanel() {
		return displayPanel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == addAdminButton) {
			adminDialog.addAdmin(admin);
		} else if (arg0.getSource() == editAdminButton) {
			if (adminTable.getSelectedRow() >= 0)
				editAdminAt(adminTable.getSelectedRow());
		} else if (arg0.getSource() == deleteAdminButton) {
			if (adminTable.getSelectedRow() >= 0) {
				AdminInfo toRemove = admins[adminTable.getSelectedRow()];
				if (JOptionPane.showConfirmDialog(null, MessageFormat.format(
						locAdmin.getString("admin.deleteAdmin"), toRemove
								.getUsername(), toRemove.getName())) == JOptionPane.OK_OPTION)
					try {
						db.removeAdmin(toRemove.getUsername());
					} catch (Exception e) {
						// TODO: deal with the error.
					}
			}
		} else if (arg0.getSource() == editAdminPermissionsButton) {
			if (adminTable.getSelectedRow() >= 0) {
				editAdminPermissionsAt(adminTable.getSelectedRow());
			}
			return;
		}
		updateAdmins();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

	@Override
	public String getName() {
		return locAdmin.getString("dialog.conf.edit_admins.title");
	}

}
