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
 *	07/08/2010:	Created on the basis of EditMemberDialog.
 */
package org.ahunt.simpleRowLog.gui.admin;

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
import java.lang.reflect.Member;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.ahunt.simpleRowLog.common.AdminInfo;
import org.ahunt.simpleRowLog.common.BoatInfo;
import org.ahunt.simpleRowLog.common.EntryAlreadyExistsException;
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
public class EditBoatDialog extends JDialog {

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

	/**
	 * Localisation data.
	 */
	private ResourceBundle loc = ResourceBundle.getBundle("admin");
	private ResourceBundle locCommon = ResourceBundle.getBundle("common");

	private JPanel entryPanel = new JPanel();
	private TitledBorder entryPanelBorder;

	private JLabel nameEntryLabel = new JLabel();
	private JTextField nameEntry = new JTextField(32);

	private JLabel typeEntryLabel = new JLabel();
	private JTextArea typeEntry = new JTextArea(2, 32);

	private JLabel inHouseCheckBoxLabel = new JLabel();
	private JCheckBox inHouseCheckBox = new JCheckBox();

	private JButton cancelButton = new JButton();
	private JButton saveButton = new JButton();

	// The current member being modified (if applicable).
	private BoatInfo boat;

	private AdminInfo admin;

	/**
	 * Create and show a new AddMemberDialog.
	 */
	public EditBoatDialog(Database db, AdminInfo admin) {
		super();
		this.db = db;
		this.admin = admin;

		try {
			conf = Configuration.getConf("admin");
		} catch (FileNotFoundException e) {
			ErrorHandler.handleError(e);
		}

		entryPanelBorder = new TitledBorder(new LineBorder(Color.BLACK), "");
		entryPanel.setBorder(entryPanelBorder);
		this.setModal(true);

		// NameEntryListener nel = new NameEntryListener(surnameEntry);
		// surnameEntry.addFocusListener(nel);
		// surnameEntry.addMouseListener(nel);
		// nel = new NameEntryListener(forenameEntry);
		// forenameEntry.addFocusListener(nel);
		// forenameEntry.addMouseListener(nel);
		// add(entryPanel); // Temporary
		setupLayout();

		ButtonListener bl = new ButtonListener();
		cancelButton.addActionListener(bl);
		saveButton.addActionListener(bl);
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
						.addComponent(nameEntryLabel).addComponent(
								typeEntryLabel).addComponent(
								inHouseCheckBoxLabel)).addGroup(
				r.createParallelGroup().addComponent(nameEntry).addComponent(
						typeEntry).addComponent(inHouseCheckBox)));
		r.setVerticalGroup(r.createSequentialGroup().addGroup(
				r.createParallelGroup().addComponent(nameEntryLabel)
						.addComponent(nameEntry)).addGroup(
				r.createParallelGroup().addComponent(typeEntryLabel)
						.addComponent(typeEntry)).addGroup(
				r.createParallelGroup().addComponent(inHouseCheckBoxLabel)
						.addComponent(inHouseCheckBox)));
	}

	// /**
	// * Set up the group selector by placing all relevant choices in it.
	// */
	// private void setupGroupSelector() {
	// groupSelector.removeAllItems();
	// groups = db.getGroups();
	// GroupInfo defaultGroup = db.getDefaultGroup();
	// int defaultSelectionPosition = 0;
	// String[] groupNames = new String[groups.length];
	// for (int i = 0; i < groups.length; i++) {
	// groupNames[i] = groups[i].getName();
	// groupSelector.addItem(groups[i].getName());
	// if (groups[i] == defaultGroup)
	// defaultSelectionPosition = i;
	// }
	// groupSelector.setSelectedIndex(defaultSelectionPosition);
	// }

	/**
	 * 
	 * @param surname
	 * @param forename
	 * @return The id for the new member, or 0 if the dialog was cancelled or
	 *         otherwise failed.
	 */
	public void addBoat() {
		// setupGroupSelector();
		mode = DIALOG_MODE.ADD;
		boat = null;

		setTitle(loc.getString("boat.add"));

		updateLocalisation();
		this.pack();
		this.setResizable(false);

		nameEntry.setText("");
		typeEntry.setText("");
		inHouseCheckBox.setSelected(true);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screenSize.width / 2 - this.getSize().width / 2,
				screenSize.height / 2 - this.getSize().height / 2);

		setVisible(true);

		this.setResizable(true);
	}

	public void editBoat(BoatInfo boat) {
		// setupGroupSelector();
		this.boat = boat;
		mode = DIALOG_MODE.EDIT;
		setTitle(loc.getString("boat.edit"));

		updateLocalisation();
		this.pack();
		this.setResizable(false);

		nameEntry.setText(boat.getName());
		typeEntry.setText(boat.getType());
		inHouseCheckBox.setSelected(boat.inHouse());

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screenSize.width / 2 - this.getSize().width / 2,
				screenSize.height / 2 - this.getSize().height / 2);

		setVisible(true);

		this.setResizable(true);
	}

	public void updateLocalisation() {

		entryPanelBorder.setTitle(loc.getString("boat.add.entryframe"));

		nameEntryLabel.setText(loc.getString("boat.name") + ":");
		typeEntryLabel.setText(loc.getString("boat.type") + ":");
		inHouseCheckBox.setText(loc.getString("boat.in_house"));

		cancelButton.setText(locCommon.getString("cancel"));
		saveButton.setText(locCommon.getString("save"));
	}

	private class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (arg0.getSource() == saveButton) {
				if (nameEntry.getText().length() == 0) {
					JOptionPane.showMessageDialog(null, loc
							.getString("boat.add.no_name"), loc
							.getString("boat.add.no_name.title"),
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
					db.addBoat(nameEntry.getText(), typeEntry.getText(),
							inHouseCheckBox.isSelected());
					setVisible(false);
				} catch (Exception e) {
					// TODO: process, check that the exceptions can be thrown in
					// db. EntryAlreadyExistsException
					// String message =
					// "<html><table><tr><td width=300 align=\"left\">"
					// + MessageFormat
					// .format(
					// loc.getString("member.add.exists"),
					// "<i>"
					// + MessageFormat
					// .format(
					// conf
					// .getProperty("srl.name_format"),
					// surnameEntry
					// .getText(),
					// forenameEntry
					// .getText())
					// + "</i>",
					// "<i>"
					// + new SimpleDateFormat(
					// conf
					// .getProperty("srl.date_format"))
					// .format(dobEntry
					// .getDate())
					// + "</i>")
					// + "</td></tr></table></html>";
					// JOptionPane.showMessageDialog(null, message, loc
					// .getString("member.add.exists.title"),
					// JOptionPane.ERROR_MESSAGE);
				}
			} else if (arg0.getSource() == saveButton
					&& mode == DIALOG_MODE.EDIT) { // EDIT
				// surname = Util.capitaliseName(surname);
				// forename = Util.capitaliseName(forename);
				// TODO: listener for fields.
				try {
					db.modifyBoat(boat, nameEntry.getText(), typeEntry
							.getText(), inHouseCheckBox.isSelected());
					setVisible(false);
				} catch (Exception e) {
					// TODO: check that this can be thrown etc. and process:
					// EntryAlreadyExistsException
					// String message =
					// "<html><table><tr><td width=300 align=\"left\">"
					// + MessageFormat
					// .format(
					// loc.getString("member.add.exists"),
					// "<i>"
					// + MessageFormat
					// .format(
					// conf
					// .getProperty("srl.name_format"),
					// surnameEntry
					// .getText(),
					// forenameEntry
					// .getText())
					// + "</i>",
					// "<i>"
					// + new SimpleDateFormat(
					// conf
					// .getProperty("srl.date_format"))
					// .format(dobEntry
					// .getDate())
					// + "</i>")
					// + "</td></tr></table></html>";
					// JOptionPane.showMessageDialog(null, message, loc
					// .getString("member.add.exists.title"),
					// JOptionPane.ERROR_MESSAGE);
				}
			}

		}
	}

}
