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
 *	04/10/2010:	Created on the basis of DeleteMemberDialog.
 */
package org.ahunt.simpleRowLog.gui.admin;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.ahunt.simpleRowLog.common.AdminInfo;
import org.ahunt.simpleRowLog.common.BoatInfo;
import org.ahunt.simpleRowLog.common.ErrorHandler;
import org.ahunt.simpleRowLog.common.MemberInfo;
import org.ahunt.simpleRowLog.conf.Configuration;
import org.ahunt.simpleRowLog.interfaces.Database;

/**
 * 
 * @author Andrzej JR Hunt
 * 
 */
public class DeleteBoatDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Database db;
	private Configuration conf;

	/**
	 * Localisation data.
	 */
	private ResourceBundle loc = ResourceBundle.getBundle("admin");
	private ResourceBundle locCommon = ResourceBundle.getBundle("common");

	private JLabel deletionInfoLabel = new JLabel();

	private JPanel entryPanel = new JPanel();

	private JLabel replacementSelectionLabel = new JLabel();
	private JComboBox replacementSelection = new JComboBox();

	private JButton selectDeletedMemberButton = new JButton();

	private JButton cancelButton = new JButton();
	private JButton deleteButton = new JButton();

	// The current member being modified (if applicable).
	private BoatInfo boat;

	private BoatInfo[] boats;
	private int otherBoatIndex;

	/**
	 * Create and show a new AddMemberDialog.
	 */
	public DeleteBoatDialog(Database db) {
		super();
		this.db = db;

		try {
			conf = Configuration.getConf("admin");
		} catch (FileNotFoundException e) {
			ErrorHandler.handleError(e);
		}

		entryPanel.setBorder(new LineBorder(Color.BLACK));
		this.setModal(true);

		setupLayout();

		ButtonListener bl = new ButtonListener();
		cancelButton.addActionListener(bl);
		deleteButton.addActionListener(bl);
		selectDeletedMemberButton.addActionListener(bl);
		this.getRootPane().setDefaultButton(deleteButton);
	}

	private void setupLayout() {
		// General layout
		GroupLayout l = new GroupLayout(this.getContentPane());
		this.getContentPane().setLayout(l);
		l.setAutoCreateGaps(true);
		l.setAutoCreateContainerGaps(true);
		l
				.setHorizontalGroup(l.createParallelGroup().addComponent(
						deletionInfoLabel).addComponent(entryPanel).addGroup(
						l.createSequentialGroup().addPreferredGap(
								LayoutStyle.ComponentPlacement.RELATED,
								GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)// .addGap(Short.MAX_VALUE)
								.addComponent(cancelButton).addComponent(
										deleteButton)));

		l
				.setVerticalGroup(l.createSequentialGroup().addComponent(
						deletionInfoLabel).addComponent(entryPanel).addGroup(
						l.createParallelGroup()// .addGap(Short.MAX_VALUE)
								.addComponent(cancelButton).addComponent(
										deleteButton)));
		// Now the internal pane
		GroupLayout r = new GroupLayout(entryPanel);
		entryPanel.setLayout(r);
		r.setAutoCreateGaps(true);
		r.setAutoCreateContainerGaps(true);
		r.setHorizontalGroup(r.createParallelGroup().addComponent(
				replacementSelectionLabel).addComponent(replacementSelection)
				.addComponent(selectDeletedMemberButton)

		);
		r.setVerticalGroup(r.createSequentialGroup().addComponent(
				replacementSelectionLabel).addComponent(replacementSelection)
				.addComponent(selectDeletedMemberButton));
	}

	/**
	 * 
	 * @param surname
	 * @param forename
	 * @return The id for the new member, or 0 if the dialog was cancelled or
	 *         otherwise failed.
	 */
	public void deleteMember(BoatInfo boat) {
		this.boat = boat;



		updateLocalisation();
		this.setResizable(true);
		this.pack();
		this.setResizable(false);

		// Set up the input
		replacementSelection.removeAllItems();
		boats = db.getBoats();
		BoatInfo[] bi = new BoatInfo[boats.length - 1];
		int pos = 0;
		for (BoatInfo b : boats) {
			if (b.getId() != boat.getId()) {
				bi[pos] = b;
				replacementSelection.addItem(b.getName());
				if (b.getId() == Database.OTHER_BOAT_ID) {
					otherBoatIndex = replacementSelection.getItemCount() - 1;
				}
				pos++;
			}
		}
		boats = bi;
		replacementSelection.setSelectedIndex(otherBoatIndex);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screenSize.width / 2 - this.getSize().width / 2,
				screenSize.height / 2 - this.getSize().height / 2);

		setVisible(true);

		this.setResizable(true);
		
		// We free the memory as far as possible.
		boat = null;
		boats = null;
	}

	public void updateLocalisation() {
		setTitle(loc.getString("boat.delete"));
		deletionInfoLabel.setText(MessageFormat.format(loc
				.getString("boat.delete.description"), boat.getName()));

		replacementSelectionLabel.setText(loc
				.getString("boat.delete.replacement")
				+ ":");
		selectDeletedMemberButton.setText(loc
				.getString("boat.delete.other_boat"));

		cancelButton.setText(locCommon.getString("cancel"));
		deleteButton.setText(loc.getString("boat.delete.do"));
	}

	private class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (arg0.getSource() == selectDeletedMemberButton) {
				replacementSelection.setSelectedIndex(otherBoatIndex);
			}

			if (arg0.getSource() == cancelButton) {
				setVisible(false);
			} else if (arg0.getSource() == deleteButton) {
				try {
					// TODO: provide warning that this can take a while.
					db.removeBoat(boat, boats[replacementSelection
							.getSelectedIndex()]);
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
			}

		}
	}

}
