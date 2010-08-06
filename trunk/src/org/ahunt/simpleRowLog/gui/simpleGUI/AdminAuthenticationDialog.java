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
 *  28/06/2010: Did major work on implementing.
 *  11/03/2010: Created,
 */
package org.ahunt.simpleRowLog.gui.simpleGUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

import org.ahunt.simpleRowLog.common.AdminInfo;
import org.ahunt.simpleRowLog.common.ErrorHandler;
import org.ahunt.simpleRowLog.conf.Configuration;
import org.ahunt.simpleRowLog.interfaces.Database;
import org.apache.derby.iapi.types.Resetable;

public class AdminAuthenticationDialog extends JDialog {

	private static Timer timeout = null;

	/**
	 * Localisation data.
	 */
	private static ResourceBundle rb = ResourceBundle.getBundle("gui");

	/**
	 * Configuration for the dialog.
	 */
	private static Configuration conf;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * How many login attempts are left before the timeout starts. Initially -1
	 * as a marker.
	 */
	private static int attemptsLeft = -1;

	/**
	 * The validated administrator, i.e. is a valid object if an admin has
	 * authenticated.
	 */
	private AdminInfo validatedAdmin = null;

	private Database db;

	private JTextField usernameEntry = new JTextField(32);
	private JPasswordField passwordEntry = new JPasswordField(32);

	private JButton cancelButton = new JButton();
	private JButton validateButton = new JButton();

	private JLabel attemptsLeftLabel = new JLabel();

	/**
	 * Create the authentication dialog.
	 * 
	 * @param db
	 *            The database to be used for authentication.
	 */
	protected AdminAuthenticationDialog(Database db) {
		this.db = db;
		try {
			conf = Configuration.getConf("admin");
		} catch (FileNotFoundException e) {
			ErrorHandler.handleError(e);
		}

		setTitle(rb.getString("admin.login_title"));

		cancelButton.setText(rb.getString("admin.cancel_login"));
		validateButton.setText(rb.getString("admin.validate_login"));
		cancelButton.addActionListener(new ValidationListener());
		validateButton.addActionListener(new ValidationListener());
		getRootPane().setDefaultButton(validateButton);

		// Get the maximum number of attempts left.
		if (attemptsLeft == -1) { // First use
			resetAttempts();
		}
		updateAttempts(); // Show on screen

		// Assemble the gui.
		JPanel entryPanel = new JPanel();
		entryPanel.setLayout(new BoxLayout(entryPanel, BoxLayout.PAGE_AXIS));
		entryPanel.add(usernameEntry);
		entryPanel.add(passwordEntry);
		entryPanel.setBorder(new LineBorder(Color.BLACK));

		GroupLayout l = new GroupLayout(getContentPane());
		getContentPane().setLayout(l);
		l.setAutoCreateGaps(true);
		l.setAutoCreateContainerGaps(true);
		l.setVerticalGroup(l.createSequentialGroup().addComponent(
				attemptsLeftLabel).addComponent(entryPanel).addGroup(
				l.createParallelGroup().addComponent(cancelButton)
						.addComponent(validateButton)));
		l.setHorizontalGroup(l.createParallelGroup().addComponent(
				attemptsLeftLabel).addComponent(entryPanel).addGroup(
				l.createSequentialGroup().addPreferredGap(
						LayoutStyle.ComponentPlacement.RELATED,
						GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(cancelButton)
						.addComponent(validateButton)));

		// Window properties
		this.pack();
		this.setResizable(false);
		this.setAlwaysOnTop(true);
		this.setModal(true);
		// Centering
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screenSize.width / 2 - this.getSize().width / 2,
				screenSize.height / 2 - this.getSize().height / 2);

	}

	/**
	 * Update the label showing the number of login attempts allowed.
	 */
	private void updateAttempts() {
		if (attemptsLeft > 0) {
			attemptsLeftLabel.setText(MessageFormat.format(rb
					.getString("admin.attempts_remaining"), attemptsLeft));
		}
	}

	/**
	 * Reset the number of attempts left to the maximum number.
	 */
	private static void resetAttempts() {
		attemptsLeft = Integer.parseInt(conf.getProperty("max_login_attempts"));
	}

	/**
	 * Do a login, i.e. show the dialog and ask the user to authenticate:
	 * returns the AdminInfo for the admin that has authenticated if the
	 * authentication is successful, otherwise it returns null if the login was
	 * cancelled or unsuccessful.
	 * 
	 * @return The information for the admin that has authenticated.
	 */
	public static AdminInfo doLogin(Database db) {

		if (attemptsLeft == -1)
			;
		// If the timeout is still running tell the user and exit.
		if (attemptsLeft == 0 && timeout != null && timeout.isRunning()) {
			// Tell the user
			JOptionPane.showMessageDialog(null, MessageFormat.format(rb
					.getString("admin.timeout"), Integer.parseInt(conf
					.getProperty("timeout")) / 1000), rb
					.getString("admin.timeout.title"),
					JOptionPane.WARNING_MESSAGE);
			return null;
		} else if (timeout != null && !timeout.isRunning()) {
			resetAttempts(); // Timer is finished.
		}

		AdminAuthenticationDialog d = new AdminAuthenticationDialog(db);
		d.setVisible(true);
		return d.validatedAdmin;
	}

	private class ValidationListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (arg0.getSource() == cancelButton) {
				timeout = new Timer(Integer.parseInt(conf
						.getProperty("timeout")), null);
				timeout.setRepeats(false);
				timeout.start();

				setVisible(false);
				return;
			}
			AdminInfo admin = db.getAdmin(usernameEntry.getText());
			if (admin != null) {
				if (admin.validatePassword(passwordEntry.getPassword())) {
					validatedAdmin = admin;
					setVisible(false); // Close the dialog, forcing doLogin to
					return;
				}
			}
			validatedAdmin = db.getAdmin("INVALID");
			attemptsLeft--; // One more false.
			if (attemptsLeft == 0) {
				setVisible(false);
				timeout = new Timer(Integer.parseInt(conf
						.getProperty("timeout")), null);
				timeout.setRepeats(false);
				// Tell the user
				JOptionPane.showMessageDialog(null, MessageFormat.format(rb
						.getString("admin.no_attempts_left"), Integer
						.parseInt(conf.getProperty("timeout")) / 1000), rb
						.getString("admin.no_attempts_left.title"),
						JOptionPane.WARNING_MESSAGE);
				// Then count the time.
				timeout.start();
			}
			updateAttempts();
		}

	}
}
