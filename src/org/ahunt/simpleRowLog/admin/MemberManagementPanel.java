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
package org.ahunt.simpleRowLog.admin;

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

import org.ahunt.simpleRowLog.common.ErrorHandler;
import org.ahunt.simpleRowLog.common.MemberInfo;
import org.ahunt.simpleRowLog.interfaces.Database;

/**
 * Panel allowing access to the list of members. Depending on the permissions
 * the administrator has, they may either be able to only view the names (and
 * database id) of, and add members, or also modify and delete members, or view
 * their other details (date of birth and group). (Check the manual under
 * Administrator Permissions for specific information on the permissions.)
 * Before calling this panel the code should check whether the permission
 * <code>member_list</code> is set.
 * 
 * @author Andrzej JR Hunt
 * 
 */
public class MemberManagementPanel extends AbstractTableModel implements
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
	private ResourceBundle locCommon = ResourceBundle.getBundle("common");

	/** The database we are working upon. */
	private Database db;

	/**
	 * The list of members currently in the database. Is updated after any
	 * changes. Use this to access any data as opposed to requesting from the
	 * database.
	 */
	private MemberInfo[] members;

	/** The panel containing all the graphical components. */
	private JPanel displayPanel = new JPanel();

	private JButton addMemberButton = new JButton();
	private JButton editMemberButton = new JButton();
	private JButton deleteMemberButton = new JButton();

	/** Table displaying the list of members. */
	private JTable memberTable;
	private JScrollPane memberTablePane;

	/** Dialog allowing editing of members. */
	private EditMemberDialog memberDialog;

	/** Dialog allowing deletion of members. */
	private DeleteMemberDialog deleteMemberDialog;

	/**
	 * Create the MemberManagementPanel, allowing access to and modification of
	 * the list of members currently in the database.
	 * 
	 * @param db
	 *            The database to be used.
	 * @param admin
	 *            The current administrator accessing the panel (used to
	 *            determine permissions).
	 */
	public MemberManagementPanel(Database db, AdminInfo admin) {
		super();
		this.db = db;
		this.admin = admin;

		// Setup the editing dialog (used throughout)
		memberDialog = new EditMemberDialog(db);
		deleteMemberDialog = new DeleteMemberDialog(db);

		memberTable = new JTable(this);
		memberTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		memberTablePane = new JScrollPane(memberTable);
		memberTablePane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		memberTablePane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		// Listeners to detect when someone wants to edit (for double clicks on
		// table).
		memberTable.addMouseListener(this);
		memberTablePane.addMouseListener(this);

		// Setup the display
		addMemberButton.setText(loc.getString("member.add"));
		editMemberButton.setText(loc.getString("member.edit"));
		deleteMemberButton.setText(loc.getString("member.delete"));

		// Listeners for the buttons
		addMemberButton.addActionListener(this);
		editMemberButton.addActionListener(this);
		deleteMemberButton.addActionListener(this);

		// Disable functions this admin can't use.
		if (admin.getPermissionList().isPermissionSet("member_list.modify")) {
			editMemberButton.setEnabled(true);
		} else {
			editMemberButton.setEnabled(false);
		}
		if (admin.getPermissionList().isPermissionSet("member_list.remove")) {
			deleteMemberButton.setEnabled(true);
		} else {
			deleteMemberButton.setEnabled(false);
		}

		// Layouting
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
		// Get the data loaded in from the db.
		updateMembers();
	}

	/**
	 * The columns in the table.
	 */
	private String[] columnNames = { loc.getString("member.id"),
			locCommon.getString("name"), locCommon.getString("dob"),
			locCommon.getString("group") };

	/**
	 * {@inheritDoc}
	 */
	public int getColumnCount() {
		// No details on members if not allowed, i.e only show id and name
		// columns.
		if (!admin.getPermissionList().isPermissionSet("member_list.details"))
			return 2;
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
		return members.length;
	}

	/**
	 * Edit the member in a specific row by calling the EditMemberDialog. Only
	 * works if the current admin has the required permissions.
	 * 
	 * @param row
	 *            The row in which the member is on the table.
	 */
	private void editMemberAt(int row) {
		// Exit if not allowed.
		if (!admin.getPermissionList().isPermissionSet("member_list.modify"))
			return;
		memberDialog.editMember(members[row]);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getValueAt(int row, int col) {
		switch (col) {
		case 0:
			return members[row].getId();
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void apply() {
		// We don't need to do anything here since everything autosaves.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (arg0.getClickCount() != 2) {
			return; // Return unless we have a double click
		}
		if (arg0.getSource() == memberTablePane) { // A click on blank area
			memberDialog.addMember();
		} else if (arg0.getSource() == memberTable) {
			if (memberTable.getSelectedRow() >= 0)
				editMemberAt(memberTable.getSelectedRow());
		}
		updateMembers();

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
		if (arg0.getSource() == addMemberButton) {
			memberDialog.addMember();
		} else if (arg0.getSource() == editMemberButton) {
			if (memberTable.getSelectedRow() >= 0)
				editMemberAt(memberTable.getSelectedRow());
		} else if (arg0.getSource() == deleteMemberButton) {
			if (memberTable.getSelectedRow() >= 0)
				deleteMemberDialog.deleteMember(members[memberTable
						.getSelectedRow()]);
		}
		updateMembers();
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
		return loc.getString("dialog.conf.edit_members.title");
	}

}
