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

/*
 * This class is set up for use with Apache Derby. Someone adventurous could
 * maybe try some other db if they want.
 */
package org.ahunt.simpleRowLog.db.simpleDB;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.ResourceBundle;

import org.ahunt.simpleRowLog.common.BoatInfo;
import org.ahunt.simpleRowLog.common.BoatStatistic;
import org.ahunt.simpleRowLog.common.DatabaseError;
import org.ahunt.simpleRowLog.common.GroupInfo;
import org.ahunt.simpleRowLog.common.GroupStatistic;
import org.ahunt.simpleRowLog.common.MemberInfo;
import org.ahunt.simpleRowLog.common.MemberStatistic;
import org.ahunt.simpleRowLog.common.OutingInfo;
import org.apache.derby.security.DatabasePermission;
import org.grlea.log.SimpleLogger;

/**
 * @author Andrzej JR Hunt
 *
 */
public class Database implements org.ahunt.simpleRowLog.interfaces.Database {

	private static final SimpleLogger log = new SimpleLogger(Database.class);

	// The resource bundle for getting localised texts. (For error messages.)
	private ResourceBundle rb;
	// The driver to be used.
    private String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    // The database name
    private String dbName = "srl";
    // The resulting url to be used when connecting to the database.
    private String connectionURL = "jdbc:derby:" + dbName + ";create=true";
    
    // The connection that has been set up. Can then be used to gain statements
    // for any methods needing them.
    private Connection con = null;
    
    // various prepared statements for use.
    private PreparedStatement psAddGroup;
    private PreparedStatement psAddMember;
    private PreparedStatement psGetMember;
    private PreparedStatement psGetBoat;
    
    //TODO: add others.
    
    /** The opened instance. null if none. */
    private static Database db;
    
    /** Stores the outing tables for each year. */
    private OutingManager outingManager;
    
    //Temporary testing method
    // TODO: remove once finished class.
    public static void main(String[] args) {
    	try {
			Runtime.getRuntime().exec("rm -rf ./database/srl");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	getInstance();
    	System.exit(0);
    }
    
    
    //TODO: Implement a "locking" mechanism so that users can hold on to the db,
    // and once everyone has released it it automatically closes.
    /**
     * Get an instance of the database.
     * @return The database.
     */
    public static synchronized Database getInstance() {
    	if (db != null) {
    		return db;
    	} else {
    		return new Database();
    	}
    }
    
    /**
     * Start the database.
     */
	private Database() throws DatabaseError {
		log.entry("Database()");
		
		// Set up derby properties.
		System.setProperty("derby.system.home", new File(".").getAbsolutePath()
				+"/database");
		
		
		// Set up the resourceBundle for use
		rb = ResourceBundle.getBundle("db");
		log.info("Resource Bundle loaded.");
		
		// Load the driver.
        try {
            Class.forName(driver).newInstance();
            log.info("Loaded db driver.");
        } catch(Exception e) {
        	log.errorException(e);
            throw new DatabaseError(rb.getString("driverError"),e);
        }
        
        // Set up the connection to the Database
        try {
            con = DriverManager.getConnection(connectionURL);
            log.info("Connected to db.");
        }  catch (Throwable e)  {   
        	log.errorException(e);
            throw new DatabaseError(rb.getString("connectionError"),e);
        }
        
        // Check if the Database already existed, or if it needs set up.
        try {
			if (con.getWarnings() == null) {
				log.info("No database existed beforehand: setting up.");
				createDatabase();
			} else {
				log.info("Database previously existed and will be used.");
			}
		} catch (SQLException e) {
        	log.errorException(e);
			throw new DatabaseError(rb.getString("setupError"), e);
		} catch (IOException e) {
        	log.errorException(e);
			throw new DatabaseError(rb.getString("scriptError"), e);
		}
		
		// Set up the outing manager.
		try {
			log.info("Creating OutingManager.");
			outingManager = new OutingManager();
			log.info("Outing manager set up");
		} catch (SQLException e) {
			log.error("Problem creating outing Manager.");
			log.errorException(e);
			throw new DatabaseError(rb.getString("scriptError"), e);
		} catch (IOException e) {
        	log.errorException(e);
			throw new DatabaseError(rb.getString("scriptError"), e);
		}
		
		// TODO: set up prepared statements.
		try {
			log.info("Beginning preparation of other prepared statements.");
			createPreparedStatements();
			log.info("Prepared statements prepared successfully.");
		} catch (SQLException e) {
			log.error("Problem creating prepared statements..");
			log.errorException(e);
			throw new DatabaseError(rb.getString("dbError"),e);
		}
		
		try {
			log.info("Beginning creation of default data.");
			createDefaultData();
			log.info("Default data created successfully.");
		} catch (SQLException e) {
			log.error("Problem creating default data.");
			log.errorException(e);
			throw new DatabaseError(rb.getString("dbError"),e);
		}
		//TODO: complete

//Test code:
		outingManager.getOutings(new Date());
		
		// Store this database as the running db.
		db=this;
		log.exit("Database()");
	}
	
	/**
	 * Run the scripts setting up the database.
	 * @throws SQLException If there are problems running the scripts.
	 * @throws IOException If there are problems loading the scripts.
	 */
	private void createDatabase() throws SQLException, IOException {
		log.entry("createDatabase()");
		log.info("Running db setup scripts.");
		Statement s = con.createStatement();
		log.debugObject("con.getAutoCommit()", con.getAutoCommit());
		// Groups + triggers
		log.debug("setupGroups");
		s.execute(Util.loadScript("setupGroups"));
		log.debug("setupGroupTrigger1");
		s.execute(Util.loadScript("setupGroupTrigger1"));
		log.debug("setupGroupTrigger2");
		s.execute(Util.loadScript("setupGroupTrigger2"));
		// Members
		log.debug("setupMembers");
		s.execute(Util.loadScript("setupMembers"));
		log.debug("setupBoats");
		s.execute(Util.loadScript("setupBoats"));
		log.exit("createDatabase()");
	}
	
	/**
	 * Create all the prepared statements necessary for functionining.
	 * @throws SQLException If there are problems running the statements.
	 */
	private void createPreparedStatements() throws SQLException {
		log.entry("createPreparedStatements()");
		// Group adding prepared statement
		log.info("creating psAddGroup");
		psAddGroup = con.prepareStatement("INSERT INTO "
				+"groups(name, description, colour, isDefault, isPermanent)"
				+" VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		psAddMember = con.prepareStatement("INSERT INTO members (surname, "
				+ "forename, dob, usergroup) VALUES (?, ?, ?, ?)",
				Statement.RETURN_GENERATED_KEYS);
		psGetMember = con.prepareStatement("SELECT * FROM members WHERE id = ?");
		psGetBoat = con.prepareStatement("SELECT * FROM boats WHERE name = ?");
		log.exit("createPreparedStatements()");
	}
	
	/**
	 * Create the default data for the database.
	 * @throws SQLException If there are problems creating the data.
	 */
	private void createDefaultData() throws SQLException {
		//TODO: check colours here.
		log.entry("createDefaultData()");	
		// Add the default groups and members.
		log.info("Setting up default groups and users.");
		// Group: guest (Is default group as such)
		log.info("Guest group.");
		psAddGroup.setString(1, rb.getString("guestGroupName"));
		psAddGroup.setString(2, rb.getString("guestGroupDescription"));
		psAddGroup.setInt(3, -16776961);
		psAddGroup.setShort(4, (short) 1);
		psAddGroup.setShort(5, (short) 1);
		psAddGroup.execute();
		// Group: deleted
		log.info("Deleted group.");
		psAddGroup.setString(1, rb.getString("deletedGroupName"));
		psAddGroup.setString(2, rb.getString("deletedGroupDescription"));
		psAddGroup.setInt(4, 0);
		psAddGroup.execute();
		// Group: Standard members
		log.info("Standard group");
		psAddGroup.setString(1, rb.getString("defaultGroupName"));
		psAddGroup.setString(2, rb.getString("defaultGroupDescription"));
		psAddGroup.execute();

		// Member: Guest
	    psAddMember.setString(1,rb.getString("guestMemberName"));
	    psAddMember.setString(2,"");
		psAddMember.setDate(3, new java.sql.Date(0));
		psAddMember.setInt(4, 1);
		psAddMember.execute();
		
		// Member: deleted
	    psAddMember.setString(1,rb.getString("deletedMemberName"));
		psAddMember.setInt(4, 2);
		psAddMember.execute();
		
		log.exit("createDefaultData()");
	}
	
	/* (non-Javadoc)
	 * @see org.ahunt.simpleRowLog.interfaces.Database#getBoatStatistic(java.lang.String)
	 */
	
	public BoatStatistic getBoatStatistic(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.ahunt.simpleRowLog.interfaces.Database#getBoatStatistics()
	 */
	
	public BoatStatistic[] getBoatStatistics() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.ahunt.simpleRowLog.interfaces.Database#getBoatStatistics(int)
	 */
	
	public BoatStatistic[] getBoatStatistics(int sorting) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.ahunt.simpleRowLog.interfaces.Database#getBoats()
	 */
	
	public BoatInfo[] getBoats() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.ahunt.simpleRowLog.interfaces.Database#getBoats(int)
	 */
	
	public BoatInfo[] getBoats(int sorting) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.ahunt.simpleRowLog.interfaces.Database#getGroupStatistic(java.lang.String)
	 */
	
	public GroupStatistic getGroupStatistic(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.ahunt.simpleRowLog.interfaces.Database#getGroups()
	 */
	
	public GroupInfo[] getGroups() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.ahunt.simpleRowLog.interfaces.Database#getGroups(int)
	 */
	
	public GroupInfo[] getGroups(int sorting) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.ahunt.simpleRowLog.interfaces.Database#getMemberStatistics(short)
	 */
	public MemberStatistic getMemberStatistics(short key) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.ahunt.simpleRowLog.interfaces.Database#getMemberStatistics()
	 */
	public MemberStatistic[] getMemberStatistics() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.ahunt.simpleRowLog.interfaces.Database#getMemberStatistics(int)
	 */
	public MemberStatistic getMemberStatistics(int key) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.ahunt.simpleRowLog.interfaces.Database#getMembers()
	 */
	public MemberInfo[] getMembers() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.ahunt.simpleRowLog.interfaces.Database#getMembers(int)
	 */
	public MemberInfo[] getMembers(int sorting) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Get outings for the date specified.
	 * @see org.ahunt.simpleRowLog.interfaces.Database#getOutings(Date)
	 */
	public OutingInfo[] getOutings(Date date) throws SQLException {
		return outingManager.getOutings(date);
	}

	/**
	 * Add a new outing.
	 * @see org.ahunt.simpleRowLog.interfaces.Database#addOuting(Date, short[],
	 * short, Date, Date, String, String, String, int)
	 */
	public void addOuting(Date date, short[] rowers, short cox, Date timeOut,
			Date timeIn, String comment, String dest, String boat, int distance)
			throws DatabaseError {
		outingManager.addOuting(date, rowers, cox, timeOut, timeIn,
				comment, dest, boat, distance);
	}

	public MemberInfo getMember(short id) throws DatabaseError {
		//TODO: Correct group.
		log.entry("getMember(int)");
		log.debug("Getting member " + id);
		MemberInfo ret;
		try {
			psGetMember.setInt(1, id);
			ResultSet res = psGetMember.executeQuery();
			log.verbose("Got ResultSet for that date, now processing.");
			// Process the member. There should only be one row returned.
			res.next();
			ret =  new MemberInfo(id, res.getString("surname"),
					res.getString("forename"), res.getDate("dob"),
					null);
		} catch (SQLException e) {
			log.errorException(e);
			throw new DatabaseError(rb.getString("commandError"), e);
		}
		log.exit("OutingManager.getOutings()");
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see org.ahunt.simpleRowLog.interfaces.Database#modifyOuting(long, org.ahunt.simpleRowLog.common.MemberInfo, org.ahunt.simpleRowLog.common.MemberInfo[], java.util.Date, java.util.Date, java.lang.String, java.lang.String, org.ahunt.simpleRowLog.common.BoatInfo, short)
	 */
	
	public void modifyOuting(long created, MemberInfo cox, MemberInfo[] seat,
			Date out, Date in, String comment, String destination,
			BoatInfo boat, int distance) {
		// TODO Auto-generated method stub

	}


	
	public MemberStatistic[] getMembersStatistics() {
		// TODO Auto-generated method stub
		return null;
	}


	
	public MemberStatistic[] getMembersStatistics(int sorting) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private PreparedStatement getOutingPreparedPStatement(int year) {
		return null;
		// TODO: O:implement;
	}
	
	/**
	 * 
	 * Inner class responsible for outings.
	 * @author Andrzej JR Hunt
	 *
	 */
	private class OutingManager implements Runnable {
		//TODO: add a thread periodically checking last usage of a particlar
		// statement set.
		
		// Stores the various tables.
		private Hashtable<Integer, OutingStatementSet> statementCache =
				new Hashtable<Integer, OutingStatementSet>();
		
		public OutingManager() throws SQLException, IOException {
			log.entry("OutingManager.OutingManager()");
			try {
				log.info("Trying to create a new table for this year.");
				String year = new Integer(Calendar.getInstance().
						get(Calendar.YEAR)).toString();
				log.db(org.grlea.log.DebugLevel.L6_VERBOSE,
						MessageFormat.format(Util.loadScript("createOutings"),
						year));
				con.createStatement().execute(MessageFormat.format(
						Util.loadScript("createOutings"), year));
				log.info("New Outings table for year " + year + " created.");
			} catch (SQLException e) {
			// Just ignore. Not worrying. This means the table exists.
				log.info("Outings table for this year already exists.");
				log.dbe(org.grlea.log.DebugLevel.L6_VERBOSE, e);
			}
			// Start the cache checker.
			new Thread(this).start();
			log.exit("OutingManager.OutingManager()");
		}
		
		public OutingInfo[] getOutings(Date date) throws DatabaseError {
			log.entry("OutingManager.getOutings()");
			log.info("Getting outings for " + date.toString());
			Calendar cal = new GregorianCalendar();
			cal.setTime(date);
			ArrayList<OutingInfo> array = new ArrayList<OutingInfo>();
			try {
				PreparedStatement ps = getOutingStatementSet(
						cal.get(Calendar.YEAR)).getPreparedStatement(
						OutingStatementType.GET_OUTINGS);
				ps.setDate(1, new java.sql.Date(date.getTime()));
				ResultSet res = ps.executeQuery();
				log.info("Got ResultSet for that date, now processing.");
				// Process each Outing into an object.
				// Note that for fields which can be null, the data is checked.
				while (res.next()) {
					long out_id = res.getLong("id");
					log.verbose("Processing outing with id=" + out_id);
					MemberInfo[] seats = new MemberInfo[8];
					for (int i = 0; i< 8; i++) {
						short member_id = res.getShort("rower" + (i +1));
						if (member_id != 0) {
							seats[i] = getMember(member_id);
						}
					}
					Date day = res.getDate("day");
					MemberInfo cox = null;
					short coxID = res.getShort("cox");
					if (coxID != 0) {
						cox = getMember(coxID);
					}
					Date timeOut = new Date(res.getLong("time_out"));
					Date timeIn = null;
					long timeInL = res.getLong("time_in");
					if (timeInL != 0) {
						timeIn = new Date(timeInL);
					}
					String comment = res.getString("comment");
					String destination = res.getString("destination");
					BoatInfo boat = getBoat(res.getString("boat"));
					int distance = res.getInt("distance");
					array.add(new OutingInfo(out_id, day, seats, cox, timeOut,
							timeIn, comment, destination, boat, distance));
				}
			} catch (SQLException e) {
				log.errorException(e);
				throw new DatabaseError(rb.getString("commandError"), e);
			}
			log.exit("OutingManager.getOutings()");
			return array.toArray(new OutingInfo[0]);
		}
		
		
		public void addOuting(Date date, short[] rowers,
				short cox, Date timeOut, Date timeIn, String comment, String dest,
				String boat, int distance) throws DatabaseError {
			log.entry("OutingManager.getOutings()");
			log.info("Adding outing");
			Calendar cal = new GregorianCalendar();
			cal.setTime(date);
			try  {
				PreparedStatement ps = getOutingStatementSet(
						cal.get(GregorianCalendar.YEAR)).getPreparedStatement(
						OutingStatementType.ADD_OUTING);
				ps.setDate(1, new java.sql.Date(date.getTime()));
				ps.setShort(2, rowers[0]);
				// Go through all rowers and set to null if inexistant
				short i;
				for (i = 0; i < rowers.length - 1; i++) {
					if (rowers[i+1] != 0) {
						ps.setShort(i+3,rowers[i+1]);
					} else {
						ps.setNull(i+3, java.sql.Types.SMALLINT);
					}
				}
				while (i < 7) {
					ps.setNull(i+3, java.sql.Types.SMALLINT);
					i++;
				}
				if (cox != 0) {
					ps.setShort(10,cox);
				} else {
					ps.setNull(10, java.sql.Types.INTEGER);
				}
				// Time in/out
				ps.setLong(11, timeOut.getTime());
				if (timeIn != null) {
					ps.setLong(12, timeIn.getTime());
				} else {
					ps.setNull(12,java.sql.Types.BIGINT);
				}
				// Comment
				if (comment != null) {
					ps.setString(13, comment);
				} else {
					ps.setNull(13, java.sql.Types.VARCHAR);
				}
				// Destination
				if (dest != null) {
					ps.setString(14, dest);
				} else {
					ps.setNull(14, java.sql.Types.VARCHAR);
				}
				// Boat
				ps.setString(15, boat);
				// Distance
				if (distance != 0) {
					ps.setInt(16,distance);
				} else {
					ps.setNull(16, java.sql.Types.INTEGER);
				}
				ps.execute();
			} catch (SQLException e) {
				log.errorException(e);
				throw new DatabaseError(rb.getString("commandError"), e);
			}
			log.exit("OutingManager.getOutings()");
		}
		
		/**
		 * Get the OutingStatementSet for a particular year. Automatically
		 * loads the set if required.
		 * @param year The year for which you want the set.
		 * @return The OutingStatementSet.
		 */
		private OutingStatementSet getOutingStatementSet(int year) throws
				SQLException {
			int y = new Integer(year);
			if (statementCache.contains(y)) {
				return statementCache.get(y);
			} else {
				return new OutingStatementSet(y, statementCache);
			}
		}
		
		/**
		 * The thread method checking every so often through the cache to see
		 * when an object was last used.
		 */
		public void run() {
			// We want this to run indefinitely.
			while (true) {
				// Wait for half an hour.
				try {
					Thread.sleep(1800000l);
				} catch (Exception e) {
					// We don't care if the thread is interrupted.
				}
				// Data to check;
				Integer year = GregorianCalendar.getInstance().
						get(GregorianCalendar.YEAR);
				long now = new Date().getTime();
				// Now go through the cache:
				Enumeration<OutingStatementSet> e = statementCache.elements();
				while (e.hasMoreElements()) {
					OutingStatementSet os = e.nextElement();
					// If the set is older than 20 mins and not for the current
					// year then delete it.
					if ((os.getLastUsed() + 1200000 < now) &&
							!os.getYear().equals(year)) {
						statementCache.remove(os.getYear());
						os.close();
					}
				}
			}
		}
		
	}
	
	private enum OutingStatementType {GET_OUTINGS, ADD_OUTING, MODIFY_OUTING};
	
	
	
	private class OutingStatementSet {
		
		/** The last time that this set was used.*/
		private long lastUsed;
		
		/** The year for which this set is defined. */
		private Integer year;
		
		// The various prepared statements.
		private PreparedStatement psGetOutings;
		private PreparedStatement psAddOuting;
		
		/**
		 * Set up an outingstatement set for a given year.
		 * @param year The year for which the set is needed.
		 * @param statementCache The statement cache in use.
		 */
		public OutingStatementSet(Integer year, Hashtable<Integer,
				OutingStatementSet> statementCache) throws SQLException {
			this.year = year;
			log.entry("OutingStatementSet(Integer, HashTable");
			log.info("creating psGetOutings");
			psGetOutings = con.prepareStatement(MessageFormat
					.format("SELECT * FROM outings_{0} WHERE day = ? ORDER BY time_out",
					year.toString()));
			psAddOuting = con.prepareStatement(MessageFormat
					.format("INSERT INTO outings_{0} (day, rower1, rower2,"+
							" rower3, rower4, rower5, rower6, rower7, rower8, "+
							"cox, time_out, time_in, comment, destination," +
							"boat, distance) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?"
							+",?,?,?)",
					year.toString()));			
			// We want the last used time set.
			updateTime();
			// TODO: implement.
			log.exit("OutingStatementSet(Integer, HashTable");
		}
		
		
		public PreparedStatement getPreparedStatement(OutingStatementType typ) {
			// TODO: implement.
			updateTime();
			if (typ == OutingStatementType.GET_OUTINGS) {
				return psGetOutings;
			} else if (typ == OutingStatementType.ADD_OUTING) {
				return psAddOuting;
			}
			return null;
		}
		
		/**
		 * Get when this set was last accessed.
		 * @return The time the object was last used.
		 */
		public long getLastUsed() {return lastUsed;}
		
		/**
		 * Get the year for  which the set is defined.
		 * @return The year for which this set is defined.
		 */
		public Integer getYear() {return year;}
		
		/**
		 * Update the last used time. Should be called by any method of
		 * the class.
		 */
		private void updateTime() {
			lastUsed = new Date().getTime();
		}
		
		public void close() {
			log.info("Closing OutingStatementSet for year " + year.toString());
			try {
				//TODO : close all statements.
				psGetOutings.close();
				psAddOuting.close();
			} catch (SQLException e) {
				log.errorException(e);
			}
			year = null;
		}
	}



	
	public short addMember(String surname, String forename, Date dob, short group) throws DatabaseError {
		try {
			psAddMember.setString(1, surname);
			psAddMember.setString(2, forename);
			psAddMember.setDate(3, new java.sql.Date(dob.getTime()));
			psAddMember.setShort(4, group);
			psAddMember.execute();
			ResultSet rs = psAddMember.getGeneratedKeys();
			rs.next();
			return rs.getShort(1);			
		} catch (SQLException e) {
			log.errorException(e);
			throw new DatabaseError(rb.getString("commandError"), e);
		}
	}
	
	public void addBoat(String name, String type, boolean inHouse)
			throws DatabaseError {
		try {
			//TODO:change to ps.
			con.createStatement().execute("INSERT INTO boats (name, type, inHouse)" +
					"VALUES ('" + name + "','" + type + "',1)");
		} catch (SQLException e) {
			log.errorException(e);
			throw new DatabaseError(rb.getString("commandError"), e);
		}
	}


	public BoatInfo getBoat(String name) throws DatabaseError {
		try {
			psGetBoat.setString(1, name);
			psGetBoat.execute();
			ResultSet rs = psGetBoat.getResultSet();
			rs.next();
			// TODO: implement boolean inHouse.
			return new BoatInfo(rs.getString("name"),rs.getString("type"));
		} catch (SQLException e) {
			log.error("Failed to get boat: " + name);
			log.errorException(e);
			throw new DatabaseError(rb.getString("commandError"),e);
		}
	}
}
