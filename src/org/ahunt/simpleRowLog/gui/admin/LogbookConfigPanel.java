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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ResourceBundle;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
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

	private JLabel nameFormatEditLabel = new JLabel();
	private JTextField nameFormatEdit = new JTextField();

	private JLabel dateFormatEditLabel = new JLabel();
	private JTextField dateFormatEdit = new JTextField();

	private JLabel exitAuthenticationLabel = new JLabel();
	private JCheckBox exitAuthentication = new JCheckBox();

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
		nameFormatEdit.setText(configMain.getProperty("srl.name_format"));
		dateFormatEdit.setText(configMain.getProperty("srl.date_format"));
		if (configMain.getProperty("authenticate_for_exit").equals("true")) {
			exitAuthentication.setSelected(true);
		}

		// Localisation
		nameFormatEditLabel.setText(loc
				.getString("dialog.conf.main.config.name_format"));
		dateFormatEditLabel.setText(loc
				.getString("dialog.conf.main.config.date_format"));
		exitAuthenticationLabel.setText(loc
				.getString("dialog.conf.main.config.exit_authentication"));

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
						.addComponent(nameFormatEditLabel).addComponent(
								dateFormatEditLabel).addComponent(
								exitAuthenticationLabel)).addGroup(
				l.createParallelGroup().addComponent(nameFormatEdit)
						.addComponent(dateFormatEdit).addComponent(
								exitAuthentication)));
		l.setVerticalGroup(l.createSequentialGroup().addGroup(
				l.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(nameFormatEditLabel).addComponent(
								nameFormatEdit)).addGroup(
				l.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(dateFormatEditLabel).addComponent(
								dateFormatEdit)).addGroup(
				l.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(exitAuthenticationLabel).addComponent(
								exitAuthentication)));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void apply() {
		// Save all details required.
		try {
			// Main configuration
			boolean storeOnModifyMain = false;
			if (configMain.getStoreOnModify()) {
				storeOnModifyMain = true;
				configMain.setStoreOnModify(false);
			}
			configMain.setProperty("srl.name_format", nameFormatEdit.getText());
			configMain.setProperty("srl.date_format", dateFormatEdit.getText());
			if (exitAuthentication.isSelected()) {
				configMain.setProperty("authenticate_for_exit", "true");
			} else {
				configMain.setProperty("authenticate_for_exit", "false");
			}
			configMain.save();
			configMain.setStoreOnModify(storeOnModifyMain);
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

}
