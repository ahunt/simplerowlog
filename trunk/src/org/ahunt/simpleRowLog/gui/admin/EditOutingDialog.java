/*
 *    This file is part of simple rowLog: the open rowing logbook.
 *    Copyright (C) 2010, 2011  Andrzej JR Hunt
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
 *  06/01/2011: Created from the ModifyOutingDialog in simpleGUI.
 */
package org.ahunt.simpleRowLog.gui.admin;

import org.ahunt.simpleRowLog.common.SuggestiveTextField;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;

import org.ahunt.simpleRowLog.common.BoatInfo;
import org.ahunt.simpleRowLog.common.MemberInfo;
import org.ahunt.simpleRowLog.common.OutingInfo;
import org.ahunt.simpleRowLog.interfaces.Database;

import com.toedter.calendar.JDateChooser;

/**
 * Show the dialog to modify the information for an outing.
 * 
 * @author Andrzej JR Hunt
 * 
 */
public class EditOutingDialog extends JDialog {

	private JDialog dialog = this;

	/**
	 * Whether this dialog is dealing wit a new outing, or else whether we are
	 * modifying an outing.
	 */
	private boolean isNewOuting;
	private DateFormat format = new SimpleDateFormat("HH:mm");

	/** A list of boats the Dialog is to use. */
	private BoatInfo[] boats;
	/** A list of members. */
	private MemberInfo[] members;

	// The GUI elements
	private JDateChooser dateChooser = new JDateChooser();
	private JLabel dateLabel = new JLabel();

	private SuggestiveTextField boatEntry;
	private JLabel boatEntryLabel = new JLabel();

	private SuggestiveTextField[] rowerEntry = new SuggestiveTextField[8];
	private JLabel[] rowerEntryLabel = new JLabel[8];
	private SuggestiveTextField coxEntry;
	private JLabel coxEntryLabel;

	private FormattedTimeField timeOutEntry;
	private JLabel timeOutEntryLabel;
	private FormattedTimeField timeInEntry;
	private JLabel timeInEntryLabel;

	private JFormattedTextField distanceEntry = new JFormattedTextField(new NumberFormatter(new DecimalFormat("#")));
	private JLabel distanceEntryLabel = new JLabel();
	private JTextArea destinationEntry = new JTextArea(3, 32);
	private JLabel destinationEntryLabel = new JLabel();
	private JTextArea commentEntry = new JTextArea(3, 32);
	private JLabel commentEntryLabel = new JLabel();

	private JButton deleteOutingButton = new JButton();

	private JButton confirmButton = new JButton();
	private JButton cancelButton = new JButton();

	private OutingInfo outing;

	// Valid input;
	/**
	 * Whether a valid distance has been entered.
	 */
	private boolean validDistance = true;

	/**
	 * The serialisation number.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The database instance that this dialog is to use.
	 */
	private Database db;

	/**
	 * ResourceBundle for l10n data.
	 */
	private ResourceBundle locGUI = ResourceBundle.getBundle("gui");
	private ResourceBundle locAdmin = ResourceBundle.getBundle("admin");

	/**
	 * Create a new EditOutingDialog, used for adding and editing outings in the
	 * admin dialog.
	 */
	public EditOutingDialog(Database db) {
		this.db = db;

		// Boat input
		boatEntryLabel.setText(locGUI.getString("outing.boat"));
		boatEntry = new SuggestiveTextField(this, true, null, 16);
		dateLabel.setText(locAdmin.getString("outing.date"));

		// Bottom buttons
		confirmButton.setText(locGUI.getString("outing.confirm"));
		deleteOutingButton.setText(locAdmin.getString("outing.delete"));
		ButtonListener bl = new ButtonListener();
		confirmButton.addActionListener(bl);
		cancelButton.addActionListener(bl);
		deleteOutingButton.addActionListener(bl);

		distanceEntry.setValue(new Integer(0));
		JPanel rowerPanel = setupRowerPanel();
		JPanel infoPanel = setupInfoPanel();

		// Layouting
		GroupLayout l = new GroupLayout(this.getContentPane());
		this.getContentPane().setLayout(l);
		l.setAutoCreateGaps(true);
		l.setAutoCreateContainerGaps(true);
		// Grouping in layout
		l.setHorizontalGroup(l.createSequentialGroup().addGroup(
				l.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
						l.createSequentialGroup().addComponent(dateLabel)
								.addComponent(dateChooser).addPreferredGap(
										LayoutStyle.ComponentPlacement.RELATED,
										GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)).addGroup(
						l.createSequentialGroup().addComponent(boatEntryLabel)
								.addComponent(boatEntry).addPreferredGap(
										LayoutStyle.ComponentPlacement.RELATED,
										GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)).addComponent(
						rowerPanel).addComponent(infoPanel).addGroup(
						l.createSequentialGroup().addPreferredGap(
								LayoutStyle.ComponentPlacement.RELATED,
								GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(deleteOutingButton)).addGroup(
						l.createSequentialGroup().addPreferredGap(
								LayoutStyle.ComponentPlacement.RELATED,
								GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(confirmButton).addComponent(
										cancelButton))));
		l.setVerticalGroup(l.createSequentialGroup().addGroup(
				l.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(dateLabel).addComponent(dateChooser))
				.addGroup(
						l.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(boatEntryLabel).addComponent(
										boatEntry)).addComponent(rowerPanel)
				.addComponent(infoPanel).addGroup(
						l.createParallelGroup(GroupLayout.Alignment.TRAILING)
								.addComponent(deleteOutingButton)).addGroup(
						l.createParallelGroup(GroupLayout.Alignment.TRAILING)
								.addComponent(confirmButton).addComponent(
										cancelButton)));
		// Routine (and boring) window setup
		this.getRootPane().setDefaultButton(confirmButton);
		this.setModal(true);
		this.pack();
		this.setResizable(false);
		this.setAlwaysOnTop(true);
	}

	/**
	 * Setup the panel for entering the crew for an outing.
	 * 
	 * @return
	 */
	private JPanel setupRowerPanel() {
		JPanel rowerPanel = new JPanel(); // Holding all the items
		// Rower input
		for (int i = 0; i < 8; i++) { // Create and fill the entries
			if (i == 0) {
				rowerEntry[i] = new SuggestiveTextField(rowerPanel, true, null,
						32);
				rowerEntry[i].addFocusListener(new NameEntryListener(
						rowerEntry[i]));
			} else {
				rowerEntry[i] = new SuggestiveTextField(rowerPanel, false,
						null, 32);
				rowerEntry[i].addFocusListener(new NameEntryListener(
						rowerEntry[i]));
			}
			// rowerEntry[i] = new JTextField(32);
			rowerEntryLabel[i] = new JLabel(MessageFormat.format(locGUI
					.getString("outing.rowerNum"), i + 1));
			// new EntryListener(rowerPanel, rowerEntry[i], i);
		}
		coxEntry = new SuggestiveTextField(rowerPanel, false, null, 32);
		coxEntry.addFocusListener(new NameEntryListener(coxEntry));
		coxEntryLabel = new JLabel(locGUI.getString("outing.cox"));
		// new EntryListener(rowerPanel, coxEntry, -1);
		rowerPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK),
				locGUI.getString("outing.rowers")));
		GroupLayout r = new GroupLayout(rowerPanel); // Layouting
		rowerPanel.setLayout(r);

		r.setHorizontalGroup(r.createSequentialGroup()
				.addGroup(
						r.createParallelGroup()
								.addComponent(rowerEntryLabel[0]).addComponent(
										rowerEntryLabel[1]).addComponent(
										rowerEntryLabel[2]).addComponent(
										rowerEntryLabel[3]).addComponent(
										rowerEntryLabel[4]).addComponent(
										rowerEntryLabel[5]).addComponent(
										rowerEntryLabel[6]).addComponent(
										rowerEntryLabel[7]).addComponent(
										coxEntryLabel)).addPreferredGap(
						LayoutStyle.ComponentPlacement.RELATED,
						GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(
						r.createParallelGroup().addComponent(rowerEntry[0])
								.addComponent(rowerEntry[1]).addComponent(
										rowerEntry[2]).addComponent(
										rowerEntry[3]).addComponent(
										rowerEntry[4]).addComponent(
										rowerEntry[5]).addComponent(
										rowerEntry[6]).addComponent(
										rowerEntry[7]).addComponent(coxEntry)));
		r.setVerticalGroup(r.createSequentialGroup().addGroup(
				r.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(rowerEntryLabel[0]).addComponent(
								rowerEntry[0])).addPreferredGap(
				LayoutStyle.ComponentPlacement.RELATED,
				GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(
				r.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(rowerEntryLabel[1]).addComponent(
								rowerEntry[1])).addPreferredGap(
				LayoutStyle.ComponentPlacement.RELATED,
				GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(
				r.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(rowerEntryLabel[2]).addComponent(
								rowerEntry[2])).addPreferredGap(
				LayoutStyle.ComponentPlacement.RELATED,
				GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(
				r.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(rowerEntryLabel[3]).addComponent(
								rowerEntry[3])).addPreferredGap(
				LayoutStyle.ComponentPlacement.RELATED,
				GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(
				r.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(rowerEntryLabel[4]).addComponent(
								rowerEntry[4])).addPreferredGap(
				LayoutStyle.ComponentPlacement.RELATED,
				GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(
				r.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(rowerEntryLabel[5]).addComponent(
								rowerEntry[5])).addPreferredGap(
				LayoutStyle.ComponentPlacement.RELATED,
				GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(
				r.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(rowerEntryLabel[6]).addComponent(
								rowerEntry[6])).addPreferredGap(
				LayoutStyle.ComponentPlacement.RELATED,
				GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(
				r.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(rowerEntryLabel[7]).addComponent(
								rowerEntry[7])).addPreferredGap(
				LayoutStyle.ComponentPlacement.RELATED,
				GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(
				r.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(coxEntryLabel).addComponent(coxEntry))

		);
		return rowerPanel;
	}

	private JPanel setupInfoPanel() {
		JPanel ret = new JPanel();
		distanceEntryLabel.setText(locGUI.getString("outing.distance"));
		destinationEntryLabel.setText(locGUI.getString("outing.destination"));
		commentEntryLabel.setText(locGUI.getString("outing.comment"));
		// Set up the line-wrapping
		destinationEntry.setLineWrap(true);
		destinationEntry.setWrapStyleWord(true);
		commentEntry.setLineWrap(true);
		commentEntry.setWrapStyleWord(true);
		// Set tab traversal
		Set<KeyStroke> strokes = new HashSet<KeyStroke>(Arrays.asList(KeyStroke
				.getKeyStroke("pressed TAB")));
		destinationEntry.setFocusTraversalKeys(
				KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, strokes);
		commentEntry.setFocusTraversalKeys(
				KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, strokes);
		// Prepare the scroll areas
		JScrollPane destinationEntryScrollPane = new JScrollPane(
				destinationEntry);
		destinationEntryScrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		destinationEntryScrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		JScrollPane commentEntryScrollPane = new JScrollPane(commentEntry);
		commentEntryScrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		commentEntryScrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		// Time in and out
		// timeOutEntry = new JFormattedTextField(new DateFormatter(
		// new SimpleDateFormat("HH:mm")));
		try {
			timeOutEntry = new FormattedTimeField(true);
		} catch (Exception e) {
			// TODO: clean
		}
		timeOutEntry.setHorizontalAlignment(JTextField.CENTER);
		timeOutEntryLabel = new JLabel(locGUI.getString("outing.timeOut"));
		// timeInEntry = new JFormattedTextField(new DateFormatter(
		// new SimpleDateFormat("HH:mm")));
		try {
			timeInEntry = new FormattedTimeField(false);
		} catch (Exception e) {
			// TODO: clean
		}
		timeInEntry.setHorizontalAlignment(JTextField.CENTER);
		timeInEntryLabel = new JLabel(locGUI.getString("outing.timeIn"));
		// timeInEntry.getDocument().addDocumentListener(null);
		// TODO: listener for validation
		// Layout
		GroupLayout l = new GroupLayout(ret);
		ret.setLayout(l);
		l.setAutoCreateGaps(true);
		l
				.setHorizontalGroup(l
						.createSequentialGroup()
						.addGroup(
								l.createParallelGroup().addComponent(
										distanceEntryLabel).addComponent(
										timeOutEntryLabel).addComponent(
										destinationEntryLabel).addComponent(
										commentEntryLabel))
						.addGroup(
								l
										.createParallelGroup()
										.addGroup(
												l
														.createSequentialGroup()
														.addComponent(
																distanceEntry)
														.addPreferredGap(
																LayoutStyle.ComponentPlacement.RELATED,
																GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE))
										.addGroup(
												l
														.createSequentialGroup()
														.addComponent(
																timeOutEntry)
														.addPreferredGap(
																LayoutStyle.ComponentPlacement.RELATED,
																GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(
																timeInEntryLabel)
														.addComponent(
																timeInEntry)
														.addPreferredGap(
																LayoutStyle.ComponentPlacement.UNRELATED,
																GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE))
										.addComponent(
												destinationEntryScrollPane)
										.addComponent(commentEntryScrollPane))

				);
		l.setVerticalGroup(l.createSequentialGroup().addGroup(
				l.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(distanceEntryLabel).addComponent(
								distanceEntry)).addPreferredGap(
				LayoutStyle.ComponentPlacement.RELATED,
				GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(
				l.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(timeOutEntryLabel).addComponent(
								timeOutEntry).addComponent(timeInEntryLabel)
						.addComponent(timeInEntry)).addPreferredGap(
				LayoutStyle.ComponentPlacement.RELATED,
				GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(
				l.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(destinationEntryLabel).addComponent(
								destinationEntryScrollPane)).addPreferredGap(
				LayoutStyle.ComponentPlacement.RELATED,
				GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(
				l.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(commentEntryLabel).addComponent(
								commentEntryScrollPane))

		);
		return ret;
	}

	/**
	 * Show the dialog in order to add a new outing.
	 */
	public void addOuting() {
		this.outing = null;
		isNewOuting = true;
		this.setTitle(locGUI.getString("outingDialog.title.newOuting"));
		cancelButton.setText(locGUI.getString("outing.cancel_new"));
		deleteOutingButton.setEnabled(false);

		// Clear all boxes
		dateChooser.setDate(new Date()); // Today's date by default
		boatEntry.setText("");
		for (int i = 0; i < 8; i++) {
			rowerEntry[i].setText("");
		}
		distanceEntry.setText("");
		// We want to set the current time
		timeOutEntry.setText(format.format(new Date()));
		timeInEntry.setText("");
		coxEntry.setText("");
		destinationEntry.setText("");
		commentEntry.setText("");

		updateDBInfo();
		// Highlight the necessary fields.
		boatEntry.setBackground(Color.YELLOW);
		rowerEntry[0].setBackground(Color.YELLOW);
		timeOutEntry.setBackground(Color.YELLOW);

		boatEntry.requestFocusInWindow();
		// Centering
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screenSize.width / 2 - this.getSize().width / 2,
				screenSize.height / 2 - this.getSize().height / 2);
		this.setVisible(true);
	}

	/**
	 * Show the dialog to modify an existing outing. This retrieves all data so
	 * far from the database.
	 * 
	 * @param id
	 *            The id of the outing.
	 */
	public void modifyOuting(OutingInfo outing) {
		this.outing = outing;
		updateDBInfo();
		isNewOuting = false;
		cancelButton.setText(locGUI.getString("outing.cancel_modify"));
		deleteOutingButton.setEnabled(false);

		// TODO: if cancel is pressed ask for confirmation.
		// Clear all boxes
		dateChooser.setDate(outing.getDay());
		boatEntry.setText(outing.getBoat().getName());
		for (int i = 0; i < 8; i++) {
			if (outing.getRowers()[i] != null) {
				rowerEntry[i].setText(outing.getRowers()[i].getName());
			} else {
				rowerEntry[i].setText("");
			}
		}
		distanceEntry.setValue(outing.getDistance());
		timeOutEntry.setText(format.format(outing.getOut()));
		if (outing.getIn() != null) {
			timeInEntry.setText(format.format(outing.getIn()));
		} else {
			timeInEntry.setText(format.format(new Date()));
		}
		if (outing.getCox() != null) {
			coxEntry.setText(outing.getCox().getName());
		} else {
			coxEntry.setText("");
		}
		destinationEntry.setText(outing.getDestination());
		commentEntry.setText(outing.getComment());
		distanceEntry.requestFocusInWindow(); // Distance is first of likely
		// empty fields
		// Centering
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screenSize.width / 2 - this.getSize().width / 2,
				screenSize.height / 2 - this.getSize().height / 2);
		this.setVisible(true);

	}

	private void updateDBInfo() {
		// Boats
		boats = db.getBoats(true);
		ArrayList<String> a = new ArrayList<String>();
		for (BoatInfo b : boats) {
			a.add(b.getName());
		}
		boatEntry.setOptions(a.toArray(new String[0]));
		// Members
		members = db.getMembers();
		a.clear();
		for (MemberInfo m : members) {
			a.add(m.getName());
		}
		String[] options = a.toArray(new String[0]);
		for (SuggestiveTextField t : rowerEntry) {
			t.setOptions(options);
		}
		coxEntry.setOptions(options);

	}

	/**
	 * Check whether all the input is valid, i.e. can be input to the database.
	 * 
	 * @return Whether the input data is valid.
	 */
	private boolean isValidInput() {
		if (boatEntry.isValidEntry() && coxEntry.isValidEntry()
				&& rowerEntry[0].isValidEntry() && rowerEntry[1].isValidEntry()
				&& rowerEntry[2].isValidEntry() && rowerEntry[3].isValidEntry()
				&& rowerEntry[4].isValidEntry() && rowerEntry[5].isValidEntry()
				&& rowerEntry[6].isValidEntry() && rowerEntry[7].isValidEntry()
				&& timeOutEntry.isValidEntry() && timeInEntry.isValidEntry()
				&& validDistance) {
			return true;
		} else {
			return false;
		}
	}

	private class FormattedTimeField extends JFormattedTextField implements
			DocumentListener {

		private boolean mustBeFilled;
		private boolean isValid;
		private boolean isFilled;

		public FormattedTimeField(boolean mustBeFilled) throws ParseException {
			super(new MaskFormatter("##:##"));
			this.mustBeFilled = mustBeFilled;
			if (mustBeFilled) {
				isValid = false;
			} else {
				isValid = true;
			}
			getDocument().addDocumentListener(this);
			setFocusLostBehavior(JFormattedTextField.PERSIST);
		}

		/**
		 * Only call if the field is valid.
		 * 
		 * @return
		 */
		public Date getTime() throws InvalidEntryException {
			if (!isValid()) {
				throw new InvalidEntryException("Time field must be valid."); // TODO:
				// custom
				// error.
			}
			if (isValid() && isFilled) {
				try {
					return format.parse(getText());
				} catch (ParseException e) {
					// TODO: log.error ()
					return null; // Shouldn't happen.
				}
			} else {
				return null;
			}
		}

		@Override
		public void changedUpdate(DocumentEvent arg0) {
			try {
				format.parse(getText());
				isValid = true;
			} catch (ParseException e) {
				isValid = false;
			}
			String s = getText();
			int length = s.length();
			int colonPosition = s.indexOf(":");
			if (colonPosition != -1) {
				try {
					// No colon = not good.
					int hours = Integer.parseInt(s.substring(0, colonPosition));
					int minutes = Integer.parseInt(s.substring(
							colonPosition + 1, length));
					if (!(hours < 23 && minutes < 60)) { // only from 00:00 to
						// 23:59
						isValid = false;
					}
				} catch (NumberFormatException e) { // Can't parse the times.
					isValid = false;
				}
			} else {
				isValid = false;
			}
			if (!mustBeFilled && getText().equals("  :  ")) { // Empty and ok
				isValid = true;
			}
			if (getText().equals("  :  ")) {
				isFilled = false;
			} else {
				isFilled = true;
			}
			if (getText().equals("  :  ") && !mustBeFilled) {
				setBackground(Color.WHITE);
			} else if (mustBeFilled && getText().equals("  :  ")) {
				setBackground(Color.YELLOW);
			} else if (isValid) {
				setBackground(Color.WHITE); // Formerly green
			} else {
				setBackground(Color.RED);
			}
		}

		@Override
		public void insertUpdate(DocumentEvent arg0) {
			changedUpdate(arg0);
		}

		@Override
		public void removeUpdate(DocumentEvent arg0) {
			changedUpdate(arg0);
		}

		public boolean isValidEntry() {
			return isValid;
		}

	}

	private class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (arg0.getSource() == cancelButton && isNewOuting) {
				// showConfirmDialog(cancelNewOuting)
				setVisible(false); // Return the dialog.
			} else if (arg0.getSource() == cancelButton && !isNewOuting) {
				// TODO: dialog
				setVisible(false);
			} else if (arg0.getSource() == deleteOutingButton) {
				db.removeOuting(outing);
			} else if (arg0.getSource() == confirmButton) {
				if (isValidInput()) {
					try {
						int distance;
						if (distanceEntry.getText().equals("")) {
							distance = 0;
						} else {
							try {
								distance = Integer
									.parseInt(distanceEntry.getText());
							} catch (NumberFormatException e) {
								JOptionPane.showMessageDialog(dialog, locGUI
										.getString("outing.invalid_input_distance"), locGUI
										.getString("outing.invalid_input.title"),
										JOptionPane.WARNING_MESSAGE);
								return;
							}
						}
						if (isNewOuting) {
							db.addOuting(dateChooser.getDate(), new int[] {
									getRowerForName(rowerEntry[0].getText()),
									getRowerForName(rowerEntry[1].getText()),
									getRowerForName(rowerEntry[2].getText()),
									getRowerForName(rowerEntry[3].getText()),
									getRowerForName(rowerEntry[4].getText()),
									getRowerForName(rowerEntry[5].getText()),
									getRowerForName(rowerEntry[6].getText()),
									getRowerForName(rowerEntry[7].getText()) },
									getRowerForName(coxEntry.getText()),
									timeOutEntry.getTime(), timeInEntry
											.getTime(), commentEntry.getText(),
									destinationEntry.getText(),
									getBoatForName(boatEntry.getText()),
									distance);
						} else {
							db.modifyOuting(outing, dateChooser.getDate()
									.getTime(), new int[] {
									getRowerForName(rowerEntry[0].getText()),
									getRowerForName(rowerEntry[1].getText()),
									getRowerForName(rowerEntry[2].getText()),
									getRowerForName(rowerEntry[3].getText()),
									getRowerForName(rowerEntry[4].getText()),
									getRowerForName(rowerEntry[5].getText()),
									getRowerForName(rowerEntry[6].getText()),
									getRowerForName(rowerEntry[7].getText()) },
									getRowerForName(coxEntry.getText()),
									timeOutEntry.getTime(), timeInEntry
											.getTime(), commentEntry.getText(),
									destinationEntry.getText(),
									getBoatForName(boatEntry.getText()),
									distance);
						}

						// Save the data
						setVisible(false); // Return the dialog.
					} catch (InvalidEntryException e) {
						// TODO: inexplicable error.
					}
				} else {
					// Warn about incorrect data, and where.
					// TODO: update this outdated method.
					JOptionPane.showMessageDialog(dialog, locGUI
							.getString("outing.invalid_input"), locGUI
							.getString("outing.invalid_input.title"),
							JOptionPane.WARNING_MESSAGE);
				}

			}

		}
	}

	private int getRowerForName(String s) {
		if (s.equals("")) {
			return 0;
		}
		for (MemberInfo m : members) {
			if (m.getName().equals(s)) {
				return m.getId();
			}
		}
		return Database.GUEST_MEMBER_ID; // Guest elsewise.
	}

	private int getBoatForName(String s) {
		for (BoatInfo b : boats) {
			if (b.getName().equals(s)) {
				return b.getId();
			}
		}
		return Database.OTHER_BOAT_ID;
	}

	private class InvalidEntryException extends Exception {

		public InvalidEntryException(String string) {
			super(string);
			// TODO Auto-generated constructor stub
		}

	}

	/**
	 * Listen to the name entry fields. Once it is completed, it capitalises the
	 * names accordingly.
	 * 
	 */
	private class NameEntryListener implements FocusListener {

		private SuggestiveTextField entry;

		public NameEntryListener(SuggestiveTextField entry) {
			this.entry = entry;
		}

		@Override
		public void focusGained(FocusEvent arg0) {
			// Do nothing
		}

		@Override
		public void focusLost(FocusEvent arg0) {
			// entry.setText(Util.capitaliseName(entry.getText()));
			if (!entry.isValid()) {
				// TODO: ask for confirmation of addition.
			}
		}
	}

}
