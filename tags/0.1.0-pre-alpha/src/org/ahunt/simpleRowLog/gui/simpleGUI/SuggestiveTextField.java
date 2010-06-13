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
 *	24/01/2010:	Created to clean up the mess in OutingDialog.
 */

package org.ahunt.simpleRowLog.gui.simpleGUI;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Date;

import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;

public class SuggestiveTextField extends JTextField implements
		DocumentListener, MenuKeyListener, ActionListener {

	private Point popupLocation;
	private JPopupMenu popup = new JPopupMenu();
	private Container container;
	private int entryNumber;
	private boolean mustBeFilled;
	private boolean validEntry;
	private boolean isFilled;
	private String[] options;

	public SuggestiveTextField(Container container, boolean mustBeFilled,
			String[] options, int columns) {
		super(columns);
		this.container = container;
		this.mustBeFilled = mustBeFilled;
		this.options = options;
		getDocument().addDocumentListener(this);
		popup.addMenuKeyListener(this);

	}

	/**
	 * Calculate the position of the popup. Only call this AFTER you are sure
	 * that the dialog is packed, since the positions won't be set otherwise.
	 */
	private void calculateLocation() {
		// Get locations and dimensions.
		Point textLocation = getLocation();
		Dimension textSize = getSize();
		Insets insets = container.getInsets();
		System.out.println("Inset" + insets);
		// Position popup at bottom left corner of the entry.
		if (container.getClass().getName().equals(OutingDialog.class.getName())) {
			popupLocation = new Point(textLocation.x + insets.left,
					textLocation.y + textSize.height + insets.top);
		} else {
			popupLocation = new Point(textLocation.x, textLocation.y
					+ textSize.height);
		}
	}

	@Override
	public void changedUpdate(DocumentEvent arg0) {
		if (popupLocation == null) { // Check that location is done.
			calculateLocation();
		}
		popup.removeAll(); // Clear previous items
		String entryText = getText(); // User input
		int entryLength = entryText.length();

		// Validity checking: we assume false beforehand, then iterate
		// through the available names, checking whether one is true.
		validEntry = false;
		// Iterate through the names, add possible ones to list for popup.
		for (String s : options) { // Go through all.
			// Match what has been typed so far:
			if (s.length() >= entryLength
					&& s.substring(0, entryLength).compareToIgnoreCase(
							entryText) == 0) {
				JMenuItem jm = new JMenuItem(s);
				jm.addActionListener(this);
				popup.add(jm); // Add
			}
			// Validation: Either correct, or empty (for any apart from
			// first rower
			if (s.compareToIgnoreCase(entryText) == 0
					| (entryLength == 0 && !mustBeFilled)) {
				// Input is valid, save
				validEntry = true;
			}
			// If strings capitalisation doesn't match then autocorrect
			if (s.compareToIgnoreCase(entryText) == 0 && !s.equals(entryText)) {
				setText(s);
			}
		}
		if (entryLength > 0) {
			isFilled = true;
		} else {
			isFilled = false;
		}
		// Colour the textBox as needed
		if (validEntry) {
			setBackground(Color.WHITE); // Formerly green
		} else if (entryLength == 0 && !mustBeFilled) {
			setBackground(Color.WHITE);
		} else if (entryLength == 0 && mustBeFilled) {
			setBackground(Color.YELLOW);
		} else {
			setBackground(Color.RED);
		}
		// If one item or more then show.
		if (popup.getComponentCount() != 0
				&& container.isShowing()) {
			popup.show(container, popupLocation.x, popupLocation.y);
		}
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		// Treat the same as a modification.
		changedUpdate(arg0);
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		// Treat the same as a modification.
		changedUpdate(arg0);
	}

	@Override
	public void menuKeyPressed(MenuKeyEvent arg0) {
		if ((arg0.getKeyCode() == KeyEvent.VK_ENTER | arg0.getKeyCode() == KeyEvent.VK_TAB)
				&& popup.getComponentCount() == 1) {
			JMenuItem c = (JMenuItem) popup.getComponents()[0];
			setText(c.getText());
			popup.setEnabled(false); // Disable
			popup.setVisible(false); // And hide
			dispatchEvent(new KeyEvent(this, KeyEvent.KEY_PRESSED, new Date()
					.getTime(), 0, KeyEvent.VK_TAB, KeyEvent.CHAR_UNDEFINED));
		} else if (arg0.getKeyCode() == KeyEvent.VK_BACK_SPACE
				| arg0.getKeyCode() == KeyEvent.VK_TAB) {
			menuKeyTyped(arg0); // Pass on to text field as usual
		}
		// Nothing, we only want typed keys.
	}

	@Override
	public void menuKeyReleased(MenuKeyEvent arg0) {
		// Nothing, we only want typed keys.
	}

	@Override
	public void menuKeyTyped(MenuKeyEvent arg0) {
		popup.setEnabled(false); // Disable
		popup.setVisible(false); // And hide
		// Pass on to the text entry.
		KeyboardFocusManager.getCurrentKeyboardFocusManager().redispatchEvent(
				this, arg0);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		setText(arg0.getActionCommand());
		popup.setEnabled(false); // Disable
		popup.setVisible(false); // And hide
		dispatchEvent(new KeyEvent(this, KeyEvent.KEY_PRESSED, new Date()
				.getTime(), 0, KeyEvent.VK_TAB, KeyEvent.CHAR_UNDEFINED));
	}

	public void setOptions(String[] options) {
		this.options = options;
	}

}
