/*
 *    This file is part of simple rowLog: the open rowing logbook.
 *    Copyright (C) 2010, 2011  Andrzej JR Hunt
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
 *	06/01/2011: Actually did the work on it.
 *	23/12/2010:	Created from MemberManagementPanel.
 */
package org.ahunt.simpleRowLog.admin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.AbstractTableModel;

import org.ahunt.simpleRowLog.common.BoatInfo;
import org.ahunt.simpleRowLog.common.ErrorHandler;
import org.ahunt.simpleRowLog.common.MemberInfo;
import org.ahunt.simpleRowLog.common.OutingInfo;
import org.ahunt.simpleRowLog.conf.Configuration;
import org.ahunt.simpleRowLog.interfaces.Database;

import com.toedter.calendar.JDateChooser;

/**
 * Panel allowing access to the list of outings. Depending on the permissions
 * the administrator has, they may either be able to only view the outings, add
 * outings, modify outings, or even delete outings. (Check the manual under
 * Administrator Permissions for specific information on the permissions.)
 * Before calling this panel the code should check whether the permission
 * <code>outings_list</code> is set.
 * 
 * @author Andrzej JR Hunt
 * 
 */
public class OutingManagementPanel extends AbstractTableModel implements
		ConfigPanelInterface, MouseListener, ActionListener {

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
	private ResourceBundle locGUI = ResourceBundle.getBundle("gui");
	private Configuration conf;

	/** The database we are working upon. */
	private Database db;

	/**
	 * The list of members currently in the database. Is updated after any
	 * changes. Use this to access any data as opposed to requesting from the
	 * database.
	 */
	private OutingInfo[] outings = new OutingInfo[0];

	/** The panel containing all the graphical components. */
	private JPanel displayPanel = new JPanel();

	private JButton addOutingButton = new JButton();
	private JButton editOutingButton = new JButton();
	private JButton deleteOutingButton = new JButton();

	/** Table displaying the list of outings. */
	private JTable outingTable;
	private JScrollPane outingTablePane;

	/** Dialog allowing editing of members. */
	private EditOutingDialog outingDialog;

	// The filter panel
	private JPanel filterPanel = new JPanel();
	private JComboBox filterBoatSelector = new JComboBox();
	private JComboBox filterMemberSelector = new JComboBox();
	private JDateChooser filterStartDate = new JDateChooser();
	private JDateChooser filterEndDate = new JDateChooser();

	private JLabel filterBoatSelectorLabel = new JLabel();
	private JLabel filterMemberSelectorLabel = new JLabel();
	private JLabel filterStartDateLabel = new JLabel();
	private JLabel filterEndDateLabel = new JLabel();

	private JButton filterResetButton = new JButton();
	private JButton filterApplyButton = new JButton();
	private JLabel filterCurrentLabel = new JLabel();

	private BoatInfo[] boats;

	private MemberInfo[] members;

	// private
	// TODO: implement a way of filtering outings in the database, according
	// to ranges of days/months/years...

	// /** Dialog allowing deletion of members. */
	// private DeleteMemberDialog deleteMemberDialog;

	/**
	 * Create the OutingManagementPanel, allowing access to and modification of
	 * the list of outings currently in the database.
	 * 
	 * @param db
	 *            The database to be used.
	 * @param admin
	 *            The current administrator accessing the panel (used to
	 *            determine permissions).
	 */
	public OutingManagementPanel(final Database db, AdminInfo admin) {
		super();
		this.db = db;
		this.admin = admin;

		// Setup the editing dialog (used throughout)
		outingDialog = new EditOutingDialog(db);
		// deleteMemberDialog = new DeleteMemberDialog(db);

		outingTable = new JTable(this);
		outingTable.setRowHeight(100);
		outingTable.getColumnModel().getColumn(0).setPreferredWidth(10); // ID
		outingTable.getColumnModel().getColumn(1).setPreferredWidth(50); // Dat
		outingTable.getColumnModel().getColumn(2).setPreferredWidth(50); // Boat
		outingTable.getColumnModel().getColumn(3).setPreferredWidth(200); // Crew
		outingTable.getColumnModel().getColumn(4).setPreferredWidth(20); // Time
		outingTable.getColumnModel().getColumn(5).setPreferredWidth(20); // Time
		outingTable.getColumnModel().getColumn(6).setPreferredWidth(10); // Distance

		outingTablePane = new JScrollPane(outingTable);
		outingTablePane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		outingTablePane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		// Listeners to detect when someone wants to edit (for double clicks on
		// table).
		outingTable.addMouseListener(this);
		outingTablePane.addMouseListener(this);

		// Setup the display
		addOutingButton.setText(locAdmin.getString("outing.add"));
		editOutingButton.setText(locAdmin.getString("outing.edit"));
		deleteOutingButton.setText(locAdmin.getString("outing.delete"));

		// Listeners for the buttons
		addOutingButton.addActionListener(this);
		editOutingButton.addActionListener(this);
		deleteOutingButton.addActionListener(this);

		try {
			conf = Configuration.getConf("simpleGUI");
		} catch (FileNotFoundException e) {
			ErrorHandler.handleError(e);
		}

		// Disable functions this admin can't use.
		if (admin.getPermissionList().isPermissionSet("outing_list.modify")) {
			editOutingButton.setEnabled(true);
		} else {
			editOutingButton.setEnabled(false);
		}
		if (admin.getPermissionList().isPermissionSet("outing_list.remove")) {
			deleteOutingButton.setEnabled(true);
		} else {
			deleteOutingButton.setEnabled(false);
		}

		// Layouting
		setupFilterPanel();
		GroupLayout l = new GroupLayout(displayPanel);
		displayPanel.setLayout(l);
		l.setAutoCreateGaps(true);
		l.setAutoCreateContainerGaps(true);
		l.setVerticalGroup(l.createSequentialGroup().addComponent(filterPanel)
				.addComponent(outingTablePane).addGroup(
						l.createParallelGroup().addComponent(addOutingButton)
								.addComponent(editOutingButton).addComponent(
										deleteOutingButton)));
		l.setHorizontalGroup(l.createParallelGroup().addComponent(filterPanel)
				.addComponent(outingTablePane).addGroup(
						l.createSequentialGroup().addPreferredGap(
								LayoutStyle.ComponentPlacement.RELATED,
								GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(addOutingButton).addComponent(
										editOutingButton).addComponent(
										deleteOutingButton)));
		// Get the data loaded in from the db.
		displayPanel.addComponentListener(new ComponentListener() {

			@Override
			public void componentHidden(ComponentEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void componentMoved(ComponentEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void componentResized(ComponentEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void componentShown(ComponentEvent e) {
				// Reload all the data that could have changed.
				boats = db.getBoats();
				filterBoatSelector.removeAllItems();
				filterBoatSelector.addItem(locAdmin
						.getString("outing.filter.no_boat_selected"));
				for (BoatInfo b : boats) {
					filterBoatSelector.addItem(b.getName());
				}
				members = db.getMembers();
				filterMemberSelector.removeAllItems();
				filterMemberSelector.addItem(locAdmin
						.getString("outing.filter.no_member_selected"));
				for (MemberInfo m : members) {
					filterMemberSelector.addItem(m.getName());
				}
				applyFilter();
			}

		});
		// Code to apply the filter.
		filterApplyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applyFilter();
			}
		});
		filterResetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				resetFilter();
			}
		});
	}

	private JPanel setupFilterPanel() {

		filterStartDate.setDate(new Date());
		filterEndDate.setDate(new Date());

		filterStartDateLabel.setText(locAdmin
				.getString("outing.filter.startDate"));
		filterEndDateLabel.setText(locAdmin.getString("outing.filter.endDate"));
		filterBoatSelectorLabel.setText(locAdmin
				.getString("outing.filter.boat"));
		filterMemberSelectorLabel.setText(locAdmin
				.getString("outing.filter.member"));

		filterApplyButton.setText(locAdmin.getString("outing.filter.apply"));
		filterResetButton.setText(locAdmin.getString("outing.filter.reset"));

		GroupLayout l = new GroupLayout(filterPanel);
		filterPanel.setLayout(l);
		l.setAutoCreateGaps(true);
		l.setAutoCreateContainerGaps(true);
		l.setHorizontalGroup(l.createParallelGroup().addGroup(
				l.createSequentialGroup().addGroup(
						l.createParallelGroup().addComponent(
								filterBoatSelectorLabel).addComponent(
								filterMemberSelectorLabel)).addGroup(
						l.createParallelGroup()
								.addComponent(filterBoatSelector).addComponent(
										filterMemberSelector)).addGroup(
						l.createParallelGroup().addComponent(
								filterStartDateLabel).addComponent(
								filterEndDateLabel)).addGroup(
						l.createParallelGroup().addComponent(filterStartDate)
								.addComponent(filterEndDate))).addGroup(
				GroupLayout.Alignment.TRAILING,
				l.createSequentialGroup().addComponent(filterCurrentLabel)
						.addComponent(filterResetButton).addComponent(
								filterApplyButton)));
		l
				.setVerticalGroup(l
						.createSequentialGroup()
						.addGroup(
								l
										.createParallelGroup(
												GroupLayout.Alignment.BASELINE)
										.addGroup(
												l
														.createSequentialGroup()
														.addGroup(
																l
																		.createParallelGroup(
																				GroupLayout.Alignment.BASELINE)
																		.addComponent(
																				filterBoatSelectorLabel)
																		.addComponent(
																				filterBoatSelector))
														.addGroup(
																l
																		.createParallelGroup(
																				GroupLayout.Alignment.BASELINE)
																		.addComponent(
																				filterMemberSelectorLabel)
																		.addComponent(
																				filterMemberSelector)))
										.addGroup(
												l
														.createSequentialGroup()
														.addGroup(
																l
																		.createParallelGroup(
																				GroupLayout.Alignment.BASELINE)
																		.addComponent(
																				filterStartDateLabel)
																		.addComponent(
																				filterStartDate))
														.addGroup(
																l
																		.createParallelGroup(
																				GroupLayout.Alignment.BASELINE)
																		.addComponent(
																				filterEndDateLabel)
																		.addComponent(
																				filterEndDate))))
						.addGroup(
								l.createParallelGroup(
										GroupLayout.Alignment.BASELINE)
										.addComponent(filterCurrentLabel)
										.addComponent(filterResetButton)
										.addComponent(filterApplyButton)));

		// TODO: make sure to have a -- empty -- selection available.
		return filterPanel;

	}

	/**
	 * The columns in the table.
	 */
	private String[] columnNames = { locAdmin.getString("outing.id"),
			locAdmin.getString("outing.date"), locGUI.getString("outing.boat"),
			locGUI.getString("outing.rowers"),
			locGUI.getString("outing.timeOut"),
			locGUI.getString("outing.timeIn"),
			locGUI.getString("outing.distance"),
			locGUI.getString("outing.destination"),
			locGUI.getString("outing.comment") };

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
		return outings.length;
	}

	/**
	 * Edit the member in a specific row by calling the EditMemberDialog. Only
	 * works if the current admin has the required permissions.
	 * 
	 * @param row
	 *            The row in which the member is on the table.
	 */
	private void editOutingAt(int row) {
		// Exit if not allowed.
		if (!admin.getPermissionList().isPermissionSet("outing_list.modify"))
			return;
		outingDialog.modifyOuting(outings[row]);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getValueAt(int row, int col) {

		// loc.getString("outing.id"),
		// loc.getString("outing.date"), locGUI.getString("outing.boat"),
		// locGUI.getString("outing.rowers"),
		// locGUI.getString("outing.rowers"), locGUI.getString("outing.cox"),
		// locGUI.getString("outing.timeOut"),
		// locGUI.getString("outing.timeIn"),
		// locGUI.getString("outing.distance"),
		// locGUI.getString("outing.destination"),
		// locGUI.getString("outing.comment") };

		switch (col) {
		case 0:
			return outings[row].getId();
		case 1:
			SimpleDateFormat df = new SimpleDateFormat(conf
					.getProperty("srl.date_format"));
			return df.format(outings[row].getDay());
		case 2:
			return outings[row].getBoat().getName();
		case 3: // rowers could be null...
			StringBuffer buff = new StringBuffer();
			// TODO: check whether alignment is correct.
			buff.append("<html><table align=top>");
			MemberInfo[] rowers = outings[row].getRowers();
			// Go through four rows.
			for (short i = 0; i < 4; i++) {
				// Get rower 1 - 4 if existant.
				if (i < rowers.length && rowers[i] != null) {
					buff.append("<tr><td>");
					buff.append(rowers[i].getName());
					buff.append("</td>");
					// Get rower 5-8 if existant. (1 and 5 in same row.)
					if (i < rowers.length + 4 && rowers[i + 4] != null) {
						buff.append("<td>");
						buff.append(rowers[i + 4].getName());
						buff.append("</td>");
					}
					buff.append("</tr>");
				}
			}
			if (outings[row].getCox() != null) {
				buff.append("<tr><td><i>" + outings[row].getCox().getName()
						+ "</i></td</tr");
			}
			buff.append("</table></html>");
			return buff.toString();
		case 4:
			SimpleDateFormat df1 = new SimpleDateFormat(conf
					.getProperty("time_format_outings"));
			return df1.format(outings[row].getOut());
		case 5:
			if (outings[row].getIn() != null) {
				SimpleDateFormat df2 = new SimpleDateFormat(conf
						.getProperty("time_format_outings"));
				return df2.format(outings[row].getIn());
			}
		case 6:
			return outings[row].getDistance();
		case 7:
			return outings[row].getDestination();
		case 8:
			return outings[row].getComment();
		default:
			return null;
		}
	}

	/**
	 * Resets the filter.
	 */
	private void resetFilter() {
		filterBoatSelector.setSelectedIndex(0);
		filterMemberSelector.setSelectedIndex(0);
		filterStartDate.setDate(new Date());
		filterEndDate.setDate(new Date());
		applyFilter();
	}

	/**
	 * Updates the displayed outings using the database.
	 * 
	 */
	private void applyFilter() {
		try {
			if (filterBoatSelector.getSelectedIndex()
					+ filterMemberSelector.getSelectedIndex() == 0) {
				// No selection
				outings = db.getOutings(filterStartDate.getDate(),
						filterEndDate.getDate());
			} else if (filterMemberSelector.getSelectedIndex() == 0) {
				// Only boat filtering
				outings = db.getOutings(boats[filterBoatSelector
						.getSelectedIndex() - 1], filterStartDate.getDate(),
						filterEndDate.getDate());
			} else if (filterBoatSelector.getSelectedIndex() == 0) {
				// Only member filtering
				outings = db.getOutings(members[filterMemberSelector
						.getSelectedIndex() - 1], filterStartDate.getDate(),
						filterEndDate.getDate());
			} else {
				// Member and boat filtering
				outings = db.getOutings(members[filterMemberSelector
						.getSelectedIndex() - 1], boats[filterBoatSelector
						.getSelectedIndex() - 1], filterStartDate.getDate(),
						filterEndDate.getDate());
			}
			fireTableDataChanged();
		} catch (Exception e) {
			ErrorHandler.handleError(e);
		}
		String boatString;
		String memberString;
		String dateString;
		if (filterBoatSelector.getSelectedIndex() > 0) {
			boatString = MessageFormat.format(locAdmin
					.getString("outing.filter.boat_selected.true"),
					boats[filterBoatSelector.getSelectedIndex() - 1].getName());
		} else {
			boatString = locAdmin
					.getString("outing.filter.boat_selected.false");
		}
		if (filterMemberSelector.getSelectedIndex() > 0) {
			memberString = MessageFormat.format(locAdmin
					.getString("outing.filter.member_selected.true"),
					members[filterMemberSelector.getSelectedIndex() - 1]
							.getName());
		} else {
			memberString = locAdmin
					.getString("outing.filter.member_selected.false");
		}

		Calendar startCal = new GregorianCalendar();
		Calendar endCal = new GregorianCalendar();
		startCal.setTime(filterStartDate.getDate());
		endCal.setTime(filterEndDate.getDate());
		// Test whether or not endDate < startDate
		if ((startCal.get(Calendar.YEAR) == endCal.get(Calendar.YEAR))
				&& (startCal.get(Calendar.MONTH) == endCal.get(Calendar.MONTH))
				&& (startCal.get(Calendar.DAY_OF_MONTH) == endCal
						.get(Calendar.DAY_OF_MONTH))) {
			dateString = MessageFormat.format(locAdmin
					.getString("outing.filter.day.one"), filterStartDate
					.getDate());
		} else {
			dateString = MessageFormat.format(locAdmin
					.getString("outing.filter.day.multiple"), filterStartDate
					.getDate(), filterEndDate.getDate());
		}
		filterCurrentLabel.setText(MessageFormat.format(locAdmin
				.getString("outing.filter.current"), boatString, memberString,
				dateString));
		// MessageFormat.format(locAdmin.getString(outing.filter.day.one),
		// arguments)
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
		if (arg0.getSource() == outingTablePane) { // A click on blank area
			outingDialog.addOuting();
		} else if (arg0.getSource() == outingTable) {
			if (outingTable.getSelectedRow() >= 0)
				editOutingAt(outingTable.getSelectedRow());
		}
		applyFilter();

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
		if (arg0.getSource() == addOutingButton) {
			outingDialog.addOuting();
		} else if (arg0.getSource() == editOutingButton) {
			if (outingTable.getSelectedRow() >= 0)
				editOutingAt(outingTable.getSelectedRow());
		} else if (arg0.getSource() == deleteOutingButton) {
			if (outingTable.getSelectedRow() >= 0) {
				OutingInfo toRemove = outings[outingTable.getSelectedRow()];
				if (JOptionPane.showConfirmDialog(null, locAdmin
						.getString("outing.delete_outing")) == JOptionPane.OK_OPTION)
					try {
						db.removeOuting(toRemove);
					} catch (Exception e) {
						// TODO: deal with the error.
					}
			}
			// TODO: confirmation dialog.
			// deleteMemberDialog.deleteMember(outings[outingTable
			// .getSelectedRow()]);
		}

		applyFilter();
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
		return locAdmin.getString("dialog.conf.edit_outings.title");
	}

}
