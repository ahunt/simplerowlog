/*
 *    This file is part of simple rowLog: the open rowing logbook.
 *    Copyright (C) 2009  Andrzej JR Hunt
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
 *	23/08/2009:	Changelog added.
 */

package org.ahunt.simpleRowLog.gui.simpleGUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import java.util.Date;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.ahunt.simpleRowLog.common.MemberInfo;
import org.ahunt.simpleRowLog.common.OutingInfo;
import org.ahunt.simpleRowLog.conf.Configuration;
import org.ahunt.simpleRowLog.interfaces.Database;


/**
 * A simple graphical user Interface for simple rowLog.
 * 
 * @author Andrzej JR Hunt
 * @version 0.01 - 03. November 2008
 */
public class SimpleGUI extends JFrame {

	static final long serialVersionUID = 1l;

	// Get the language file
	private ResourceBundle rb = ResourceBundle.getBundle("gui");
	
	// File Menu
	private JMenu menuFile = new JMenu();

	// File Menu: Exit
	private JMenuItem menuFileExit = new JMenuItem();

	// File Menu: New Member
	private JMenuItem menuFileNewMember = new JMenuItem();

	// Options Menu
	private JMenu menuOptions = new JMenu();

	// Options Menu: Admin mode
	private JMenuItem menuOptionsAdmin = new JMenuItem();

	// Help menu
	private JMenu menuHelp = new JMenu();

	// Help Menu: Help contents
	private JMenuItem menuHelpHelp = new JMenuItem();

	// Help Menu: About
	private JMenuItem menuHelpAbout = new JMenuItem();

	// Outings table.
	private JScrollPane outingTablePane;
	private JTable outingTable;
	private OutingTableManager outingTableManager;
	
	private JButton editOutingButton = new JButton();
	private JButton newOutingButton = new JButton();
	
	// The database
	private Database db;
	
	// The current date selected in the window. (Not specifically today's date.)
	private Date current = new Date();

	// Configuration file.
	private Configuration conf;
	
	/**
	 * Temporary test method.
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Runtime.getRuntime().exec("rm -rf ./database/srl");
		Database bc = org.ahunt.simpleRowLog.db.simpleDB.Database.getInstance();
		bc.addBoat("Anna", "8+", true);
		bc.addBoat("Bob", "1x", true);
		System.out.println();
		bc.addOuting(new Date(), new short[] {bc.addMember("Blog", "James", new Date(), (short) 1), bc.addMember("Blog", "Mike", new Date(), (short) 1),bc.addMember("Blog", "Jane", new Date(), (short) 1),1,1,1,1,1}, bc.addMember("Blog", "May", new Date(), (short) 1), new Date(), null, "Blogtown", null, "Anna", 0);
		bc.addOuting(new Date(), new short[] {bc.addMember("Bump", "John", new Date(), (short) 1)}, (short) 0, new Date(), new Date(new Date().getTime() + 3600000), "Middle Earth", "Fun trip!", "Bob", 12);
		new SimpleGUI(bc);
	}
	
	/**
	 * Create the gui.
	 * @param db The database from which data is to be requested.
	 */
	public SimpleGUI(Database db) {
		conf = Configuration.getConf("simpleGUI");
		this.db = db;
		setupMenus();
		updateLanguages();
		reloadConfig();
		
		// Set up the table. TODO: check whether height is correct.
		outingTableManager = new OutingTableManager();
		outingTable = new JTable(outingTableManager) {
			private static final long serialVersionUID = 1L;
			//  Get the class of the column in use.
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
            {
                    Component c = super.prepareRenderer(renderer, row, column);
                    if (!c.getBackground().equals(getSelectionBackground())){
                            Object timeOut = getModel().getValueAt(row, 3);
                            // Sets colours for completed rows.
                            c.setBackground( timeOut == null ?
                            		Color.RED : Color.WHITE );
                    }
                    return c;
            }
		};
		
		
		outingTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	    int vColIndex = 1;
	    TableColumn col = outingTable.getColumnModel().getColumn(vColIndex);
	    int width = 200;
	    col.setPreferredWidth(width);
	    outingTable.getColumnModel().getColumn(0).setPreferredWidth(50);

		outingTablePane = new JScrollPane(outingTable);
		outingTable.setRowHeight(100);
		
		outingTablePane.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		outingTablePane.setHorizontalScrollBarPolicy(
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		setupLayout();
		setVisible(true);
	}
	
	/**
	 * Set up the layout as required.
	 */
	private void setupLayout() {
		Container p = this.getContentPane();
		GroupLayout l = new GroupLayout(p);
		p.setLayout(l);
		l.setAutoCreateGaps(true);
		l.setAutoCreateContainerGaps(true);

//		outingTable.setMinimumSize(new Dimension(400,300));
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		l.setVerticalGroup(
				l.createSequentialGroup()
					.addComponent(outingTablePane, 300, d.height, d.height)
					.addGroup(l.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(editOutingButton)
							.addComponent(newOutingButton)));
		l.setHorizontalGroup(
			l.createParallelGroup(GroupLayout.Alignment.TRAILING)
				.addComponent(outingTablePane, 400, d.width, d.width)
				.addGroup(l.createSequentialGroup()
						.addComponent(editOutingButton)
						.addComponent(newOutingButton)));
		// TODO: implement.
	}
	
	/**
	 * Tells the window to take all chaged settings into account.
	 * 
	 */
	private void reloadConfig() {
		if (conf.getProperty("fullscreen").equals("true")) {
			setFullScreen(true);
		}
	}

	/**
	 * Sets up the menus: Adds them, localises them.
	 * 
	 */
	private void setupMenus() {
		setJMenuBar(new JMenuBar());
		JMenuBar mb = getJMenuBar();
		// File menu
		mb.add(menuFile);
		menuFile.add(menuFileExit);
		menuFile.add(menuFileNewMember);
		// Options menu
		mb.add(menuOptions);
		menuOptions.add(menuOptionsAdmin);
		// Help menu
		mb.add(menuHelp);
		menuHelp.add(menuHelpHelp);
		menuHelp.add(menuHelpAbout);
	}

	/**
	 * Set the window to be full screen.
	 * 
	 * @param setFullScreen
	 *            True if the window is to be fullscreen, false if not to be
	 *            full screen.
	 */
	private void setFullScreen(boolean setFullScreen) {
		setUndecorated(setFullScreen);
		setResizable(!setFullScreen);
		setSize(Toolkit.getDefaultToolkit().getScreenSize());
		setLocation(0, 0);
	}

	/**
	 * Localises the menus, also hides/shows any items that have been edited in
	 * the settings.
	 * 
	 */
	private void updateMenuLanguages() {
		// File menu
		menuFile.setText(rb.getString("file"));
		menuFileExit.setText(rb.getString("file.exit"));
		menuFileNewMember.setText(rb.getString("file.newMember"));
		// Options menu
		menuOptions.setText(rb.getString("options"));
		menuOptionsAdmin.setText(rb.getString("options.admin"));
		// Help menu
		menuHelp.setText(rb.getString("help"));
		menuHelpHelp.setText(rb.getString("help.help"));
		menuHelpAbout.setText(rb.getString("help.about"));
	}

	private void updateLanguages() {
		updateMenuLanguages();
		String prepend = "<html><font size=+2>";
		String append = "</font></html>";
		editOutingButton.setText(prepend + rb.getString("main.editOuting")
				+ append);
		newOutingButton.setText(prepend + rb.getString("main.newOuting")
				+ append);
	}

	class ExitListener extends WindowAdapter {
		// TODO: Also implement exit button + behaviour
		public void windowClosing(WindowEvent e) {
			onClose();
		}

		private void onClose() {
			// TODO: stuff
			// TODO: some method has to be called to clean up and save
			//       data that changes e.g. config.
		}
	}
	
	private class OutingTableManager extends AbstractTableModel {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * The outings this TableModel is responsible for.
		 */
		private OutingInfo[] outings;
		
		private String columnNamePrepend = "<html><font size=+1>";
		private String columnNameAppend = "</font></html>";
		private String[] columnNames = {rb.getString("outing.boat"),
				rb.getString("outing.rowers"), rb.getString("outing.timeOut"),
				rb.getString("outing.timeIn"), rb.getString("outing.comment"),
				rb.getString("outing.destination"),rb.getString("outing.distance")};
		
		public OutingTableManager() {
			// TODO: set width
			updateOutings();
		}
		
	
		public int getColumnCount() {return 7;}	
		public int getRowCount() {return outings.length;}
		
		/**
		 * Get the name for a specific column.
		 * @param col The column.
		 * @return The name of the column.
		 */
	    public String getColumnName(int col) {
	    	return columnNamePrepend + columnNames[col].toString()
	    			+ columnNameAppend;
	    }

	    public Object getValueAt(int row, int col) {
	        if (col == 0) {
	        	//TODO: implement in DB.
	        	return outings[row].getBoat().getName();
	        	//if (row == 0) {return "Anna";} else {return "Bob";}
	        } else if (col == 1) {
	        	// Build the table showing rower names.
	        	StringBuffer buff = new StringBuffer();
	        	// TODO: check whether alignment is correct.
	        	buff.append("<html><table align=top>");
	        	MemberInfo[] rowers = outings[row].getRowers();
	        	// Go through four rows.
	        	for (short i = 0; i < 4; i++) {
	        		// Get rower 1 - 4 if existant.
	        		if (i <  rowers.length && rowers[i] != null) {
	        			buff.append("<tr><td>");
	        			buff.append(rowers[i].getName());
	        			buff.append("</td>");
	        			// Get rower 5-8 if existant. (1 and 5 in same row.)
	        			if (i <  rowers.length  + 4 && rowers[i+4] != null) {
	        				buff.append("<td>");
	        				buff.append(rowers[i+4].getName());
	        				buff.append("</td>");
	        			}
		        		buff.append("</tr>");
	        		}
	        	}
        		if (outings[row].getCox() != null) {
        			buff.append("<tr><td><i>" + outings[row].getCox().getName()
        					+"</i></td</tr");
        		}
	        	buff.append("</table></html>");
	        	System.out.println(buff.toString());
	        	return buff.toString();
	        } else if (col == 2) {
	        	// TODO: user format.
	        	SimpleDateFormat df = new SimpleDateFormat("HH:mm");
	        	return df.format(outings[row].getOut());
	        } else if (col == 3) {
	        	if (outings[row].getIn() != null) {
		        	SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		        	return df.format(outings[row].getIn());
	        	}
	        } else if (col == 4) {
	        	return outings[row].getDestination();
	        } else if (col == 5) {
	        	return outings[row].getComment();
	        } else if (col ==6) {
	        	// TODO: get Distance units.
	        	int d = outings[row].getDistance();
	        	if (d != 0) {
	        		return d;
	        	} else {
	        		return null;
	        	}
	        } else {
	        	System.out.println("col " + row + " --null");
	        }
	        return null;
	    }
		
		/**
		 * Updates the displayed outings using the database.
		 * 
		 */
		public void updateOutings() {
			try {
				outings = db.getOutings(current);
				fireTableDataChanged();
			} catch (Exception e) {
				// TODO: error dialog and cry!
			}
			// TODO: Complete
			//db.getOutings(current);
		}

	}
}