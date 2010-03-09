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
 *	08/03/2010: Created.
 */

package org.ahunt.simpleRowLog.gui.simpleGUI;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeListener;

import org.ahunt.simpleRowLog.conf.Configuration;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JSpinnerDateEditor;

/**
 * A panel allowing selection of a date, and a button allowing resetting it to
 * today's date.
 * 
 * @author Andrzej JR Hunt
 * 
 */
public class DaySelectionPanel extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Configuration conf;

	private ResourceBundle loc = ResourceBundle.getBundle("gui");

	private JSpinnerDateEditor dateChooser = new JSpinnerDateEditor();
	private JButton todayButton = new JButton();

	public DaySelectionPanel() {
		try {
			conf = Configuration.getConf("simpleGUI");
		} catch (FileNotFoundException e) {
			ErrorHandler.handleError(e);
		}
		todayButton.addActionListener(this);
		GroupLayout l = new GroupLayout(this);
		setLayout(l);
		l.setAutoCreateContainerGaps(true);
		l
				.setHorizontalGroup(l
						.createParallelGroup()
						.addComponent(dateChooser)
						.addGroup(
								l
										.createSequentialGroup()
										.addPreferredGap(
												LayoutStyle.ComponentPlacement.UNRELATED,
												GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addComponent(todayButton)
										.addPreferredGap(
												LayoutStyle.ComponentPlacement.UNRELATED,
												GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));
		l.setVerticalGroup(l.createSequentialGroup().addComponent(dateChooser)
				.addComponent(todayButton));
		dateChooser.setDateFormatString(conf.getProperty("srl.date_format"));
		updateLocalisation();
		LineBorder b = new LineBorder(Color.BLACK);
		setBorder(b);
		actionPerformed(null);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		dateChooser.setDate(new Date());
	}

	public void updateLocalisation() {
		todayButton.setText(loc.getString("select_today"));
	}

	public void addChangeListener(ChangeListener cl) {
		dateChooser.addChangeListener(cl);
	}

	public Date getDate() {
		return dateChooser.getDate();
	}
}
