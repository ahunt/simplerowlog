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
 *	08/08/2010:	Created.
 */
package org.ahunt.simpleRowLog.gui.admin;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.ahunt.simpleRowLog.common.AdminInfo;
import org.ahunt.simpleRowLog.common.ErrorHandler;
import org.ahunt.simpleRowLog.conf.Configuration;
import org.ahunt.simpleRowLog.interfaces.Database;

/**
 * 
 * 
 * @author Andrzej JR Hunt
 * 
 */
public class LogbookConfigPanel implements ConfigPanelInterface, ActionListener {

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

	private Configuration configMain;
	private Configuration configAdmin;

	/** The panel containing all the graphical components. */
	private JPanel displayPanel = new JPanel();

	// Main configuration components.
	private JPanel mainConfigPanel = new JPanel();

	private JLabel main_nameFormatEditLabel = new JLabel();
	private JTextField main_nameFormatEdit = new JTextField();

	private JLabel main_dateFormatEditLabel = new JLabel();
	private JTextField main_dateFormatEdit = new JTextField();

	private JLabel main_exitAuthenticationLabel = new JLabel();
	private JCheckBox main_exitAuthentication = new JCheckBox();

	private JLabel main_lafSelectionLabel = new JLabel();
	private LAFRadioButton[] main_lafSelectionButtons;

	public LogbookConfigPanel(Database db, AdminInfo admin) {
		super();
		this.db = db;
		this.admin = admin;
		try {
			configMain = Configuration.getConf("main");
			configAdmin = Configuration.getConf("admin");
		} catch (FileNotFoundException e) {
			ErrorHandler.handleError(e);
		}

		if (admin.getPermissionList().isPermissionSet("config_main")) {
			prepareMain();
			displayPanel.add(mainConfigPanel);
			// TODO: better layout
		}

	}

	private void prepareMain() {

		main_nameFormatEdit.setText(configMain.getProperty("srl.name_format"));
		main_dateFormatEdit.setText(configMain.getProperty("srl.date_format"));
		if (configMain.getProperty("authenticate_for_exit").equals("true")) {
			main_exitAuthentication.setSelected(true);
		}

		// Set up the LAF selection.
		UIManager.LookAndFeelInfo[] availableLAFs = UIManager
				.getInstalledLookAndFeels();

		ButtonGroup bg = new ButtonGroup(); // Containers
		ArrayList<LAFRadioButton> buttons_lafs = new ArrayList<LAFRadioButton>();
		JPanel lafSelectionPanel = new JPanel(new GridLayout(0, 1));

		for (UIManager.LookAndFeelInfo laf : availableLAFs) {
			LAFRadioButton b = new LAFRadioButton(laf.getName(), laf
					.getClassName());
			// TODO: Once LAF updating is available, remove this highlighting.
			if (laf.getClassName().equals(
					UIManager.getLookAndFeel().getClass().getCanonicalName())) {
				b.setFont(b.getFont().deriveFont(Font.ITALIC)); // If this is
				// the current
				// in use laf.
			}
			if (laf.getClassName()
					.equals(configMain.getProperty("gui.toolkit"))) {
				b.setSelected(true); // If this is the stored LAF.
			}
			buttons_lafs.add(b);
			bg.add(b);
			lafSelectionPanel.add(b);
		}
		main_lafSelectionButtons = buttons_lafs.toArray(new LAFRadioButton[1]);
		// Put the radio buttons in a column in a panel.

		// Localisation
		main_nameFormatEditLabel.setText(loc
				.getString("dialog.conf.main.config.name_format"));
		main_dateFormatEditLabel.setText(loc
				.getString("dialog.conf.main.config.date_format"));
		main_exitAuthenticationLabel.setText(loc
				.getString("dialog.conf.main.config.exit_authentication"));
		main_lafSelectionLabel.setText(loc.getString("dialog.conf.main.laf"));

		// Layouting and prettiness
		Border border = new TitledBorder(new LineBorder(Color.BLACK), loc
				.getString("dialog.conf.main.config"));
		mainConfigPanel.setBorder(border);

		GroupLayout l = new GroupLayout(mainConfigPanel);
		mainConfigPanel.setLayout(l);
		l.setAutoCreateGaps(true);
		l.setAutoCreateContainerGaps(true);
		l.setHorizontalGroup(l.createSequentialGroup().addGroup(
				l.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(main_nameFormatEditLabel).addComponent(
								main_dateFormatEditLabel).addComponent(
								main_exitAuthenticationLabel).addComponent(
								main_lafSelectionLabel)).addGroup(
				l.createParallelGroup().addComponent(main_nameFormatEdit)
						.addComponent(main_dateFormatEdit).addComponent(
								main_exitAuthentication).addComponent(
								lafSelectionPanel)));
		l.setVerticalGroup(l.createSequentialGroup().addGroup(
				l.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(main_nameFormatEditLabel).addComponent(
								main_nameFormatEdit)).addGroup(
				l.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(main_dateFormatEditLabel).addComponent(
								main_dateFormatEdit)).addGroup(
				l.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(main_exitAuthenticationLabel)
						.addComponent(main_exitAuthentication)).addGroup(
				l.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(main_lafSelectionLabel).addComponent(
								lafSelectionPanel)));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void apply() {
		// Save all details required.
		try {
			// Main configuration
			if (mainConfigPanel != null) {
				boolean storeOnModifyMain = false;
				if (configMain.getStoreOnModify()) {
					storeOnModifyMain = true;
					configMain.setStoreOnModify(false);
				}
				configMain.setProperty("srl.name_format", main_nameFormatEdit
						.getText());
				configMain.setProperty("srl.date_format", main_dateFormatEdit
						.getText());
				if (main_exitAuthentication.isSelected()) {
					configMain.setProperty("authenticate_for_exit", "true");
				} else {
					configMain.setProperty("authenticate_for_exit", "false");
				}
				for (LAFRadioButton b : main_lafSelectionButtons) {
					if (b.isSelected()) {
						if (!b.getClassName().equals(
								configMain.getProperty("gui.toolkit"))) {
							JOptionPane
									.showMessageDialog(
											null,
											loc
													.getString("dialog.conf.main.laf.restart_required"),
											loc
													.getString("dialog.conf.main.laf.restart_required.title"),
											JOptionPane.WARNING_MESSAGE);
						}
						configMain.setProperty("gui.toolkit", b.getClassName());
					}
				}
				configMain.save();
				configMain.setStoreOnModify(storeOnModifyMain);
			}
		} catch (IOException e) {
			// TODO: inform of problem.
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JPanel getPanel() {
		return displayPanel;
	}

	public String getName() {
		return loc.getString("dialog.conf.settings");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	/**
	 * A Look and Feel radio-button. A simple wrapper around JRadioButton
	 * allowing storing of the class name for look and feels, since it isn't
	 * possible to use the name of the look and feel to get back to a class
	 * name, and that is all the normal button could store.
	 * 
	 */
	private class LAFRadioButton extends JRadioButton {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * The name of the class for this LAF.
		 */
		private String className;

		/**
		 * Create a new LAFRadioButton
		 * 
		 * @param name
		 *            The name of this LAF.
		 * @param className
		 *            The class name for the LAF.
		 */
		public LAFRadioButton(String name, String className) {
			super(name);
			this.className = className;
		}

		/**
		 * Get the class name for this LAF.
		 * 
		 * @return The class name for the LAF.
		 */
		public String getClassName() {
			return className;
		}
	}

}
