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
 *	06/08/2010:	Created.
 */
package org.ahunt.simpleRowLog.gui.admin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ResourceBundle;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.AbstractTableModel;

import org.ahunt.simpleRowLog.common.AdminInfo;
import org.ahunt.simpleRowLog.common.BoatInfo;
import org.ahunt.simpleRowLog.common.ErrorHandler;
import org.ahunt.simpleRowLog.interfaces.Database;

/**
 * Panel allowing access to the list of boats. Depending on the permissions the
 * administrator has, they may either be able to only view and add boats, or
 * also modify and delete boats. (Check the manual under Administrator
 * Permissions for specific information on the permissions.) Before calling this
 * panel the code should check whether the permission <code>boat_list</code> is
 * set.
 * 
 * @author Andrzej JR Hunt
 * 
 */
public class BoatManagementPanel extends AbstractTableModel implements
		ConfigPanelInterface, MouseListener, ActionListener {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	/**
	 * The current administrator who is working with the panel. Is used to
	 * determine permissions.
	 */
	private AdminInfo admin;

	/** The language files for use. */
	private ResourceBundle loc = ResourceBundle.getBundle("admin");

	/** The database we are working upon. */
	private Database db;

	/**
	 * The list of boats currently in the database. Is updated after any
	 * changes. Use this to access any data as opposed to requesting from the
	 * database.
	 */
	private BoatInfo[] boats;

	/** The panel containing all the graphical components. */
	private JPanel displayPanel = new JPanel();

	private JButton addBoatButton = new JButton();
	private JButton editBoatButton = new JButton();
	private JButton deleteBoatButton = new JButton();

	/** Table displaying the boats in the database. */
	private JTable boatTable;
	private JScrollPane boatTablePane;

	/** Dialog allowing editing of boats. */
	private EditBoatDialog boatDialog;

	/**
	 * Create the BoatManagementPanel, allowing access to and modification of
	 * the list of boats currently in the database.
	 * 
	 * @param db
	 *            The database to be used.
	 * @param admin
	 *            The current administrator accessing the panel (used to
	 *            determine permissions).
	 */
	public BoatManagementPanel(Database db, AdminInfo admin) {
		super();
		this.db = db;
		this.admin = admin;

		// Set up the boat dialog (it's reused throughout)
		boatDialog = new EditBoatDialog(db, admin);

		boatTable = new JTable(this);
		boatTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		boatTablePane = new JScrollPane(boatTable);
		boatTablePane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		boatTablePane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		// Listeners to detect when someone wants to edit (for double clicks on
		// table).
		boatTable.addMouseListener(this);
		boatTablePane.addMouseListener(this);

		// Setup the display
		addBoatButton.setText(loc.getString("boat.add"));
		editBoatButton.setText(loc.getString("boat.edit"));
		deleteBoatButton.setText(loc.getString("boat.delete"));

		// Listeners for the buttons
		addBoatButton.addActionListener(this);
		editBoatButton.addActionListener(this);
		deleteBoatButton.addActionListener(this);

		// Disable functions this admin can't use.
		if (admin.getPermissionList().isPermissionSet("boat_list.modify")) {
			editBoatButton.setEnabled(true);
		} else {
			editBoatButton.setEnabled(false);
		}
		if (admin.getPermissionList().isPermissionSet("boat_list.remove")) {
			deleteBoatButton.setEnabled(true);
		} else {
			deleteBoatButton.setEnabled(false);
		}

		// Layouting
		GroupLayout l = new GroupLayout(displayPanel);
		displayPanel.setLayout(l);
		l.setAutoCreateGaps(true);
		l.setAutoCreateContainerGaps(true);
		l.setVerticalGroup(l.createSequentialGroup()
				.addComponent(boatTablePane).addGroup(
						l.createParallelGroup().addComponent(addBoatButton)
								.addComponent(editBoatButton).addComponent(
										deleteBoatButton)));
		l.setHorizontalGroup(l.createParallelGroup()
				.addComponent(boatTablePane).addGroup(
						l.createSequentialGroup().addPreferredGap(
								LayoutStyle.ComponentPlacement.RELATED,
								GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(addBoatButton).addComponent(
										editBoatButton).addComponent(
										deleteBoatButton)));
		// Get the data loaded in from the db.
		updateBoats();
	}

	/**
	 * The columns in the table.
	 */
	private String[] columnNames = { loc.getString("boat.name"),
			loc.getString("boat.type"), loc.getString("boat.in_house") };

	/**
	 * {@inheritDoc}
	 */
	public int getColumnCount() {
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
		return boats.length;
	}

	/**
	 * Edit the boat at a specific row. This calls up the editing dialog to edit
	 * the boat.
	 * 
	 * @param row
	 *            The row which is to be modified.
	 */
	private void editBoatAt(int row) {
		// Exit if not allowed.
		if (!admin.getPermissionList().isPermissionSet("member_list.modify"))
			return;
		boatDialog.editBoat(boats[row]);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getValueAt(int row, int col) {
		switch (col) {
		case 0:
			return boats[row].getName();
		case 1:
			return boats[row].getType();
		case 2:
			return boats[row].inHouse();
		default:
			return null;
		}
	}

	/**
	 * Update the list of outings stored for use.
	 * 
	 */
	public void updateBoats() {
		try {
			boats = db.getBoats();
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
		// We do all saving automatically so no need.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPanel getPanel() {
		if (!admin.getPermissionList().isPermissionSet("boat_list")) {
			return new JPanel();
		}
		return displayPanel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == addBoatButton) {
			boatDialog.addBoat();
		} else if (arg0.getSource() == editBoatButton) {
			if (boatTable.getSelectedRow() >= 0)
				editBoatAt(boatTable.getSelectedRow());
		} else if (arg0.getSource() == deleteBoatButton) {
			// TODO: ask for confirmation and then do. Also include a relinking
			// dialog, i.e. what the outings with this boat should be reassigned
			// to.
		}
		updateBoats();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (arg0.getClickCount() != 2) {
			return; // Return unless we have a double click
		}
		if (arg0.getSource() == boatTablePane) { // Click on blank area
			boatDialog.addBoat();
		} else if (arg0.getSource() == boatTable) {
			editBoatAt(boatTable.getSelectedRow());
		}
		updateBoats();

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
		return loc.getString("dialog.conf.edit_boats.title");
	}

}
