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
 *	06/08/2010:	Created on the basis of AddMemberDialog.
 */
package org.ahunt.simpleRowLog.admin;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.ahunt.simpleRowLog.common.InvalidDataException;
import org.ahunt.simpleRowLog.common.ErrorHandler;
import org.ahunt.simpleRowLog.common.GroupInfo;
import org.ahunt.simpleRowLog.common.MemberInfo;
import org.ahunt.simpleRowLog.common.Util;
import org.ahunt.simpleRowLog.conf.Configuration;
import org.ahunt.simpleRowLog.interfaces.Database;

import com.toedter.calendar.JDateChooser;

/**
 * 
 * @author Andrzej JR Hunt
 * 
 */
public class EditMemberDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private enum DIALOG_MODE {
		ADD, EDIT
	};

	private DIALOG_MODE mode;

	private Database db;
	private Configuration conf;

	private int createdMemberId;
	/**
	 * Localisation data.
	 */
	private ResourceBundle loc = ResourceBundle.getBundle("admin");
	private ResourceBundle locCommon = ResourceBundle.getBundle("common");

	private JPanel entryPanel = new JPanel();
	private TitledBorder entryPanelBorder;
	private JLabel surnameEntryLabel = new JLabel();
	private JTextField surnameEntry = new JTextField(25);
	private JLabel forenameEntryLabel = new JLabel();
	private JTextField forenameEntry = new JTextField(25);
	private JLabel dobEntryLabel = new JLabel();
	private JDateChooser dobEntry = new JDateChooser();
	private JLabel groupSelectorLabel = new JLabel();
	private JComboBox groupSelector = new JComboBox();
	private JButton deleteMemberButton = new JButton();

	/**
	 * The currently available groups on show.
	 */
	private GroupInfo[] groups;

	private JButton cancelButton = new JButton();
	private JButton saveButton = new JButton();

	// The current member being modified (if applicable).
	private MemberInfo member;

	/**
	 * Create and show a new AddMemberDialog.
	 */
	public EditMemberDialog(Database db) {
		super();
		this.db = db;

		try {
			conf = Configuration.getConf("admin");
		} catch (FileNotFoundException e) {
			ErrorHandler.handleError(e);
		}

		entryPanelBorder = new TitledBorder(new LineBorder(Color.BLACK), "");
		entryPanel.setBorder(entryPanelBorder);
		this.setModal(true);

		NameEntryListener nel = new NameEntryListener(surnameEntry);
		surnameEntry.addFocusListener(nel);
		surnameEntry.addMouseListener(nel);
		nel = new NameEntryListener(forenameEntry);
		forenameEntry.addFocusListener(nel);
		forenameEntry.addMouseListener(nel);
		// add(entryPanel); // Temporary
		setupLayout();

		ButtonListener bl = new ButtonListener();
		cancelButton.addActionListener(bl);
		saveButton.addActionListener(bl);
		deleteMemberButton.addActionListener(bl);
		this.getRootPane().setDefaultButton(saveButton);
	}

	private void setupLayout() {
		// General layout
		GroupLayout l = new GroupLayout(this.getContentPane());
		this.getContentPane().setLayout(l);
		l.setAutoCreateGaps(true);
		l.setAutoCreateContainerGaps(true);
		l.setHorizontalGroup(l.createParallelGroup().addComponent(entryPanel)
				.addGroup(
						l.createSequentialGroup().addPreferredGap(
								LayoutStyle.ComponentPlacement.RELATED,
								GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)// .addGap(Short.MAX_VALUE)
								.addComponent(cancelButton).addComponent(
										saveButton)));

		l.setVerticalGroup(l.createSequentialGroup().addComponent(entryPanel)
				.addGroup(l.createParallelGroup()// .addGap(Short.MAX_VALUE)
						.addComponent(cancelButton).addComponent(saveButton)));
		// Now the internal pane
		GroupLayout r = new GroupLayout(entryPanel);
		entryPanel.setLayout(r);
		r.setAutoCreateGaps(true);
		r.setAutoCreateContainerGaps(true);
		r.setHorizontalGroup(r.createSequentialGroup().addGroup(
				r.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(forenameEntryLabel).addComponent(
								surnameEntryLabel).addComponent(dobEntryLabel)
						.addComponent(groupSelectorLabel)).addGroup(
				r.createParallelGroup().addComponent(forenameEntry)
						.addComponent(surnameEntry).addComponent(dobEntry)
						.addComponent(groupSelector).addComponent(
								deleteMemberButton,
								GroupLayout.Alignment.TRAILING)));
		r.setVerticalGroup(r.createSequentialGroup().addGroup(
				r.createParallelGroup().addComponent(forenameEntryLabel)
						.addComponent(forenameEntry)).addGroup(
				r.createParallelGroup().addComponent(surnameEntryLabel)
						.addComponent(surnameEntry)).addGroup(
				r.createParallelGroup().addComponent(dobEntryLabel)
						.addComponent(dobEntry)).addGroup(
				r.createParallelGroup().addComponent(groupSelectorLabel)
						.addComponent(groupSelector)).addComponent(
				deleteMemberButton));
	}

	/**
	 * Set up the group selector by placing all relevant choices in it.
	 */
	private void setupGroupSelector() {
		groupSelector.removeAllItems();
		groups = db.getGroups();
		GroupInfo defaultGroup = db.getDefaultGroup();
		int defaultSelectionPosition = 0;
		String[] groupNames = new String[groups.length];
		for (int i = 0; i < groups.length; i++) {
			groupNames[i] = groups[i].getName();
			groupSelector.addItem(groups[i].getName());
			if (groups[i] == defaultGroup)
				defaultSelectionPosition = i;
		}
		groupSelector.setSelectedIndex(defaultSelectionPosition);
	}

	/**
	 * 
	 * @param surname
	 * @param forename
	 * @return The id for the new member, or 0 if the dialog was cancelled or
	 *         otherwise failed.
	 */
	public int addMember(String surname, String forename) {
		setupGroupSelector();
		mode = DIALOG_MODE.ADD;
		member = null;
		deleteMemberButton.setVisible(false);

		setTitle(loc.getString("member.add"));
		createdMemberId = 0;

		dobEntry.setDateFormatString(conf.getProperty("srl.date_format"));
		updateLocalisation();
		this.pack();
		this.setResizable(false);

		surnameEntry.setText(surname);
		forenameEntry.setText(forename);
		dobEntry.setDate(null);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screenSize.width / 2 - this.getSize().width / 2,
				screenSize.height / 2 - this.getSize().height / 2);

		setVisible(true);

		this.setResizable(true);
		return createdMemberId;
	}

	public int addMember() {
		return addMember(null, null);
	}

	public void editMember(MemberInfo member) {
		setupGroupSelector();
		this.member = member;
		mode = DIALOG_MODE.EDIT;
		setTitle(loc.getString("member.edit"));

		deleteMemberButton.setVisible(true);
		// In case we have one of the undeletable members.
		if (member.getId() == Database.DELETED_MEMBER_ID
				|| member.getId() == Database.GUEST_MEMBER_ID) {
			deleteMemberButton.setEnabled(false);
		} else {
			deleteMemberButton.setEnabled(true);
		}

		dobEntry.setDateFormatString(conf.getProperty("srl.date_format"));
		updateLocalisation();
		this.pack();
		this.setResizable(false);

		surnameEntry.setText(member.getSurname());
		forenameEntry.setText(member.getForename());
		dobEntry.setDate(member.getDob());
		for (int i = 0; i < groups.length; i++) {
			if (member.getGroupInfo().getId() == groups[i].getId()) {
				groupSelector.setSelectedIndex(i);
			}
		}

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screenSize.width / 2 - this.getSize().width / 2,
				screenSize.height / 2 - this.getSize().height / 2);

		setVisible(true);

		this.setResizable(true);
	}

	public void updateLocalisation() {

		entryPanelBorder.setTitle(loc.getString("member.add.entryframe"));

		surnameEntryLabel.setText(locCommon.getString("surname") + ":");
		forenameEntryLabel.setText(locCommon.getString("forename") + ":");
		dobEntryLabel.setText(locCommon.getString("dob") + ":");
		groupSelectorLabel.setText(locCommon.getString("group") + ":");

		deleteMemberButton.setText(loc.getString("member.delete"));

		cancelButton.setText(locCommon.getString("cancel"));
		saveButton.setText(locCommon.getString("save"));
	}

	private class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// Check that all required details are filled in or warn.
			if (arg0.getSource() == saveButton) {
				if (surnameEntry.getText().length() == 0
						|| dobEntry.getDate() == null) {
					JOptionPane.showMessageDialog(null, loc
							.getString("member.add.missing_details"), loc
							.getString("member.add.missing_details.title"),
							JOptionPane.WARNING_MESSAGE);
					return;
				}
			}
			if (arg0.getSource() == cancelButton) {
				setVisible(false);
			} else if (arg0.getSource() == saveButton
					&& mode == DIALOG_MODE.ADD) {
				// surname = Util.capitaliseName(surname);
				// forename = Util.capitaliseName(forename);
				// TODO: listener for fields.
				try {
					createdMemberId = db.addMember(surnameEntry.getText(),
							forenameEntry.getText(), dobEntry.getDate(),
							groups[groupSelector.getSelectedIndex()].getId());
					// Dialog stating success?
					setVisible(false);
				} catch (InvalidDataException e) {
					String message = "<html><table><tr><td width=300 align=\"left\">"
							+ MessageFormat
									.format(
											loc.getString("member.add.exists"),
											"<i>"
													+ MessageFormat
															.format(
																	conf
																			.getProperty("srl.name_format"),
																	surnameEntry
																			.getText(),
																	forenameEntry
																			.getText())
													+ "</i>",
											"<i>"
													+ new SimpleDateFormat(
															conf
																	.getProperty("srl.date_format"))
															.format(dobEntry
																	.getDate())
													+ "</i>")
							+ "</td></tr></table></html>";
					JOptionPane.showMessageDialog(null, message, loc
							.getString("member.add.exists.title"),
							JOptionPane.WARNING_MESSAGE);
					return;
				}
			} else if (arg0.getSource() == saveButton
					&& mode == DIALOG_MODE.EDIT) { // EDIT
				// surname = Util.capitaliseName(surname);
				// forename = Util.capitaliseName(forename);
				// TODO: listener for fields.
				try {
					db.modifyMember(member, surnameEntry.getText(),
							forenameEntry.getText(), dobEntry.getDate(),
							groups[groupSelector.getSelectedIndex()].getId());
					// Dialog stating success?
					setVisible(false);
				} catch (InvalidDataException e) {
					String message = "<html><table><tr><td width=300 align=\"left\">"
							+ MessageFormat
									.format(
											loc.getString("member.add.exists"),
											"<i>"
													+ MessageFormat
															.format(
																	conf
																			.getProperty("srl.name_format"),
																	surnameEntry
																			.getText(),
																	forenameEntry
																			.getText())
													+ "</i>",
											"<i>"
													+ new SimpleDateFormat(
															conf
																	.getProperty("srl.date_format"))
															.format(dobEntry
																	.getDate())
													+ "</i>")
							+ "</td></tr></table></html>";
					JOptionPane.showMessageDialog(null, message, loc
							.getString("member.add.exists.title"),
							JOptionPane.WARNING_MESSAGE);
					return;
				}
			} else if (arg0.getSource() == deleteMemberButton
					&& mode == DIALOG_MODE.EDIT) {
				if (new DeleteMemberDialog(db).deleteMember(member))
					setVisible(false); // Check whether deleted, and hide if
				// true
			}

		}
	}

	/**
	 * Listen to the name entry fields. Once it is completed, it capitalises the
	 * names accordingly. It has to listen to both Keyboard and Mouse events
	 * meaning it is both a FocusListener and MouseListener, since the
	 * FocusListener ignores what the mouse does.
	 * 
	 */
	private class NameEntryListener implements FocusListener, MouseListener {

		private JTextField entry;

		public NameEntryListener(JTextField entry) {
			this.entry = entry;
		}

		@Override
		public void focusGained(FocusEvent arg0) {
			// Do nothing
		}

		@Override
		public void focusLost(FocusEvent arg0) {
			entry.setText(Util.capitaliseName(entry.getText()));
		}

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// Do nothing
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// Do nothing
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			focusLost(null);
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// Do nothing
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// Do nothing
		}
	}
}
