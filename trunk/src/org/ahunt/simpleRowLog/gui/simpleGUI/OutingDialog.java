package org.ahunt.simpleRowLog.gui.simpleGUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import javax.swing.text.DateFormatter;
import javax.swing.text.InternationalFormatter;
import javax.swing.text.MaskFormatter;

import org.ahunt.simpleRowLog.common.BoatInfo;
import org.ahunt.simpleRowLog.common.MemberInfo;
import org.ahunt.simpleRowLog.interfaces.Database;

public class OutingDialog extends JDialog {

	// TEMPORARY Method
	public static void main(String[] args) {
		System.out.println("test");
		
		Database d = org.ahunt.simpleRowLog.db.simpleDB.Database.getInstance();
		d.addBoat("Andy", "4x+", true);
		d.addBoat("Andrew","4x+", true);
		
		OutingDialog od = new OutingDialog(d);
		od.doNewOuting();
		System.out.println(od.getSize());
		od.doNewOuting();
		System.exit(0);
	}

	/** A list of boats the Dialog is to use. */
	private BoatInfo[] boats;
	/** A list of members. */
	private MemberInfo[] members;
	
	
	private JTextField boatEntry = new JTextField(16);
	private JLabel boatEntryLabel = new JLabel();
	private JPopupMenu boatEntryPopup = new JPopupMenu();
	
	private JTextField[] rowerEntry = new JTextField[8];
	private JLabel[] rowerEntryLabel = new JLabel[8];
	private JTextField coxEntry;
	private JLabel coxEntryLabel;
	
	private JFormattedTextField timeOutEntry;
	private JLabel timeOutEntryLabel;
	private JFormattedTextField timeInEntry;
	private JLabel timeInEntryLabel;
	
	private JTextField distanceEntry = new JTextField(1);
	private JLabel distanceEntryLabel = new JLabel();
	private JTextArea destinationEntry = new JTextArea(3,32);
	private JLabel destinationEntryLabel = new JLabel();
	private JTextArea commentEntry = new JTextArea(3,32);
	private JLabel commentEntryLabel = new JLabel();
	
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
		
		// Button's icons
		confirmButton.setIcon(new ImageIcon("img/icons/gnome-confirm-24.png"));
		cancelButton.setIcon(new ImageIcon("img/icons/gnome-cancel-24.png"));
		// Boat input
		boatEntryLabel.setText("<html><b>" + rb.getString("outing.boat") 
				+ ":</b></html>");
		BoatEntryListener boatListener = new BoatEntryListener(this,
				boatEntry);
		boatEntry.getDocument().addDocumentListener(boatListener);

		
		// Bottom buttons
		confirmButton.setText(rb.getString("outing.confirm"));

		JPanel rowerPanel = setupRowerPanel();
		JPanel infoPanel = setupInfoPanel();
		
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
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
									GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						)
						.addComponent(rowerPanel)
						.addComponent(infoPanel)
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
						.addComponent(infoPanel)
						.addGroup(l.createParallelGroup(GroupLayout.Alignment.TRAILING)
							.addComponent(confirmButton)
							.addComponent(cancelButton)	
						)						
		);
		// Routine (and boring) window setup
		this.setModal(true);
		this.pack();
		this.setResizable(false);
	}

	/**
	 * Setup the panel for entering the crew for an outing.
	 * @return
	 */
	private JPanel setupRowerPanel() {
		// Rower input
		for (int i = 0; i < 8; i++) { // Create and fill the entries
			rowerEntry[i] = new JTextField(32);
			rowerEntryLabel[i] = new JLabel("<html><b>"+ MessageFormat.format(
					rb.getString("outing.rowerNum") + ":</b></html>", i+1));
		}	
		coxEntry = new JTextField(32);
		coxEntryLabel = new JLabel("<html><b><i>" + rb.getString("outing.cox")
				+":</i></b></html>");
		JPanel rowerPanel = new JPanel(); // Holding all the items
		rowerPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK),
				rb.getString("outing.rowers")));
		GroupLayout r = new GroupLayout(rowerPanel); // Layouting
		rowerPanel.setLayout(r);
		
		r.setHorizontalGroup(r.createSequentialGroup()
				.addGroup(r.createParallelGroup()
						.addComponent(rowerEntryLabel[0])
						.addComponent(rowerEntryLabel[1])
						.addComponent(rowerEntryLabel[2])
						.addComponent(rowerEntryLabel[3])
						.addComponent(rowerEntryLabel[4])
						.addComponent(rowerEntryLabel[5])
						.addComponent(rowerEntryLabel[6])
						.addComponent(rowerEntryLabel[7])
						.addComponent(coxEntryLabel)
				)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
									GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addGroup(r.createParallelGroup()
						.addComponent(rowerEntry[0])
						.addComponent(rowerEntry[1])
						.addComponent(rowerEntry[2])
						.addComponent(rowerEntry[3])
						.addComponent(rowerEntry[4])
						.addComponent(rowerEntry[5])
						.addComponent(rowerEntry[6])
						.addComponent(rowerEntry[7])
						.addComponent(coxEntry)
				)
		);
		r.setVerticalGroup(r.createSequentialGroup()
				.addGroup(r.createParallelGroup()
						.addComponent(rowerEntryLabel[0])
						.addComponent(rowerEntry[0])
				)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
									GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addGroup(r.createParallelGroup()
						.addComponent(rowerEntryLabel[1])
						.addComponent(rowerEntry[1])
				)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
									GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addGroup(r.createParallelGroup()
						.addComponent(rowerEntryLabel[2])
						.addComponent(rowerEntry[2])
				)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
									GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addGroup(r.createParallelGroup()
						.addComponent(rowerEntryLabel[3])
						.addComponent(rowerEntry[3])
				)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
									GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addGroup(r.createParallelGroup()
						.addComponent(rowerEntryLabel[4])
						.addComponent(rowerEntry[4])
				)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
									GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addGroup(r.createParallelGroup()
						.addComponent(rowerEntryLabel[5])
						.addComponent(rowerEntry[5])
				)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
									GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addGroup(r.createParallelGroup()
						.addComponent(rowerEntryLabel[6])
						.addComponent(rowerEntry[6])
				)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
									GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addGroup(r.createParallelGroup()
						.addComponent(rowerEntryLabel[7])
						.addComponent(rowerEntry[7])
				)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
									GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addGroup(r.createParallelGroup()
						.addComponent(coxEntryLabel)
						.addComponent(coxEntry)
				)
				
		);
		return rowerPanel;
	}
	
	private JPanel setupInfoPanel() {
		JPanel ret = new JPanel();
		distanceEntryLabel.setText("<html><b>" + rb.getString("outing.distance") 
				+ ":</b></html>");
		destinationEntryLabel.setText("<html><b>" + rb.getString(
				"outing.destination") + ":</b></html>");
		commentEntryLabel.setText("<html><b>" +rb.getString("outing.comment")
				+ ":</b></html>");
		// Prepare the scroll areas
		JScrollPane destinationEntryScrollPane =
			new JScrollPane(destinationEntry);
		destinationEntryScrollPane.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		JScrollPane commentEntryScrollPane = 
			new JScrollPane(commentEntry);
		commentEntryScrollPane.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		// Time in and out
		timeOutEntry = new JFormattedTextField(new DateFormatter(
				new SimpleDateFormat("HH:mm")));
		timeOutEntryLabel = new JLabel("<html><b>" +
				rb.getString("outing.timeOut") + ":</b></html>");
		timeInEntry = new JFormattedTextField(new DateFormatter(
				new SimpleDateFormat("HH:mm")));
		timeInEntryLabel = new JLabel("<html><b>" +
				rb.getString("outing.timeIn") + ":</b></html>");
		// Layout
		GroupLayout l = new GroupLayout(ret);
		ret.setLayout(l);
		l.setAutoCreateGaps(true);
		l.setHorizontalGroup(l.createSequentialGroup()
				.addGroup(l.createParallelGroup()
						.addComponent(distanceEntryLabel)
						.addComponent(timeOutEntryLabel)
						.addComponent(destinationEntryLabel)
						.addComponent(commentEntryLabel)
				)
				.addGroup(l.createParallelGroup()
						.addGroup(l.createSequentialGroup()
								.addComponent(distanceEntry)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
										GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						)
						.addGroup(l.createSequentialGroup()
								.addComponent(timeOutEntry)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
										GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(timeInEntryLabel)
								.addComponent(timeInEntry)
								.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,
										GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						)
						.addComponent(destinationEntryScrollPane)
						.addComponent(commentEntryScrollPane)
				)
				
		);
		l.setVerticalGroup(l.createSequentialGroup()
				.addGroup(l.createParallelGroup()
						.addComponent(distanceEntryLabel)
						.addComponent(distanceEntry)
				)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
						GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)	
				.addGroup(l.createParallelGroup()
						.addComponent(timeOutEntryLabel)
						.addComponent(timeOutEntry)
						.addComponent(timeInEntryLabel)
						.addComponent(timeInEntry)
				)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
						GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)				
				.addGroup(l.createParallelGroup()
						.addComponent(destinationEntryLabel)
						.addComponent(destinationEntryScrollPane)
				)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
						GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)	
				.addGroup(l.createParallelGroup()
						.addComponent(commentEntryLabel)
						.addComponent(commentEntryScrollPane)
				)
				
		);
		return ret;
	}
	/**
	 * Show the dialog in order to add a new outing.
	 */
	public void doNewOuting() {
		this.setTitle(rb.getString("outingDialog.title.newOuting"));
		cancelButton.setText(rb.getString("outing.cancel_new"));
		
		boats = db.getBoats(true);
		members = db.getMembers();
		
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

	private class BoatEntryListener implements DocumentListener, MenuKeyListener {

		private JDialog dialog;
		private JComponent textEntry;
		private Point popupLocation;
		
		public BoatEntryListener(JDialog dialog, JComponent textEntry) {
			this.dialog = dialog;
			this.textEntry = textEntry;
			boatEntryPopup.addMenuKeyListener(this);
		}
		
		private void setupLocation() {
			Point textLocation = textEntry.getLocation();
			System.out.println(textLocation);
			Dimension textSize =  textEntry.getSize();
			System.out.println(textSize);
			Insets insets = dialog.getInsets();
			System.out.println(insets);
			popupLocation = new Point(textLocation.x + insets.left,
					textLocation.y + textSize.height + insets.top);
		}
		
		@Override
		public void changedUpdate(DocumentEvent arg0) {
			insertUpdate(arg0);

		}

		@Override
		public void insertUpdate(DocumentEvent arg0) {
			if (popupLocation == null) {
				setupLocation();
			}
			boatEntryPopup.setVisible(false);
			boatEntryPopup.removeAll();
			// TODO  Auto-generated method stub
			System.out.println(boatEntry.getText());
			String entry = boatEntry.getText();
			int entryLength = entry.length();
			for (int i = 0; i < boats.length; i++) {
				if (boats[i].getName().length() >= entryLength && 
						boats[i].getName().substring(0, entryLength).compareToIgnoreCase(entry) == 0) {
					boatEntryPopup.add(new JMenuItem(boats[i].getName()));
				}
			}
			if (boatEntryPopup.getComponentCount() != 0) {
				boatEntryPopup.show(dialog, popupLocation.x, popupLocation.y);
//				boatEntryPopup.setSelected(boatEntryPopup);
			}
			System.out.println("Focus:" +dialog.getFocusOwner());

		}

		@Override
		public void removeUpdate(DocumentEvent arg0) {
			insertUpdate(arg0);
		}

		@Override
		public void menuKeyPressed(MenuKeyEvent arg0) {

			
		}

		@Override
		public void menuKeyReleased(MenuKeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void menuKeyTyped(MenuKeyEvent arg0) {
//			char[] c = new char[1];
//			c[0] = arg0.getKeyChar();
//			if (c[0] != KeyEvent.CHAR_UNDEFINED) {
//				boatEntry.setText(boatEntry.getText() + new String(c));
//			}
			boatEntryPopup.setEnabled(false);
			boatEntryPopup.setSelected(boatEntry);
			boatEntryPopup.setVisible(false);
			boatEntry.requestFocus();
//			while (!boatEntry.hasFocus()) {
//			}
			System.out.println("hasFocus:"+boatEntry.hasFocus());
			System.out.println(dialog.getFocusOwner());
			
			KeyboardFocusManager.getCurrentKeyboardFocusManager().redispatchEvent(boatEntry, arg0);
			
			// TODO: now enable selection of single items
			
//			boatEntry.dispatchEvent(new KeyEvent(dialog, 
//					KeyEvent.KEY_TYPED, new Date().getTime(), 0, 
//					KeyEvent.VK_UNDEFINED, arg0.getKeyChar()));

		}

	}
}
