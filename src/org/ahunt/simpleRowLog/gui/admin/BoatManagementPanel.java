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
import org.ahunt.simpleRowLog.common.AdminPermissionList;
import org.ahunt.simpleRowLog.common.BoatInfo;
import org.ahunt.simpleRowLog.common.ErrorHandler;
import org.ahunt.simpleRowLog.common.MemberInfo;
import org.ahunt.simpleRowLog.conf.Configuration;
import org.ahunt.simpleRowLog.interfaces.Database;

/**
 * Panel allowing access to the list of members.
 * 
 * @author Andrzej JR Hunt
 * 
 */
public class BoatManagementPanel extends AbstractTableModel implements
		ConfigPanelInterface, MouseListener {

	/** The configuration in use */
	private Configuration config;

	private AdminInfo admin;

	/** The language files for use. */
	private ResourceBundle loc = ResourceBundle.getBundle("admin");
	private ResourceBundle locCommon = ResourceBundle.getBundle("common");

	/** The database we are working upon. */
	Database db;

	private JPanel displayPanel = new JPanel();

	private EditBoatDialog boatDialog;
	private JButton addBoatButton = new JButton();
	private JButton editBoatButton = new JButton();
	private JButton deleteBoatButton = new JButton();

	private JScrollPane boatTablePane;
	private JTable boatTable;

	private BoatInfo[] boats;

	public BoatManagementPanel(Database db, AdminInfo admin) {
		super();
		this.db = db;
		this.admin = admin;
		boatDialog = new EditBoatDialog(db, admin);

		boatTable = new JTable(this);
		boatTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		boatTablePane = new JScrollPane(boatTable);
		boatTablePane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		boatTablePane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		// Listeners to listen for attempted edits.
		boatTable.addMouseListener(this);
		boatTablePane.addMouseListener(this);

		// Setup the display
		addBoatButton.setText(loc.getString("boat.add"));
		editBoatButton.setText(loc.getString("boat.edit"));
		deleteBoatButton.setText(loc.getString("boat.delete"));

		if (admin.getPermissionList().isPermissionSet("boat_list.modify")) {
			editBoatButton.setVisible(true);
		} else {
			editBoatButton.setVisible(false);
		}
		if (admin.getPermissionList().isPermissionSet("boat_list.remove")) {
			deleteBoatButton.setVisible(true);
		} else {
			deleteBoatButton.setVisible(false);
		}

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

		updateBoats();

	}

	private String[] columnNames = { loc.getString("boat.name"),
			loc.getString("boat.type"),
			loc.getString("boat.in_house") };

	public int getColumnCount() {
		return columnNames.length;
	}

	/**
	 * Get the name for a specific column.
	 * 
	 * @param col
	 *            The column.
	 * @return The name of the column.
	 */
	public String getColumnName(int col) {
		return columnNames[col];
	}

	public int getRowCount() {
		return boats.length;
	}

	public void editBoatAt(int row) {
		// Exit if not allowed.
		if (!admin.getPermissionList().isPermissionSet("member_list.modify"))
			return;
		boatDialog.editBoat(boats[row]);
		updateBoats();
	}

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
	 * Updates the displayed outings using the database.
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

	@Override
	public void apply() {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (arg0.getClickCount() != 2) {
			return; // Return unless we have a double click
		}
		if (arg0.getSource() == boatTablePane) { // Click on blank area
			boatDialog.addBoat();
		} else if (arg0.getSource() == boatTable) {
			this.editBoatAt(boatTable.getSelectedRow());
		}
		this.updateBoats();

	}

	@Override
	public JPanel getPanel() {
		return displayPanel;
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {

	}

}
