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
public class MemberManagementPanel extends AbstractTableModel implements
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

	private EditMemberDialog memberDialog;
	private JButton addMemberButton = new JButton();
	private JButton editMemberButton = new JButton();
	private JButton deleteMemberButton = new JButton();

	private JScrollPane memberTablePane;
	private JTable memberTable;

	private MemberInfo[] members;

	public MemberManagementPanel(Database db, AdminInfo admin) {
		super();
		this.db = db;
		this.admin = admin;
		memberDialog = new EditMemberDialog(db);

		memberTable = new JTable(this);
		memberTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		memberTablePane = new JScrollPane(memberTable);
		memberTablePane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		memberTablePane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		// Listeners to listen for attempted edits.
		memberTable.addMouseListener(this);
		memberTablePane.addMouseListener(this);

		// Setup the display
		addMemberButton.setText(loc.getString("member.add"));
		editMemberButton.setText(loc.getString("member.edit"));
		deleteMemberButton.setText(loc.getString("member.delete"));

		if (admin.getPermissionList().isPermissionSet("member_list.modify")) {
			editMemberButton.setVisible(true);
		} else {
			editMemberButton.setVisible(false);
		}
		if (admin.getPermissionList().isPermissionSet("member_list.remove")) {
			deleteMemberButton.setVisible(true);
		} else {
			deleteMemberButton.setVisible(false);
		}
		
		GroupLayout l = new GroupLayout(displayPanel);
		displayPanel.setLayout(l);
		l.setAutoCreateGaps(true);
		l.setAutoCreateContainerGaps(true);

		l.setVerticalGroup(l.createSequentialGroup().addComponent(
				memberTablePane).addGroup(
				l.createParallelGroup().addComponent(addMemberButton)
						.addComponent(editMemberButton).addComponent(
								deleteMemberButton)));
		l.setHorizontalGroup(l.createParallelGroup().addComponent(
				memberTablePane).addGroup(
				l.createSequentialGroup().addPreferredGap(
						LayoutStyle.ComponentPlacement.RELATED,
						GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(addMemberButton).addComponent(
								editMemberButton).addComponent(
								deleteMemberButton)));

		updateMembers();

	}

	private String[] columnNames = { loc.getString("member.id"),
			locCommon.getString("name"), locCommon.getString("dob"),
			locCommon.getString("group") };

	public int getColumnCount() {
		// No details on members if not allowed
		if (!admin.getPermissionList().isPermissionSet("member_list.details"))
			return 2;
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
		return members.length;
	}

	public void editMemberAt(int row) {
		// Exit if not allowed.
		if (!admin.getPermissionList().isPermissionSet("member_list.modify"))
			return;
		memberDialog.editMember(members[row]);
		updateMembers();
	}

	public Object getValueAt(int row, int col) {
		switch (col) {
		case 0:
			return members[row].getKey();
		case 1:
			return members[row].getName();
		case 2:
			return members[row].getDob();
		case 3:
			return members[row].getGroupInfo().getName();
		default:
			return null;
		}
	}

	/**
	 * Updates the displayed outings using the database.
	 * 
	 */
	public void updateMembers() {
		try {
			members = db.getMembers();
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
		if (arg0.getSource() == memberTablePane) { // Click on blank area
			memberDialog.addMember();
		} else if (arg0.getSource() == memberTable) {
			this.editMemberAt(memberTable.getSelectedRow());
		}
		this.updateMembers();

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
