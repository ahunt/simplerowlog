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
 *  25/01/2010: Worked on dialog: added validation of text fields (except for
 *  			distance entry), added possibility of saving outings (albeit no
 *  			editing yet).
 *	18/01/2010:	Changelog added.
 */
package org.ahunt.simpleRowLog.gui.simpleGUI;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.DateFormat;
import java.text.Format;
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
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
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
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import javax.swing.text.DateFormatter;
import javax.swing.text.MaskFormatter;

import org.ahunt.simpleRowLog.common.BoatInfo;
import org.ahunt.simpleRowLog.common.MemberInfo;
import org.ahunt.simpleRowLog.common.OutingInfo;
import org.ahunt.simpleRowLog.interfaces.Database;

/**
 * Show the dialog to modify the information for an outing.
 * 
 * @author Andrzej JR Hunt
 * 
 */
public class OutingDialog extends JDialog {

	private JDialog dialog = this;

	/**
	 * Whether this dialog is dealing wit a new outing, or else whether we are
	 * modifying an outing.
	 */
	private boolean isNewOuting;
	private DateFormat format = new SimpleDateFormat("HH:mm");

	// TEMPORARY Method
	public static void main(String[] args) throws Exception {
		javax.swing.UIManager
				.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		try {
			Runtime.getRuntime().exec("rm -rf ./database/srl");
		} catch (IOException e) {
			e.printStackTrace();
		}

		Database d = org.ahunt.simpleRowLog.db.simpleDB.Database.getInstance();
		d.addBoat("Andy", "4x+", true);
		d.addBoat("Andrew", "4x+", true);
		d.addMember("Hunt", "Andrew", new Date(), 1);
		d.addMember("Hunt", "James", new Date(), 1);
		d.addMember("Cricket", "Andrew", new Date(), 1);
		d.addMember("Hunt", "Kenneth", new Date(), 1);
		d.addMember("Hunt", "Kasia", new Date(), 1);
		d.addMember("Random", "Guy", new Date(), 1);
		d.addMember("Random", "Girl", new Date(), 1);

		System.out.println("2");
		OutingDialog od = new OutingDialog(d);
		od.doNewOuting();
		OutingInfo[] outings = d.getOutings(new Date());
		for (OutingInfo o : outings) {
			System.out.println(o.getBoat().getName() + ":"
					+ o.getRowers()[0].getName());
		}
		od.doNewOuting();
		outings = d.getOutings(new Date());
		for (OutingInfo o : outings) {
			System.out.println(o.getBoat().getName() + ":"
					+ o.getRowers()[0].getName());
		}
		System.exit(0);
	}

	/** A list of boats the Dialog is to use. */
	private BoatInfo[] boats;
	/** A list of members. */
	private MemberInfo[] members;

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

	private JFormattedTextField distanceEntry = new JFormattedTextField(1);
	private JLabel distanceEntryLabel = new JLabel();
	private JTextArea destinationEntry = new JTextArea(3, 32);
	private JLabel destinationEntryLabel = new JLabel();
	private JTextArea commentEntry = new JTextArea(3, 32);
	private JLabel commentEntryLabel = new JLabel();

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
	private ResourceBundle rb;

	/**
	 * Create a new OutingDialog. It will not be shown by default. Since this is
	 * an often used dialog it is recommended that one is created at the
	 * beginning of a session and reused.
	 */
	public OutingDialog(Database db) {
		this.db = db;
		rb = ResourceBundle.getBundle("gui");

		// Button's icons
		confirmButton.setIcon(new ImageIcon("img/icons/gnome-confirm-24.png"));
		cancelButton.setIcon(new ImageIcon("img/icons/gnome-cancel-24.png"));
		// Boat input
		boatEntryLabel.setText("<html><b>" + rb.getString("outing.boat")
				+ ":</b></html>");
		boatEntry = new SuggestiveTextField(this, true, null, 16);

		// Bottom buttons
		confirmButton.setText(rb.getString("outing.confirm"));
		confirmButton.addActionListener(new ButtonListener());
		cancelButton.addActionListener(new ButtonListener());

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
						l.createSequentialGroup().addComponent(boatEntryLabel)
								.addComponent(boatEntry).addPreferredGap(
										LayoutStyle.ComponentPlacement.RELATED,
										GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)).addComponent(
						rowerPanel).addComponent(infoPanel).addGroup(
						l.createSequentialGroup().addPreferredGap(
								LayoutStyle.ComponentPlacement.RELATED,
								GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(confirmButton).addComponent(
										cancelButton))));
		l.setVerticalGroup(l.createSequentialGroup().addGroup(
				l.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(boatEntryLabel).addComponent(boatEntry))
				.addComponent(rowerPanel).addComponent(infoPanel).addGroup(
						l.createParallelGroup(GroupLayout.Alignment.TRAILING)
								.addComponent(confirmButton).addComponent(
										cancelButton)));
		// Routine (and boring) window setup
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
			} else {
				rowerEntry[i] = new SuggestiveTextField(rowerPanel, false,
						null, 32);
			}
			// rowerEntry[i] = new JTextField(32);
			rowerEntryLabel[i] = new JLabel("<html><b>"
					+ MessageFormat.format(rb.getString("outing.rowerNum")
							+ ":</b></html>", i + 1));
			// new EntryListener(rowerPanel, rowerEntry[i], i);
		}
		coxEntry = new SuggestiveTextField(rowerPanel, false, null, 32);
		coxEntryLabel = new JLabel("<html><b><i>" + rb.getString("outing.cox")
				+ ":</i></b></html>");
		// new EntryListener(rowerPanel, coxEntry, -1);
		rowerPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK), rb
				.getString("outing.rowers")));
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
				r.createParallelGroup().addComponent(rowerEntryLabel[0])
						.addComponent(rowerEntry[0])).addPreferredGap(
				LayoutStyle.ComponentPlacement.RELATED,
				GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(
				r.createParallelGroup().addComponent(rowerEntryLabel[1])
						.addComponent(rowerEntry[1])).addPreferredGap(
				LayoutStyle.ComponentPlacement.RELATED,
				GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(
				r.createParallelGroup().addComponent(rowerEntryLabel[2])
						.addComponent(rowerEntry[2])).addPreferredGap(
				LayoutStyle.ComponentPlacement.RELATED,
				GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(
				r.createParallelGroup().addComponent(rowerEntryLabel[3])
						.addComponent(rowerEntry[3])).addPreferredGap(
				LayoutStyle.ComponentPlacement.RELATED,
				GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(
				r.createParallelGroup().addComponent(rowerEntryLabel[4])
						.addComponent(rowerEntry[4])).addPreferredGap(
				LayoutStyle.ComponentPlacement.RELATED,
				GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(
				r.createParallelGroup().addComponent(rowerEntryLabel[5])
						.addComponent(rowerEntry[5])).addPreferredGap(
				LayoutStyle.ComponentPlacement.RELATED,
				GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(
				r.createParallelGroup().addComponent(rowerEntryLabel[6])
						.addComponent(rowerEntry[6])).addPreferredGap(
				LayoutStyle.ComponentPlacement.RELATED,
				GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(
				r.createParallelGroup().addComponent(rowerEntryLabel[7])
						.addComponent(rowerEntry[7])).addPreferredGap(
				LayoutStyle.ComponentPlacement.RELATED,
				GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(
				r.createParallelGroup().addComponent(coxEntryLabel)
						.addComponent(coxEntry))

		);
		return rowerPanel;
	}

	private JPanel setupInfoPanel() {
		JPanel ret = new JPanel();
		distanceEntryLabel.setText("<html><b>"
				+ rb.getString("outing.distance") + ":</b></html>");
		destinationEntryLabel.setText("<html><b>"
				+ rb.getString("outing.destination") + ":</b></html>");
		commentEntryLabel.setText("<html><b>" + rb.getString("outing.comment")
				+ ":</b></html>");
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
		timeOutEntryLabel = new JLabel("<html><b>"
				+ rb.getString("outing.timeOut") + ":</b></html>");
		// timeInEntry = new JFormattedTextField(new DateFormatter(
		// new SimpleDateFormat("HH:mm")));
		try {
			timeInEntry = new FormattedTimeField(false);
		} catch (Exception e) {
			// TODO: clean
		}
		timeInEntry.setHorizontalAlignment(JTextField.CENTER);
		timeInEntryLabel = new JLabel("<html><b>"
				+ rb.getString("outing.timeIn") + ":</b></html>");
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
				l.createParallelGroup().addComponent(distanceEntryLabel)
						.addComponent(distanceEntry)).addPreferredGap(
				LayoutStyle.ComponentPlacement.RELATED,
				GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(
				l.createParallelGroup().addComponent(timeOutEntryLabel)
						.addComponent(timeOutEntry).addComponent(
								timeInEntryLabel).addComponent(timeInEntry))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
						GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(
						l.createParallelGroup().addComponent(
								destinationEntryLabel).addComponent(
								destinationEntryScrollPane)).addPreferredGap(
						LayoutStyle.ComponentPlacement.RELATED,
						GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(
						l.createParallelGroup().addComponent(commentEntryLabel)
								.addComponent(commentEntryScrollPane))

		);
		return ret;
	}

	/**
	 * Show the dialog in order to add a new outing.
	 */
	public void doNewOuting() {
		this.outing = null;
		isNewOuting = true;
		this.setTitle(rb.getString("outingDialog.title.newOuting"));
		cancelButton.setText(rb.getString("outing.cancel_new"));

		// Clear all boxes
		boatEntry.setText("");
		for (int i = 0; i < 8; i++) {
			rowerEntry[i].setText("");
		}
		distanceEntry.setText("");
		timeOutEntry.setText("");
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
	public void doModifyOuting(OutingInfo outing) {
		this.outing = outing;
		updateDBInfo();
		isNewOuting = false;
		cancelButton.setText(rb.getString("outing.cancel_modify"));
		// TODO: if cancel is pressed ask for confirmation.
		// Clear all boxes
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
			timeInEntry.setText("");
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
		if (boatEntry.isValid() && coxEntry.isValid()
				&& rowerEntry[0].isValid() && rowerEntry[1].isValid()
				&& rowerEntry[2].isValid() && rowerEntry[3].isValid()
				&& rowerEntry[4].isValid() && rowerEntry[5].isValid()
				&& rowerEntry[6].isValid() && rowerEntry[7].isValid()
				&& timeOutEntry.isValid() && timeInEntry.isValid()
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

		public boolean isValid() {
			return isValid;
		}

		public boolean isFilled() {
			return isFilled;
		}

	}

	private class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (arg0.getSource() == cancelButton && isNewOuting) {
				// showConfirmDialog(cancelNewOuting)
				System.out.println("deleting new outing");
				setVisible(false); // Return the dialog.
			} else if (arg0.getSource() == cancelButton && !isNewOuting) {
				// TODO: dialog
				setVisible(false);
			} else if (arg0.getSource() == confirmButton) {
				if (isValidInput()) {
					System.out.println("Saving new outing");
					try {
						int distance;
						if (distanceEntry.getText().equals("")) {
							distance = 0;
						} else {
							distance = Integer
									.parseInt(distanceEntry.getText());
						}
						if (isNewOuting) {
							System.out.println("new outing");
							db.addOuting(new Date(), new int[] {
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
									destinationEntry.getText(), boatEntry
											.getText(), distance);
						} else {
							System.out.println("modify");
							db.modifyOuting(outing.getId(), outing.getDay()
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
									destinationEntry.getText(), boatEntry
											.getText(), distance);
						}

						// Save the data
						setVisible(false); // Return the dialog.
					} catch (InvalidEntryException e) {
						// TODO: inexplicable error.
					}
				} else {
					// Warn about incorrect data, and where.
					// TODO: update this outdated method.
					JOptionPane.showMessageDialog(null, rb
							.getString("outing.invalid_input"), rb
							.getString("outing.invalid_input.title"),
							JOptionPane.ERROR_MESSAGE);
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
				return m.getKey();
			}
		}
		return 1; // Guest elsewise.
	}

	private class InvalidEntryException extends Exception {

		public InvalidEntryException(String string) {
			super(string);
			// TODO Auto-generated constructor stub
		}

	}

}
