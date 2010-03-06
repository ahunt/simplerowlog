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
 *	Changelog
 *	06/03/2010: Renamed from MemberDialog to AddMemberDialog since only the
 *				admin can edit members, i.e. this dialog is only used to add
 *				them.
 *	31/01/2010:	Created.
 */

package org.ahunt.simpleRowLog.gui.simpleGUI;

import java.awt.Color;
import java.util.ResourceBundle;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.ahunt.simpleRowLog.common.MemberInfo;
import org.ahunt.simpleRowLog.interfaces.Database;

/**
 * Dialog allowing the adding of members to the database.
 * 
 * @author Andrzej JR Hunt
 * 
 */
public class AddMemberDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Database db;
	
	/**
	 * Localisation data.
	 */
	private ResourceBundle loc = ResourceBundle.getBundle("gui");
	private ResourceBundle locCommon = ResourceBundle.getBundle("common");

	private JPanel entryPanel = new JPanel();
	private TitledBorder entryPanelBorder;
	private JLabel surnameEntryLabel = new JLabel();
	private JTextField surnameEntry = new JTextField(25);
	private JLabel forenameEntryLabel = new JLabel();
	private JTextField forenameEntry = new JTextField(25);
	private JLabel dobEntryLabel = new JLabel();
	private JTextField dobEntry = new JTextField(15);

	private JButton cancelButton = new JButton();
	private JButton saveButton = new JButton();

	/**
	 * Create and show a new AddMemberDialog.
	 */
	public AddMemberDialog(Database db) {
		super();
		this.db = db;
		entryPanelBorder = new TitledBorder(new LineBorder(Color.BLACK), "");
		entryPanel.setBorder(entryPanelBorder);
		this.setModal(true);
		setupLayout();
		add(entryPanel); // Temporary
	}

	private void setupLayout() {
		GroupLayout r = new GroupLayout(entryPanel); // Layouting
		entryPanel.setLayout(r);
		r.setAutoCreateGaps(true);
		r.setAutoCreateContainerGaps(true);
		r.setHorizontalGroup(r.createSequentialGroup().addGroup(
				r.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(forenameEntryLabel)
						.addComponent(surnameEntryLabel).addComponent(
								dobEntryLabel)).addGroup(
				r.createParallelGroup().addComponent(forenameEntry)
						.addComponent(surnameEntry).addComponent(dobEntry)));
		r.setVerticalGroup(r.createSequentialGroup().addGroup(
				r.createParallelGroup().addComponent(forenameEntryLabel)
						.addComponent(forenameEntry)).addGroup(
				r.createParallelGroup().addComponent(surnameEntryLabel)
						.addComponent(surnameEntry)).addGroup(
				r.createParallelGroup().addComponent(dobEntryLabel)
						.addComponent(dobEntry)));
	}

	/**
	 * 
	 * @param surname
	 * @param forename
	 * @return The information for the new member, or null if the dialog was
	 *         cancelled or otherwise failed.
	 */
	public MemberInfo addMember(String surname, String forename) {
		updateLocalisation();
		this.pack();
		this.setResizable(false);
		setVisible(true);
		this.setResizable(true);
		return null;
	}
	
	public MemberInfo addMember() {
		addMember(null, null);
		return null;
	}

	public void updateLocalisation() {
		setTitle(loc.getString("addMember.title"));
		entryPanelBorder.setTitle(loc.getString("addMember.entryFrame"));

		surnameEntryLabel.setText(locCommon.getString("surname") + ":");
		forenameEntryLabel.setText(locCommon.getString("forename") + ":");
		dobEntryLabel.setText(locCommon.getString("dob") + ":");
	}
}
