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
 *	14/03/2011:	Created.
 */
package org.ahunt.simpleRowLog.admin;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;

import org.ahunt.simpleRowLog.interfaces.Database;

/**
 * Dialog allowing the editing of an admin's permissions.
 * 
 * @author Andrzej J.R. Hunt
 */
public class EditAdminPermissions extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private AdminInfo admin;

	private Database db;

	private ResourceBundle locAdmin = ResourceBundle.getBundle("admin");

	private JPanel permissionsPanel = new JPanel();
	private JLabel[] permissionDescriptors;
	private JCheckBox[] permissionSelectors;

	private JCheckBox allPermissionsSelector;

	private JButton applyButton = new JButton();
	private JButton exitButton = new JButton();
	private JButton cancelButton = new JButton();

	public EditAdminPermissions(Database db, AdminInfo admin) {
		super();
		if (db == null) {
			throw new IllegalArgumentException("db cannot be null");
		}
		if (admin == null) {
			throw new IllegalArgumentException("admin cannot be null");
		}
		this.db = db;
		this.admin = admin;

		buildPermissionsPanel();

		// Boring old layout code
		GroupLayout l = new GroupLayout(getContentPane());
		getContentPane().setLayout(l);
		l.setAutoCreateGaps(true);
		l.setAutoCreateContainerGaps(true);

		l.setVerticalGroup(l.createSequentialGroup().addComponent(
				permissionsPanel).addGroup(
				l.createParallelGroup().addComponent(applyButton).addComponent(
						exitButton).addComponent(cancelButton)));
		l.setHorizontalGroup(l.createParallelGroup().addComponent(
				permissionsPanel).addGroup(
				GroupLayout.Alignment.TRAILING,
				l.createSequentialGroup().addComponent(applyButton)
						.addComponent(exitButton).addComponent(cancelButton)));

		applyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				savePermissions();
			}
		});
		exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				savePermissions();
				setVisible(false);
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (JOptionPane
						.showConfirmDialog(
								null,
								locAdmin
										.getString("dialog.conf.admin.permissions.confirm_cancel"),
								locAdmin
										.getString("dialog.conf.admin.permissions.confirm_cancel.title"),
								JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
					setVisible(false);
				}
			}
		});

		loadCurrentPermissions();

		applyButton.setText(locAdmin.getString("dialog.conf.apply"));
		cancelButton.setText(locAdmin.getString("dialog.conf.cancel"));
		exitButton.setText(locAdmin.getString("dialog.conf.exit"));

		setTitle(locAdmin.getString("dialog.conf.admin.permissions.title"));
		
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screenSize.width / 2 - this.getSize().width / 2,
				screenSize.height / 2 - this.getSize().height / 2);


		setResizable(false);
		setModal(true);
		setVisible(true);

	}

	private void buildPermissionsPanel() {

		CheckBoxListener cbl = new CheckBoxListener();

		// Set up the all permissions checkbox
		allPermissionsSelector = new JCheckBox("<html><b>"
				+ locAdmin.getString("admin.permissions.all") + "</b</html>");
		allPermissionsSelector.addActionListener(cbl);

		permissionSelectors = new JCheckBox[AdminPermissionList.permissions.length];
		permissionDescriptors = new JLabel[AdminPermissionList.permissions.length];
		for (int i = 0; i < AdminPermissionList.permissions.length; i++) {
			permissionSelectors[i] = new JCheckBox(""
					+ AdminPermissionList.permissions[i]);
			permissionDescriptors[i] = new JLabel(locAdmin
					.getString(AdminPermissionList.permissions[i]));
			permissionSelectors[i].addActionListener(cbl);
		}

		// Layouting
		GroupLayout l = new GroupLayout(permissionsPanel);
		permissionsPanel.setLayout(l);
		l.setAutoCreateGaps(false);
		l.setAutoCreateContainerGaps(true);

		// Parallel: all the selectors, then the descriptors in P2
		ParallelGroup hGroupP1 = l.createParallelGroup();
		ParallelGroup hGroupP2 = l.createParallelGroup();
		for (int i = 0; i < AdminPermissionList.permissions.length; i++) {
			hGroupP1.addComponent(permissionSelectors[i]);
			hGroupP2.addComponent(permissionDescriptors[i]);
		}
		l.setHorizontalGroup(l.createParallelGroup().addComponent(
				allPermissionsSelector)
				.addGroup(
						l.createSequentialGroup().addGroup(hGroupP1).addGroup(
								hGroupP2)));

		SequentialGroup vGroup = l.createSequentialGroup();
		vGroup.addGroup(l.createSequentialGroup().addComponent( 
					allPermissionsSelector));
		for (int i = 0; i < AdminPermissionList.permissions.length; i++) {
			vGroup.addGroup(
					l.createParallelGroup(Alignment.BASELINE)
							.addComponent(permissionSelectors[i]).addComponent(
									permissionDescriptors[i]));
		}
		l.setVerticalGroup(vGroup);
	}

	private void loadCurrentPermissions() {
		admin = db.getAdmin(admin.getUsername()); // Make sure we aren't stale

		String[] permissions = admin.getPermissionList().getAllPermissions();

		for (JCheckBox cb : permissionSelectors) {
			cb.setSelected(false);
			for (String p : permissions) {
				if (cb.getText().equals(p)) {
					cb.setSelected(true);
				}
			}
		}
	}

	private void savePermissions() {
		boolean autoStore = admin.getPermissionList().getAutoStore();
		admin.getPermissionList().setAutoStore(false);
		admin.getPermissionList().clearAllPermissions();
		for (JCheckBox cb : permissionSelectors) {
			if (cb.isSelected()) {
				admin.getPermissionList().addPermission(cb.getText());
			}
		}
		admin.getPermissionList().storePermissions();
		admin.getPermissionList().setAutoStore(autoStore);
	}

	private class CheckBoxListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			boolean allChecked = true;
			boolean noneChecked = true;
			for (JCheckBox c : permissionSelectors) {
				if (noneChecked && c.isSelected())
					noneChecked = false;
				if (allChecked && !c.isSelected())
					allChecked = false;
			}
			if (e.getSource() == allPermissionsSelector && allChecked) {
				// allPermissionSelector.setSelected(true);
				for (JCheckBox c : permissionSelectors) {
					c.setSelected(false);
				}
			} else if (e.getSource() == allPermissionsSelector && !allChecked) {
				for (JCheckBox c : permissionSelectors) {
					c.setSelected(true);
				}
			} else if (e.getSource() != allPermissionsSelector && allChecked) {
				allPermissionsSelector.setSelected(true);
			} else {
				allPermissionsSelector.setSelected(false);
			}

		}
	}
}
