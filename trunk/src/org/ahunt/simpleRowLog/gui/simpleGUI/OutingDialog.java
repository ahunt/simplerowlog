package org.ahunt.simpleRowLog.gui.simpleGUI;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ResourceBundle;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.ahunt.simpleRowLog.interfaces.Database;

public class OutingDialog extends JDialog {

	// TEMPORARY Method
	public static void main(String[] args) {
		System.out.println("test");
		OutingDialog od = new OutingDialog(null);
		od.doNewOuting();
		System.out.println(od.getSize());
		od.doNewOuting();
		System.exit(0);
	}

	private JTextField boatEntry = new JTextField();
	private JLabel boatEntryLabel = new JLabel();
	
	private JButton confirmButton = new JButton();
	private JButton cancelButton = new JButton();

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

		// Boat input
		boatEntryLabel.setText(rb.getString("outing.boat") + ":");
		boatEntry.getDocument().addDocumentListener(new BoatEntryListener());
		// Rower input
		JPanel rowerPanel = new JPanel();
		rowerPanel.add(new JLabel("Hello"));
		rowerPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK), rb.getString("outing.rowers")));
		// Bottom buttons
		confirmButton.setText(rb.getString("outing.confirm"));
		
		// Routine (and boring) window setup
		this.setSize(720, 660);
		this.setModal(true);
		boatEntry.setMaximumSize(new Dimension(180, 32));

		// Layouting
		GroupLayout l = new GroupLayout(this.getContentPane());
		this.getContentPane().setLayout(l);
		l.setAutoCreateGaps(true);
		l.setAutoCreateContainerGaps(true);
		// Grouping in layout
		l.setHorizontalGroup(l.createSequentialGroup().addGroup(
				l.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(l.createSequentialGroup()
							.addComponent(boatEntryLabel)
							.addComponent(boatEntry)
						)
						.addComponent(rowerPanel)
						.addGroup(l.createSequentialGroup()
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
									GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(confirmButton)
							.addComponent(cancelButton)
						)
			));
		l.setVerticalGroup(l.createSequentialGroup().addGroup(
				l.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(boatEntryLabel).addComponent(boatEntry))
						.addComponent(rowerPanel)
						.addGroup(l.createParallelGroup(GroupLayout.Alignment.TRAILING)
							.addComponent(confirmButton)
							.addComponent(cancelButton)	
						)						
		);


	}

	/**
	 * Show the dialog in order to add a new outing.
	 */
	public void doNewOuting() {
		this.setTitle(rb.getString("outingDialog.title.newOuting"));
		cancelButton.setText(rb.getString("outing.cancel_new"));
		
		this.setVisible(true);
	}

	/**
	 * Show the dialog to modify an existing outing. This retrieves all data so
	 * far from the database.
	 * 
	 * @param id
	 *            The id of the outing.
	 */
	public void doModifyOuting(long id) {
		cancelButton.setText(rb.getString("outing.cancel_modify"));
		// TODO: if cancel is pressed ask for confirmation.

	}

	private class BoatEntryListener implements DocumentListener {

		@Override
		public void changedUpdate(DocumentEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void insertUpdate(DocumentEvent arg0) {
			// TODO Auto-generated method stub
			System.out.println(boatEntry.getText());
			// JPopupMenu jp = new JPopupMenu();
			// jp.add(new JMenuItem("Hi"));
			// jp.setVisible(true);
		}

		@Override
		public void removeUpdate(DocumentEvent arg0) {
			// TODO Auto-generated method stub

		}

	}
}
