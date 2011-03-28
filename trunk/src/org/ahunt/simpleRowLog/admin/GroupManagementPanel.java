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
 *	07/08/2010:	Created.
 */
package org.ahunt.simpleRowLog.admin;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import org.ahunt.simpleRowLog.common.InvalidDataException;
import org.ahunt.simpleRowLog.common.ErrorHandler;
import org.ahunt.simpleRowLog.common.GroupInfo;
import org.ahunt.simpleRowLog.common.MemberInfo;
import org.ahunt.simpleRowLog.conf.Configuration;
import org.ahunt.simpleRowLog.interfaces.Database;

/**
 * Panel allowing access to the list of groups.
 * 
 * @author Andrzej JR Hunt
 * 
 */
public class GroupManagementPanel extends AbstractTableModel implements
		ConfigPanelInterface, MouseListener, ActionListener {

	/**
* 
*/
	private static final long serialVersionUID = 1L;

	/** The configuration in use */
	private Configuration config;

	private AdminInfo admin;

	/** The language files for use. */
	private ResourceBundle loc = ResourceBundle.getBundle("admin");
	private ResourceBundle locCommon = ResourceBundle.getBundle("common");

	/** The database we are working upon. */
	private Database db;

	private JPanel displayPanel = new JPanel();

	private EditGroupDialog groupDialog;

	private JButton makeDefaultGroupButton = new JButton();
	private JButton addGroupButton = new JButton();
	private JButton editGroupButton = new JButton();
	private JButton deleteGroupButton = new JButton();

	private JScrollPane groupTablePane;
	private JTable groupTable;

	private GroupInfo[] groups;

	public GroupManagementPanel(Database db, AdminInfo admin) {
		super();
		this.db = db;
		this.admin = admin;
		groupDialog = new EditGroupDialog(db, admin);

		groupTable = new JTable(this);
		groupTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		groupTable.getColumnModel().getColumn(4).setCellRenderer(
				new ColourCellRenderer());

		groupTablePane = new JScrollPane(groupTable);
		groupTablePane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		groupTablePane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		// Listeners to listen for attempted edits.
		groupTable.addMouseListener(this);
		groupTablePane.addMouseListener(this);

		// Setup the display
		makeDefaultGroupButton.setText(loc.getString("group.set_as_default"));
		addGroupButton.setText(loc.getString("group.add"));
		editGroupButton.setText(loc.getString("group.edit"));
		deleteGroupButton.setText(loc.getString("group.delete"));

		// Listeners for the buttons
		makeDefaultGroupButton.addActionListener(this);
		addGroupButton.addActionListener(this);
		editGroupButton.addActionListener(this);
		deleteGroupButton.addActionListener(this);

		if (admin.getPermissionList().isPermissionSet("group_list.modify")) {
			editGroupButton.setEnabled(true);
			makeDefaultGroupButton.setEnabled(true);
		} else {
			editGroupButton.setEnabled(false);
			makeDefaultGroupButton.setEnabled(false);
		}
		if (admin.getPermissionList().isPermissionSet("group_list.remove")) {
			deleteGroupButton.setEnabled(true);
		} else {
			deleteGroupButton.setEnabled(false);
		}

		GroupLayout l = new GroupLayout(displayPanel);
		displayPanel.setLayout(l);
		l.setAutoCreateGaps(true);
		l.setAutoCreateContainerGaps(true);

		l.setVerticalGroup(l.createSequentialGroup().addComponent(
				groupTablePane).addGroup(
				l.createParallelGroup().addComponent(makeDefaultGroupButton)
						.addComponent(addGroupButton).addComponent(
								editGroupButton)
						.addComponent(deleteGroupButton)));
		l.setHorizontalGroup(l.createParallelGroup().addComponent(
				groupTablePane).addGroup(
				l.createSequentialGroup().addComponent(makeDefaultGroupButton)
						.addPreferredGap(
								LayoutStyle.ComponentPlacement.RELATED,
								GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(addGroupButton).addComponent(
								editGroupButton)
						.addComponent(deleteGroupButton)));

		updateGroups();

	}

	private String[] columnNames = { loc.getString("group.id"),
			locCommon.getString("group_name"),
			locCommon.getString("group_description"),
			loc.getString("group.isDefault"), loc.getString("group.colour") };

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
		return groups.length;
	}

	private void editGroupAt(int row) {
		// Exit if not allowed.
		if (!admin.getPermissionList().isPermissionSet("group_list.modify"))
			return;
		groupDialog.editGroup(groups[row]);
		updateGroups();
	}

	public Object getValueAt(int row, int col) {
		switch (col) {
		case 0:
			return groups[row].getId();
		case 1:
			return groups[row].getName();
		case 2:
			return groups[row].getDescription();
		case 3:
			return groups[row].isDefault();
		case 4:
			return groups[row].getDisplayColour();
		default:
			return null;
		}
	}

	/**
	 * Updates the displayed outings using the database.
	 * 
	 */
	public void updateGroups() {
		try {
			groups = db.getGroups();
			fireTableDataChanged();
		} catch (Exception e) {
			ErrorHandler.handleError(e);
		}
	}

	@Override
	public void apply() {
		// We do all saving automatically so no need.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == makeDefaultGroupButton) {
			if (groupTable.getSelectedRow() >= 0) { // A row is selected.
				GroupInfo g = groups[groupTable.getSelectedRow()];
				try {
					db.modifyGroup(g, g.getName(), g.getDescription(), g
							.getDisplayColour(), true);
				} catch (InvalidDataException e) {
					// TODO: inform that such a group already exists.
				}
			}
		} else if (arg0.getSource() == addGroupButton) {
			groupDialog.addGroup();
		} else if (arg0.getSource() == editGroupButton) {
			if (groupTable.getSelectedRow() >= 0)
				editGroupAt(groupTable.getSelectedRow());
		} else if (arg0.getSource() == deleteGroupButton) {
			// TODO: ask for confirmation and then do. Also include a relinking
			// group, i.e. what the members with this group should be reassigned
			// to.
		}
		updateGroups();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (arg0.getClickCount() != 2) {
			return; // Return unless we have a double click
		}
		if (arg0.getSource() == groupTablePane) { // Click on blank area
			groupDialog.addGroup();
		} else if (arg0.getSource() == groupTable) {
			this.editGroupAt(groupTable.getSelectedRow());
		}
		this.updateGroups();

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

	private class ColourCellRenderer extends JLabel implements
			TableCellRenderer {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ColourCellRenderer() {
			setOpaque(true);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			Color col = (Color) value;
			setBackground(col);
			return this;
		}

	}

	@Override
	public String getName() {
		return loc.getString("dialog.conf.edit_groups.title");
	}
}
