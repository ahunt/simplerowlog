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
 *	03/12/2010:	Created on the basis of EditMemberDialog.
 */
package org.ahunt.simpleRowLog.admin;

// TODO: password

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
import java.util.Arrays;
import java.util.ResourceBundle;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.ahunt.simpleRowLog.common.InvalidDataException;
import org.ahunt.simpleRowLog.common.ErrorHandler;
import org.ahunt.simpleRowLog.common.Util;
import org.ahunt.simpleRowLog.conf.Configuration;
import org.ahunt.simpleRowLog.interfaces.Database;

/**
 * 
 * @author Andrzej JR Hunt
 * 
 */
public class EditAdminDialog extends JDialog {

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
	/** The admin currently running the dialog. Used to determine permissions. */
	private AdminInfo creator;

	/**
	 * Localisation data.
	 */
	private ResourceBundle loc = ResourceBundle.getBundle("admin");
	private ResourceBundle locCommon = ResourceBundle.getBundle("common");

	private JPanel entryPanel = new JPanel();
	private TitledBorder entryPanelBorder;
	private JLabel usernameEntryLabel = new JLabel();
	private JTextField usernameEntry = new JTextField(25);
	private JLabel nameEntryLabel = new JLabel();
	private JTextField nameEntry = new JTextField(25);
	private JLabel commentEntryLabel = new JLabel();
	private JTextArea commentEntry = new JTextArea();
	private JLabel makeRootLabel = new JLabel();
	private JCheckBox makeRootCheckBox = new JCheckBox();
	private JLabel passwordEntryLabel = new JLabel();
	private JPasswordField passwordEntry = new JPasswordField(32);
	private JLabel passwordConfirmationLabel = new JLabel();
	private JPasswordField passwordConfirmation = new JPasswordField(32);

	private JCheckBox showPasswordCheckBox = new JCheckBox();

	private JButton deleteAdminButton = new JButton();

	private JButton cancelButton = new JButton();
	private JButton saveButton = new JButton();

	// The current member being modified (if applicable).
	private AdminInfo admin;

	/**
	 * Create and show a new AddMemberDialog.
	 */
	public EditAdminDialog(Database db) {
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

		NameEntryListener nel = new NameEntryListener(nameEntry);
		nameEntry.addFocusListener(nel);
		nameEntry.addMouseListener(nel);

		PasswordEntryListener pel = new PasswordEntryListener();
		passwordEntry.addFocusListener(pel);
		passwordEntry.addMouseListener(pel);
		passwordEntry.getDocument().addDocumentListener(pel);
		passwordConfirmation.addFocusListener(pel);
		passwordConfirmation.addMouseListener(pel);
		passwordConfirmation.getDocument().addDocumentListener(pel);

		setupLayout();

		ButtonListener bl = new ButtonListener();
		cancelButton.addActionListener(bl);
		saveButton.addActionListener(bl);
		deleteAdminButton.addActionListener(bl);
		makeRootCheckBox.addActionListener(bl);
		showPasswordCheckBox.addActionListener(bl);
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
						.addComponent(usernameEntryLabel).addComponent(
								nameEntryLabel).addComponent(commentEntryLabel)
						.addComponent(makeRootLabel).addComponent(
								passwordEntryLabel).addComponent(
								passwordConfirmationLabel)).addGroup(
				r.createParallelGroup().addComponent(usernameEntry)
						.addComponent(nameEntry).addComponent(commentEntry)
						.addComponent(makeRootCheckBox).addComponent(
								passwordEntry).addComponent(
								passwordConfirmation).addComponent(
								showPasswordCheckBox,
								GroupLayout.Alignment.TRAILING).addComponent(
								deleteAdminButton,
								GroupLayout.Alignment.TRAILING)));
		r.setVerticalGroup(r.createSequentialGroup().addGroup(
				r.createParallelGroup().addComponent(usernameEntryLabel)
						.addComponent(usernameEntry)).addGroup(
				r.createParallelGroup().addComponent(nameEntryLabel)
						.addComponent(nameEntry)).addGroup(
				r.createParallelGroup().addComponent(commentEntryLabel)
						.addComponent(commentEntry)).addGroup(
				r.createParallelGroup().addComponent(makeRootLabel)
						.addComponent(makeRootCheckBox)).addGroup(
				r.createParallelGroup().addComponent(passwordEntryLabel)
						.addComponent(passwordEntry)).addGroup(
				r.createParallelGroup().addComponent(passwordConfirmationLabel)
						.addComponent(passwordConfirmation)).addComponent(
				showPasswordCheckBox).addComponent(deleteAdminButton));
	}

	/**
	 * Add a new admin to the database.
	 * 
	 * @return The username of the new admin, or null if the dialog was
	 *         cancelled or otherwise failed.
	 * @param creator
	 *            The admin currently logged in. Is used to determine
	 *            permissions regarding setting root etc.
	 */
	public void addAdmin(AdminInfo creator) {
		this.creator = creator;
		mode = DIALOG_MODE.ADD;
		admin = null;
		deleteAdminButton.setVisible(false);

		setTitle(loc.getString("admin.add"));

		updateLocalisation();
		this.pack();
		this.setResizable(false);

		usernameEntry.setText(null);
		nameEntry.setText(null);
		commentEntry.setText(null);
		passwordEntry.setText(null);
		passwordConfirmation.setText(null);
		// Horrible Hack
		passwordEntry.setBackground(Color.RED);
		passwordConfirmation.setBackground(Color.WHITE);

		makeRootCheckBox.setSelected(false);
		makeRootCheckBox.setEnabled(creator.isRoot());
		makeRootLabel.setEnabled(creator.isRoot());

		showPasswordCheckBox.setSelected(false);
		passwordEntry.setEchoChar('*');
		passwordConfirmation.setEchoChar('*');

		deleteAdminButton.setEnabled(false);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screenSize.width / 2 - this.getSize().width / 2,
				screenSize.height / 2 - this.getSize().height / 2);

		setVisible(true);

		this.setResizable(true);
	}

	/**
	 * Edit an admin.
	 * 
	 * @param admin
	 *            The admin to be edited.
	 * @param creator
	 *            The admin currently logged in. Is used to determine
	 *            permissions regarding setting root etc.
	 */
	public void editAdmin(AdminInfo admin, AdminInfo creator) {
		this.creator = creator;
		this.admin = admin;
		mode = DIALOG_MODE.EDIT;
		setTitle(loc.getString("admin.edit"));

		deleteAdminButton.setVisible(true);

		updateLocalisation();
		this.pack();
		this.setResizable(false);

		usernameEntry.setText(admin.getUsername());
		nameEntry.setText(admin.getName());
		commentEntry.setText(admin.getComment());
		passwordEntry.setText(null);
		passwordConfirmation.setText(null);
		// Horrible Hack
		passwordEntry.setBackground(Color.WHITE);
		passwordConfirmation.setBackground(Color.WHITE);

		makeRootCheckBox.setSelected(admin.isRoot());
		makeRootCheckBox.setEnabled(creator.isRoot() && !admin.isRoot());
		makeRootLabel.setEnabled(creator.isRoot() && !admin.isRoot());

		showPasswordCheckBox.setSelected(false);
		passwordEntry.setEchoChar('*');
		passwordConfirmation.setEchoChar('*');

		deleteAdminButton.setEnabled(!(admin.isRoot())
				|| admin.getUsername().equals(creator.getUsername()));

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screenSize.width / 2 - this.getSize().width / 2,
				screenSize.height / 2 - this.getSize().height / 2);

		setVisible(true);

		this.setResizable(true);
	}

	public void updateLocalisation() {

		entryPanelBorder.setTitle(loc.getString("member.add.entryframe"));

		usernameEntryLabel.setText(loc.getString("admin.username") + ":");
		nameEntryLabel.setText(locCommon.getString("name") + ":");
		commentEntryLabel.setText(loc.getString("admin.comment") + ":");
		makeRootLabel.setText(loc.getString("admin.makeRoot") + ":");

		deleteAdminButton.setText(loc.getString("admin.delete"));

		cancelButton.setText(locCommon.getString("cancel"));
		saveButton.setText(locCommon.getString("save"));

		if (mode == DIALOG_MODE.ADD) {
			passwordEntryLabel.setText(loc
					.getString("admin.password_entry.add"));
			passwordConfirmationLabel.setText(loc
					.getString("admin.password_confirmation.add"));
		} else {
			passwordEntryLabel.setText(loc
					.getString("admin.password_entry.edit"));
			passwordConfirmationLabel.setText(loc
					.getString("admin.password_confirmation.edit"));
		}

		showPasswordCheckBox.setText(loc
				.getString("admin.password_entry.show_password"));

	}

	private class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// Check that all required details are filled in or warn.
			if (arg0.getSource() == saveButton) {
				if (usernameEntry.getText().length() == 0) {
					JOptionPane.showMessageDialog(null, loc
							.getString("admin.add.missing_details"), loc
							.getString("admin.add.missing_details.title"),
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				if (!Arrays.equals(passwordEntry.getPassword(),
						passwordConfirmation.getPassword())) {
					// TODO: cry
					return;
				}
				if (mode == DIALOG_MODE.ADD
						&& passwordEntry.getPassword().length == 0) {
					// TODO: cry
					return;
				}
			}
			if (arg0.getSource() == cancelButton) {
				setVisible(false);
			} else if (arg0.getSource() == saveButton
					&& mode == DIALOG_MODE.ADD) {
				try {
					db.addAdmin(usernameEntry.getText(), passwordEntry
							.getPassword(), nameEntry.getText(),
							makeRootCheckBox.isSelected(), commentEntry
									.getText());
					setVisible(false);
				} catch (InvalidDataException e) {
					String message = "<html><table><tr><td width=300 align=\"left\">"
							+ MessageFormat.format(loc
									.getString("admin.add.exists"),
									usernameEntry.getText())
							+ "</td></tr></table></html>";
					JOptionPane.showMessageDialog(null, message, loc
							.getString("admin.add.exists.title"),
							JOptionPane.WARNING_MESSAGE);
					return;
				}
			} else if (arg0.getSource() == saveButton
					&& mode == DIALOG_MODE.EDIT) { // EDIT
				try {
					db.modifyAdmin(admin, usernameEntry.getText(), nameEntry
							.getText(), makeRootCheckBox.isSelected(),
							commentEntry.getText());
					if (passwordEntry.getPassword().length > 0) {
						db.setNewAdminPassword(admin, passwordEntry
								.getPassword());
					}
					// Dialog stating success?
					setVisible(false);
				} catch (InvalidDataException e) {
					String message = "<html><table><tr><td width=300 align=\"left\">"
							+ MessageFormat.format(loc
									.getString("admin.add.exists"),
									usernameEntry.getText())
							+ "</td></tr></table></html>";
					JOptionPane.showMessageDialog(null, message, loc
							.getString("admin.add.exists.title"),
							JOptionPane.WARNING_MESSAGE);
					return;
				}
			} else if (arg0.getSource() == deleteAdminButton
					&& mode == DIALOG_MODE.EDIT) {
				// TODO: check whether root, or current.
				if (admin.isRoot()
						|| admin.getUsername().equals(creator.getUsername())) {
					return;
				}
				if (JOptionPane.showConfirmDialog(null, MessageFormat.format(
						loc.getString("admin.deleteAdmin"),
						admin.getUsername(), admin.getName())) == JOptionPane.OK_OPTION)
					try {
						db.removeAdmin(admin.getUsername());
					} catch (Exception e) {
						// TODO: deal with the error.
					}
				setVisible(false); // Check whether deleted, and hide if
				// true
			} else if (arg0.getSource() == showPasswordCheckBox) {
				passwordEntry
						.setEchoChar((showPasswordCheckBox.isSelected() ? 0
								: '*'));
				passwordConfirmation.setEchoChar((showPasswordCheckBox
						.isSelected() ? 0 : '*'));
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

	private class PasswordEntryListener implements FocusListener,
			MouseListener, DocumentListener {

		public PasswordEntryListener() {
		}

		@Override
		public void focusGained(FocusEvent arg0) {
			// Do nothing
		}

		@Override
		public void focusLost(FocusEvent arg0) {
			if (!Arrays.equals(passwordEntry.getPassword(),
					passwordConfirmation.getPassword())) {
				passwordConfirmation.setBackground(Color.RED);
			} else {
				passwordConfirmation.setBackground(Color.WHITE);
			}
			if (mode == DIALOG_MODE.ADD
					&& passwordEntry.getPassword().length == 0) {
				passwordEntry.setBackground(Color.RED);
			} else {
				passwordEntry.setBackground(Color.white);
			}
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

		@Override
		public void changedUpdate(DocumentEvent arg0) {
			focusLost(null);
		}

		@Override
		public void insertUpdate(DocumentEvent arg0) {
			focusLost(null);
		}

		@Override
		public void removeUpdate(DocumentEvent arg0) {
			focusLost(null);
		}
	}
}
