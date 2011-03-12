/*
 *    This file is part of simple rowLog: the open rowing logbook.
 *    Copyright (C) 2011  Andrzej JR Hunt
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
 *	21/01/2011: Created.
 */
package org.ahunt.simpleRowLog.db.simpleDB;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Arrays;
import java.util.ResourceBundle;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;

import org.ahunt.simpleRowLog.common.DatabaseError;
import org.ahunt.simpleRowLog.common.InvalidDataException;

/**
 * This is the dialog shown to the user when setting up the database, asking for
 * default data. Currently this is only the name and password for the default
 * admin.
 * 
 * @author Andrzej J.R. Hunt
 * 
 */
public class SetupDialog extends JDialog {

	ResourceBundle loc = ResourceBundle.getBundle("db");
	private ResourceBundle locAdmin = ResourceBundle.getBundle("admin");
	private ResourceBundle locCommon = ResourceBundle.getBundle("common");

	public SetupDialog(final Database db) {
		super();
		this.setModal(true);
		this.setTitle(loc.getString("setup.dialog_title"));

		final JTextField usernameEntry = new JTextField(25);
		final JTextField nameEntry = new JTextField(25);
		final JPasswordField passwordEntry = new JPasswordField(35);
		final JPasswordField passwordConfirmation = new JPasswordField();

		JLabel usernameEntryLabel = new JLabel("<html><b>"
				+ locAdmin.getString("admin.username") + ":</b></html>");
		JLabel nameEntryLabel = new JLabel("<html><b>"
				+ locCommon.getString("name") + ":</b></html>");
		JLabel passwordEntryLabel = new JLabel("<html><b>"
				+ locAdmin.getString("admin.password_entry.add")
				+ "</b></html>");
		JLabel passwordConfirmationLabel = new JLabel("<html><b>"
				+ locAdmin.getString("admin.password_confirmation.add")
				+ "</b></html>");

		// Tooltips
		usernameEntryLabel.setToolTipText(loc
				.getString("setup.username.tooltip"));

		final JLabel dialogDescription = new JLabel("<html>"
				+ loc.getString("setup.dialog_description")
				+ "<br/><br/><br/></html>");

		JButton saveButton = new JButton(loc.getString("setup.save"));
		JButton cancelButton = new JButton(loc.getString("setup.cancel"));

		GroupLayout l = new GroupLayout(this.getContentPane());
		this.getContentPane().setLayout(l);

		l.setAutoCreateContainerGaps(true);

		l.setHorizontalGroup(l.createParallelGroup().addComponent(
				dialogDescription).addGroup(
				l.createSequentialGroup().addGroup(
						l.createParallelGroup()
								.addComponent(usernameEntryLabel).addComponent(
										nameEntryLabel).addComponent(
										passwordEntryLabel).addComponent(
										passwordConfirmationLabel)).addGroup(
						l.createParallelGroup(GroupLayout.Alignment.TRAILING)
								.addComponent(usernameEntry).addComponent(
										nameEntry).addComponent(passwordEntry)
								.addComponent(passwordConfirmation).addGroup(
										l.createSequentialGroup().addComponent(
												cancelButton).addComponent(
												saveButton)))));
		l
				.setVerticalGroup(l
						.createSequentialGroup()
						.addComponent(dialogDescription)
						.addGroup(
								l.createParallelGroup(Alignment.BASELINE)
										.addComponent(usernameEntryLabel)
										.addComponent(usernameEntry))
						.addGroup(
								l.createParallelGroup(Alignment.BASELINE)
										.addComponent(nameEntryLabel)
										.addComponent(nameEntry))
						.addGroup(
								l.createParallelGroup(Alignment.BASELINE)
										.addComponent(passwordEntryLabel)
										.addComponent(passwordEntry))
						.addGroup(
								l
										.createParallelGroup(Alignment.BASELINE)
										.addComponent(passwordConfirmationLabel)
										.addComponent(passwordConfirmation))
						.addGroup(
								l.createParallelGroup(Alignment.BASELINE)
										.addComponent(cancelButton)
										.addComponent(saveButton)));

		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				db.delete();
				System.exit(0);
			}
		});

		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Exit if the password aren't the same, or no admin name.
				if (!Arrays.equals(passwordEntry.getPassword(),
						passwordConfirmation.getPassword())
						|| usernameEntry.getText().length() == 0
						|| passwordEntry.getPassword().length == 0) {
					dialogDescription
							.setText("<html>"
									+ loc.getString("setup.dialog_description")
									+ "<br/><br/><font color=\"red\">"
									+ loc.getString("setup.invalid")
									+ "</font></html>");
					return;

				}
				try {
					db.addAdmin(usernameEntry.getText(), passwordEntry
							.getPassword(), nameEntry.getText(), true, "");
					setVisible(false);
				} catch (DatabaseError e1) {
					// TODO : implement.
				} catch (InvalidDataException e1) {
					// TODO : implement.
				}
			}
		});

		this.setModal(true);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				db.delete();
				System.exit(0);
			}
		});

		this.pack();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screenSize.width / 2 - this.getSize().width / 2,
				screenSize.height / 2 - this.getSize().height / 2);
		this.getRootPane().setDefaultButton(saveButton);
		this.setVisible(true);
	}

}
