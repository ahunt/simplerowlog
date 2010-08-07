/*
 *    This file is part of simple rowLog: the open rowing logbook.
 *    Copyright (C) 2009, 2010  Andrzej JR Hunt
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
 *  27/04/2010: Implemented getMemberStatistics.
 *	23/08/2009:	Changelog added.
 */

/*
 * This class is set up for use with Apache Derby. Someone adventurous could
 * maybe try some other db if they want.
 */
package org.ahunt.simpleRowLog.db.simpleDB;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Random;
import java.util.ResourceBundle;

import org.ahunt.simpleRowLog.common.AdminInfo;
import org.ahunt.simpleRowLog.common.AdminPermissionList;
import org.ahunt.simpleRowLog.common.BoatInfo;
import org.ahunt.simpleRowLog.common.BoatStatistic;
import org.ahunt.simpleRowLog.common.DatabaseError;
import org.ahunt.simpleRowLog.common.EntryAlreadyExistsException;
import org.ahunt.simpleRowLog.common.GroupInfo;
import org.ahunt.simpleRowLog.common.GroupStatistic;
import org.ahunt.simpleRowLog.common.MemberInfo;
import org.ahunt.simpleRowLog.common.MemberStatistic;
import org.ahunt.simpleRowLog.common.OutingInfo;

import org.grlea.log.SimpleLogger;

/**
 * An implementation of the Database interface, using Apache Derby as the
 * underlying database.
 * 
 * @author Andrzej JR Hunt
 * 
 */
public class Database implements org.ahunt.simpleRowLog.interfaces.Database {

	/** The opened instance. null if none. */
	private static Database db;

	/** Logging mechanism. */
	private static final SimpleLogger log = new SimpleLogger(Database.class);

	/** Resource bundle for databases texts. */
	private ResourceBundle rb;

	/** Stores the outing tables for each year and deals with them. */
	private OutingManager outingManager;

	/*-------------------- Connection Settings ------------->
	/** Driver to use. */
	private String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	/** Database name. */
	private String dbName = "srl";
	/** Connection url in use. */
	private String connectionURL = "jdbc:derby:" + dbName + ";create=true";

	/** The connection the db is using. null if none. */
	private Connection con;

	/* -------------- Prepared Statements for use ----------------- */
	/** To add a boat. */
	private PreparedStatement psAddBoat;
	/** To get a boat. */
	private PreparedStatement psGetBoat;
	/** To modify a boat. */
	private PreparedStatement psModifyBoat;

	/** To get all the boats. */
	private PreparedStatement psGetBoats;
	/** To get the boats that are either available, or unavailable. */
	private PreparedStatement psGetBoatsSelection;

	/** To get statistics for one boat. */
	private PreparedStatement psGetBoatStat;
	/** To get statistics for all boats. */
	private PreparedStatement psGetBoatsStats;

	/** To add a member. */
	private PreparedStatement psAddMember;
	/** To get a member. */
	private PreparedStatement psGetMember;
	/** To modify a member. */
	private PreparedStatement psModifyMember;

	/** To get all members. */
	private PreparedStatement psGetMembers;
	/** TO get some members (e.g. group) */
	private PreparedStatement psGetMembersSelection;

	private PreparedStatement psAddGroup;
	private PreparedStatement psGetGroup;
	private PreparedStatement psModifyGroup;
	private PreparedStatement psGetDefaultGroup;

	private PreparedStatement psGetGroups;

	private PreparedStatement psAddAdmin;

	// TODO: Implement a "locking" mechanism so that users can hold on to the
	// db,
	// and once everyone has released it it automatically closes.
	/**
	 * Get an instance of the database.
	 * 
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
				+ "/database");

		// Set up the resourceBundle for use
		rb = ResourceBundle.getBundle("db");
		log.info("Resource Bundle loaded.");

		// Load the driver.
		try {
			Class.forName(driver).newInstance();
			log.info("Loaded db driver.");
		} catch (Exception e) {
			log.errorException(e);
			throw new DatabaseError(rb.getString("driverError"), e);
		}

		// Set up the connection to the Database
		try {
			con = DriverManager.getConnection(connectionURL);
			log.info("Connected to db.");
		} catch (Throwable e) {
			log.errorException(e);
			throw new DatabaseError(rb.getString("connectionError"), e);
		}

		// Check if the Database already existed, or if it needs set up.
		try {
			if (con.getWarnings() == null) {
				log.info("No database existed beforehand: setting up.");
				createDatabase(); // Run the setup scripts
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

		// Store this database as the running db.
		db = this;
		log.exit("Database()");
	}

	/**
	 * Run the scripts setting up the database.
	 * 
	 * @throws SQLException
	 *             If there are problems running the scripts.
	 * @throws IOException
	 *             If there are problems loading the scripts.
	 */
	private void createDatabase() throws SQLException, IOException {
		log.entry("createDatabase()");
		log.info("Running db setup scripts.");
		Statement s = con.createStatement();
		log.debugObject("con.getAutoCommit()", con.getAutoCommit());

		// Database setup scripts (Except for Outings table -- done separately)
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
		log.debug("setupAdmins");
		s.execute(Util.loadScript("setupAdmins"));
		log.debug("setupAdminsTrigger1");
		s.execute(Util.loadScript("setupAdminsTrigger1"));
		log.debug("setupAdminsTrigger2");
		s.execute(Util.loadScript("setupAdminsTrigger2"));
		log.debug("setupAdminsTrigger1");
		s.execute(Util.loadScript("setupAdminsPermissions"));

		// The default data.
		log.info("Beginning creation of default data.");
		createDefaultData();
		log.info("Default data created successfully.");

		log.exit("createDatabase()");
	}

	/**
	 * Create the default data for the database.
	 * 
	 * @throws SQLException
	 *             If there are problems creating the data.
	 */
	private void createDefaultData() throws SQLException {
		// TODO: check colours here.
		log.entry("createDefaultData()");
		log.info("Setting up default groups and users.");
		// Groups: guest (default), deleted, and standard member
		int guestGroup = addGroup(rb.getString("guestGroupName"), rb
				.getString("guestGroupDescription"), new Color(-16776961), true);
		int deletedGroup = addGroup(rb.getString("deletedGroupName"), rb
				.getString("deletedGroupDescription"), new Color(-16776961),
				false);
		addGroup(rb.getString("memberGroupName"), rb
				.getString("memberGroupDescription"), new Color(-16776961),
				false);
		// Members: guest and deleted.
		try {
			addMember(rb.getString("guestMemberName"), "",
					new java.sql.Date(0), guestGroup);
			addMember(rb.getString("deletedMemberName"), "", new java.sql.Date(
					0), deletedGroup);
		} catch (EntryAlreadyExistsException e) {
			// Do nothing, it won't happen, or we don't care.
			// TODO: mass alarm HERE.
		}
		addBoat(rb.getString("otherBoat"), "", true);
		// TODO: setup dialog for admin, asking data.
		addAdmin("root", "root", "Rootable", true, "Default admin");

		log.exit("createDefaultData()");
	}

	/*
	 * Note: In the "categories below: The square bracketed code is to tell you
	 * what type of methods are included: A is add, G is get, M is modify.
	 * Additionally, a + sign indicated that the operation is for multiple
	 * objects. (Just a help when designing the interface. Can be pretty much
	 * ignored.)
	 */
	/* -------------------- BOATS (INDIVIDUAL) [AGM] ------------------- */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addBoat(String name, String type, boolean inHouse)
			throws DatabaseError {
		log.verbose("addBoat(...)");
		// Do data checking.
		if (name == null | name.length() == 0) {
			throw new IllegalArgumentException("Boat name cannot be null or "
					+ "empty");
		}
		try {
			if (psAddBoat == null) { // Check if ps exists, create if necessary.
				psAddBoat = con
						.prepareStatement("INSERT INTO boats (name, type,"
								+ "inHouse) VALUES (?,?,?)");
			}
			// Set the data
			psAddBoat.setString(1, name);
			psAddBoat.setString(2, type);
			psAddBoat.setBoolean(3, inHouse);
			psAddBoat.execute();
		} catch (SQLException e) {
			log.errorException(e);
			throw new DatabaseError(rb.getString("commandError"), e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoatInfo getBoat(String name) throws DatabaseError {
		log.verbose("getBoat(String)");
		try {
			if (psGetBoat == null) {
				psGetBoat = con.prepareStatement("SELECT * FROM boats WHERE"
						+ " name = ?");
			}
			psGetBoat.setString(1, name);
			psGetBoat.execute();
			ResultSet rs = psGetBoat.getResultSet();
			if (rs.next()) {
				return new BoatInfo(rs.getString("name"), rs.getString("type"),
						rs.getBoolean("inHouse"));
			} else { // No such boat.
				return null;
			}
		} catch (SQLException e) {
			log.error("Failed to get boat: " + name);
			log.errorException(e);
			throw new DatabaseError(rb.getString("commandError"), e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void modifyBoat(BoatInfo old, String name, String type,
			boolean inHouse) throws DatabaseError {
		log.verbose("modifyBoat(...)");
		// Check the data
		if (old == null) {
			throw new IllegalArgumentException("old cannot be null");
		}
		if (name == null | name.length() == 0) {
			throw new IllegalArgumentException("Boat name cannot be null or "
					+ "empty");
		}
		// Do the actual work
		try {
			if (psModifyBoat == null) {
				psModifyBoat = con.prepareStatement("UPDATE boats SET name=?,"
						+ " type=?, inHouse=? WHERE name = ?");
			}
			psModifyBoat.setString(4, old.getName()); // Old name
			psModifyBoat.setString(1, name);
			psModifyBoat.setString(2, type);
			psModifyBoat.setBoolean(3, inHouse);
			psModifyBoat.execute();
			// TODO: complete
		} catch (SQLException e) {
			log.error("Failed to modify boat: " + name);
			log.errorException(e);
			throw new DatabaseError(rb.getString("commandError"), e);
		}

	}

	/* -------------------- BOATS (GROUP) [G+] ------------------- */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoatInfo[] getBoats() throws DatabaseError {
		log.verbose("getBoats()");
		try {
			// Check whether prepared statement exists. Create if necessary.
			if (psGetBoats == null) {
				psGetBoats = con
						.prepareStatement("SELECT name FROM boats ORDER"
								+ " BY name");
			}
			// Get the data.
			psGetBoats.execute();
			// Get results.
			ResultSet rs = psGetBoats.getResultSet();
			ArrayList<BoatInfo> a = new ArrayList<BoatInfo>();
			// Go through the groups.
			while (rs.next()) {
				a.add(getBoat(rs.getString("name")));
			}
			log.verbose("Data gotten, returning boats");
			// Return a GroupInfo.
			if (a.size() == 0) {
				return null;
			}
			return a.toArray(new BoatInfo[0]);
		} catch (SQLException e) {
			log.error("Error getting boats");
			log.errorException(e);
			throw new DatabaseError(rb.getString("commandError"), e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoatInfo[] getBoats(boolean inHouse) throws DatabaseError {
		// TODO: change all these to entry. (Direct getting of data.)
		log.verbose("getBoats(boolean)");
		try {
			// Check whether prepared statement exists. Create if necessary.
			if (psGetBoatsSelection == null) {
				psGetBoatsSelection = con.prepareStatement("SELECT name FROM"
						+ " boats WHERE inHouse = ? ORDER BY name");
			}
			psGetBoatsSelection.setBoolean(1, inHouse);
			// Get the data.
			psGetBoatsSelection.execute();
			// Get results.
			ResultSet rs = psGetBoatsSelection.getResultSet();
			ArrayList<BoatInfo> a = new ArrayList<BoatInfo>();
			// Go through the groups.
			while (rs.next()) {
				a.add(getBoat(rs.getString("name")));
			}
			log.verbose("Data gotten, returning groups");
			// Return a GroupInfo.
			if (a.size() == 0) {
				return null;
			}
			return a.toArray(new BoatInfo[0]);
		} catch (SQLException e) {
			log.error("Error getting groups");
			log.errorException(e);
			throw new DatabaseError(rb.getString("commandError"), e);
		}
	}

	/* -------------------- BOATS - STATISTICS [G,G+] ------------------- */

	/**
	 * Not yet implemented.
	 */
	@Override
	public BoatStatistic getBoatStatistic(String name) {
		// TODO: implement (low priority)
		return null;
	}

	/**
	 * Not yet implemented.
	 */

	@Override
	public BoatStatistic[] getBoatsStatistics() {
		// TODO: implement (low priority)
		return null;
	}

	/* -------------------- MEMBERS (INDIVIDUAL) [AGM] ----------------- */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int addMember(String surname, String forename, Date dob, int group)
			throws DatabaseError, EntryAlreadyExistsException {
		log.verbose("addMember(... " + group + ")");
		if (surname == null | surname.length() == 0) {
			throw new IllegalArgumentException("Surname cannot be null or"
					+ " zero length");
		}
		if (dob == null) {
			throw new IllegalArgumentException("dob cannot be null");
		}
		if (getGroup(group) == null) { // Check for valid group
			throw new IllegalArgumentException(
					"group must correspond to an existing group.");
		}
		try {
			if (psAddMember == null) { // Check whether already made.
				psAddMember = con
						.prepareStatement(
								"INSERT INTO members (surname, "
										+ "forename, dob, usergroup) VALUES (?, ?, ?, ?)",
								Statement.RETURN_GENERATED_KEYS);
			}
			psAddMember.setString(1, surname);
			psAddMember.setString(2, forename);
			psAddMember.setDate(3, new java.sql.Date(dob.getTime()));
			psAddMember.setInt(4, group);
			psAddMember.execute();
			ResultSet rs = psAddMember.getGeneratedKeys();
			if (rs.next()) {
				return rs.getInt(1);
			} else {
				throw new DatabaseError(rb.getString("commandError"), null);
			}
		} catch (SQLIntegrityConstraintViolationException e) {
			throw new EntryAlreadyExistsException("A member named " + surname
					+ ":" + forename + " with dob " + dob
					+ " already is in the database.", e);
		} catch (SQLException e) {
			log.errorException(e);
			throw new DatabaseError(rb.getString("commandError"), e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MemberInfo getMember(int id) throws DatabaseError {
		// TODO: Correct group.
		log.verbose("getMember(int)");
		try {
			if (psGetMember == null) { // Ensure the ps is available
				psGetMember = con
						.prepareStatement("SELECT * FROM members WHERE id = ?");
			}
			psGetMember.setInt(1, id);
			ResultSet res = psGetMember.executeQuery();
			if (res.next()) { // Check whether there are results
				return new MemberInfo(id, res.getString("surname"), res
						.getString("forename"), res.getDate("dob"),
						getGroup(res.getInt("usergroup")));
			} else { // No such member
				return null;
			}
		} catch (SQLException e) {
			log.errorException(e);
			throw new DatabaseError(rb.getString("commandError"), e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void modifyMember(int id, String surname, String forename, Date dob,
			int group) throws DatabaseError {
		log.verbose("modifyMember(...)");
		// Check the data
		if (getMember(id) == null) {
			throw new IllegalArgumentException(
					"member must already exist in order to modify");
		}
		if (surname == null | surname.length() == 0) {
			throw new IllegalArgumentException("surname cannot be null or"
					+ " zero length");
		}
		if (dob == null) {
			throw new IllegalArgumentException("dob cannot be null");
		}
		if (getGroup(group) == null) { // Check for valid group
			throw new IllegalArgumentException(
					"group must correspond to an existing group.");
		}
		// Do the actual work
		try {
			if (psModifyMember == null) {
				psModifyMember = con.prepareStatement("UPDATE members SET"
						+ " surname=?, forename=?, dob=?, usergroup=? WHERE"
						+ " id = ?");
			}
			psModifyMember.setString(1, surname); // Old name
			psModifyMember.setString(2, forename);
			psModifyMember.setDate(3, new java.sql.Date(dob.getTime()));
			psModifyMember.setInt(4, group);
			psModifyMember.setInt(5, id);
			psModifyMember.execute();
		} catch (SQLException e) {
			log.error("Failed to modify member.");
			log.errorException(e);
			throw new DatabaseError(rb.getString("commandError"), e);
		}

	}

	/* -------------------- MEMBERS (GROUP) [G+] ----------------- */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MemberInfo[] getMembers() throws DatabaseError {
		log.verbose("getMembers()");
		try {
			if (psGetMembers == null) {
				psGetMembers = con.prepareStatement("SELECT * from members");
			}
			psGetMembers.execute();
			ResultSet rs = psGetMembers.getResultSet();
			ArrayList<MemberInfo> a = new ArrayList<MemberInfo>();
			// TODO: caching of groups to save call of getGroup for each group.
			while (rs.next()) {
				a.add(new MemberInfo(rs.getInt("id"), rs.getString("surname"),
						rs.getString("forename"), new Date(0), getGroup(rs
								.getInt("usergroup"))));
			}
			if (a.size() == 0) {
				return null;
			}
			return a.toArray(new MemberInfo[0]);
		} catch (SQLException e) {
			log.errorException(e);
			throw new DatabaseError(rb.getString("commandError"), e);
		}
	}

	/**
	 * Not yet implemented
	 */
	@Override
	public MemberInfo[] getMembers(int sorting) throws DatabaseError {
		// TODO: implement (medium).
		return null;
	}

	/* -------------------- MEMBERS - STATISTICS [G,G+] ----------------- */

	/**
	 * Not yet implemented.
	 */
	@Override
	public MemberStatistic getMemberStatistics(int id) throws DatabaseError {
		return outingManager.getMemberStatistics(id);
	}

	/**
	 * Not yet implemented.
	 */
	@Override
	public MemberStatistic[] getMembersStatistics() {
		// TODO: implement (low priority)
		return null;
	}

	/**
	 * Not yet implemented.
	 */
	@Override
	public MemberStatistic[] getMembersStatistics(int sorting) {
		// TODO: implement (low priority)
		return null;
	}

	/* -------------------- GROUPS [AGM,G+] ----------------- */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int addGroup(String name, String description, Color colour,
			boolean isDefault) throws DatabaseError {
		log.verbose("addGroup(...)");
		if (name == null || name.length() == 0) {
			throw new IllegalArgumentException("name cannot be null when "
					+ "adding a group");
		}
		if (colour == null) {
			throw new IllegalArgumentException("colour cannot be null when "
					+ "adding a group");
		}
		try {
			// Check whether prepared statement exists. Create if necessary.
			if (psAddGroup == null) {
				psAddGroup = con.prepareStatement("INSERT INTO "
						+ "groups(name, description, colour, isDefault)"
						+ " VALUES (?, ?, ?, ?)",
						Statement.RETURN_GENERATED_KEYS);
			}
			// Set up data.
			psAddGroup.setString(1, name);
			psAddGroup.setString(2, description);
			psAddGroup.setInt(3, colour.getRGB());
			psAddGroup.setBoolean(4, isDefault);
			psAddGroup.execute();
			// Get the generated id.
			ResultSet rs = psAddGroup.getGeneratedKeys();
			if (rs.next()) {
				return rs.getInt(1);
			} else {
				throw new DatabaseError(rb.getString("commandError"), null);
			}
		} catch (SQLException e) {
			log.error("Error setting up group.");
			log.errorException(e);
			throw new DatabaseError(rb.getString("commandError"), e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GroupInfo getGroup(int id) throws DatabaseError {
		log.verbose("getGroup(" + id + ")");
		try {
			// Check whether prepared statement exists. Create if necessary.
			if (psGetGroup == null) {
				psGetGroup = con.prepareStatement("SELECT * FROM groups "
						+ "WHERE id = ?");
			}
			// Set the data
			psGetGroup.setInt(1, id);
			psGetGroup.execute();
			// Get results.
			ResultSet rs = psGetGroup.getResultSet();
			if (!rs.next()) {
				// throw new IllegalArgumentException("No such group " + id);
				return null;
			}
			// Extract data.
			String name = rs.getString("name");
			String description = rs.getString("description");
			Color c = new Color(rs.getInt("colour"));
			boolean isDefault;
			isDefault = rs.getBoolean("isDefault");
			log.verbose("Data gotten, returning group");
			// Return a GroupInfo.
			return new GroupInfo(id, name, description, c, isDefault);
		} catch (SQLException e) {
			log.error("Error getting group " + id);
			log.errorException(e);
			throw new DatabaseError(rb.getString("commandError"), e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void modifyGroup(int id, String name, String description,
			Color colour, boolean isDefault) throws DatabaseError {
		log.verbose("Modifying group " + id);
		if (getGroup(id) == null) { // if no such group
			throw new IllegalArgumentException("id must be a valid group");
		}
		if (name == null | name.length() == 0) {
			throw new IllegalArgumentException("name cannot be null or zero"
					+ " length");
		}
		if (colour == null) {
			throw new IllegalArgumentException("colour cannot be null");
		}
		try {
			if (psModifyGroup == null) {
				psModifyGroup = con.prepareStatement("UPDATE groups name = ?,"
						+ "description = ?, colour = ?, isDefault = ?"
						+ " WHERE id = ?");
			}
			// Setup data.
			psModifyGroup.setString(1, name);
			psModifyGroup.setString(2, description);
			psModifyGroup.setInt(3, colour.getRGB());
			psModifyGroup.setBoolean(4, isDefault);
			psModifyGroup.setInt(5, id);
			// Process
			psModifyGroup.execute();
		} catch (SQLException e) {
			log.error("Error modifying group " + id);
			log.errorException(e);
			throw new DatabaseError(rb.getString("commandError"), e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GroupInfo[] getGroups() throws DatabaseError {
		log.verbose("getGroups()");
		try {
			// Check whether prepared statement exists. Create if necessary.
			if (psGetGroups == null) {
				psGetGroups = con.prepareStatement("SELECT id FROM groups");
			}
			// Get the data.
			psGetGroups.execute();
			// Get results.
			ResultSet rs = psGetGroups.getResultSet();
			// Check that there are any groups
			ArrayList<GroupInfo> a = new ArrayList<GroupInfo>();
			// Go through the groups.
			while (rs.next()) {
				a.add(getGroup(rs.getInt("id")));
			}
			log.verbose("Data gotten, returning groups");
			// Return a GroupInfo.
			if (a.size() == 0) {
				return null; // If there are no groups.
			}
			return a.toArray(new GroupInfo[0]);
		} catch (SQLException e) {
			log.error("Error getting groups");
			log.errorException(e);
			throw new DatabaseError(rb.getString("commandError"), e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GroupInfo getDefaultGroup() throws DatabaseError {
		log.verbose("getDefaultGroup()");
		try {
			// Check whether prepared statement exists. Create if necessary.
			if (psGetDefaultGroup == null) {
				psGetDefaultGroup = con
						.prepareStatement("SELECT * FROM groups "
								+ "WHERE isDefault = 1");
				psGetDefaultGroup.setMaxRows(1);
			}
			psGetDefaultGroup.execute();
			// Get results.
			ResultSet rs = psGetDefaultGroup.getResultSet();
			// Extract data.
			if (!rs.next()) {
				throw new DatabaseError("No default group. Major error.", null);
			}
			int id = rs.getInt("id");
			String name = rs.getString("name");
			String description = rs.getString("description");
			Color c = new Color(rs.getInt("colour"));
			log.verbose("Data gotten, returning group");
			// Return a GroupInfo.
			return new GroupInfo(id, name, description, c, true);
		} catch (SQLException e) {
			log.error("Error getting default group.");
			log.errorException(e);
			throw new DatabaseError(rb.getString("commandError"), e);
		}
	}

	/* -------------------- GROUPS - STATISTICS [G,G+] ----------------- */

	/**
	 * Not yet implemented.
	 */
	@Override
	public GroupStatistic getGroupStatistic(int id) throws DatabaseError {
		// TODO: implement (low priority)
		return null;
	}

	/**
	 * Not yet implemented.
	 */
	@Override
	public GroupStatistic[] getGroupsStatistics() throws DatabaseError {
		// TODO: implement (low priority)
		return null;
	}

	/* -------------------- Outings [AG+M] ----------------- */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long addOuting(Date date, int[] rowers, int cox, Date timeOut,
			Date timeIn, String comment, String dest, String boat, int distance)
			throws DatabaseError {
		return outingManager.addOuting(date, rowers, cox, timeOut, timeIn,
				comment, dest, boat, distance);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OutingInfo[] getOutings(Date date) throws DatabaseError {
		return outingManager.getOutings(date);
	}

	/**
	 * Not yet implemented.
	 */
	@Override
	public void modifyOuting(long id, long created, int[] rowers, int cox,
			Date out, Date in, String comment, String destination, String boat,
			int distance) {
		outingManager.modifyOuting(id, created, rowers, cox, out, in, comment,
				destination, boat, distance);

	}

	/* -------------------- Admins [AG,G+,M,R,AUTH] ----------------- */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addAdmin(String username, String password, String name,
			boolean isRoot, String comment) {
		log.entry("addAdmin(...)");
		// Generate a salt.
		Random r = new Random();
		byte[] salt = new byte[64];
		r.nextBytes(salt);
		// Generate a hash.

		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			digest.update(salt);
			byte[] hash = digest.digest(password.getBytes("UTF-16"));
			if (psAddAdmin == null) {
				psAddAdmin = con.prepareStatement("INSERT INTO admins "
						+ "(username, password, salt, name, "
						+ "isRoot, comment) VALUES (?,?,?,?,?,?)");
			}
			psAddAdmin.setString(1, username);
			psAddAdmin
					.setString(2, new String(hash, Charset.forName("UTF-16")));
			psAddAdmin
					.setString(3, new String(salt, Charset.forName("UTF-16")));
			psAddAdmin.setString(4, name);
			psAddAdmin.setBoolean(5, isRoot);
			psAddAdmin.setString(6, comment);

			psAddAdmin.execute();
			psAddAdmin.clearParameters(); // So that data isn't kept in mem.
		} catch (SQLException e) {
			log.error("Error adding new admin.");
			log.errorException(e);
			throw new DatabaseError(rb.getString("commandError"), e);
		} catch (Exception e) {
			// TODO: deal with the no encoding / algorithm exceptions.
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AdminInfo getAdmin(String username) {
		return null;
	}

	public AdminPermissionList getAdminPermissions(String username) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AdminInfo[] getAdmins() {
		return null;
	}

	public void modifyAdmin(String formerUsername) {

	}

	public void removeAdmin(String username) {

	}

	public AdminInfo authenticateAdmin(String username, String password) {
		return null;
	}

	/* -------------------- OutingManager (INTERNAL) ----------------- */

	/**
	 * 
	 * Inner class responsible for outings.
	 * 
	 * @author Andrzej JR Hunt
	 * 
	 */
	private class OutingManager implements Runnable {

		// Stores the various tables.
		private Hashtable<Integer, OutingStatementSet> statementCache = new Hashtable<Integer, OutingStatementSet>();

		public OutingManager() throws SQLException, IOException {
			log.entry("OutingManager.OutingManager()");
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
					for (int i = 0; i < 8; i++) {
						int member_id = res.getInt("rower" + (i + 1));
						if (member_id != 0) {
							seats[i] = getMember(member_id);
						}
					}
					Date day = res.getDate("day");
					MemberInfo cox = null;
					int coxID = res.getInt("cox");
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

		public long addOuting(Date date, int[] rowers, int cox, Date timeOut,
				Date timeIn, String comment, String dest, String boat,
				int distance) throws DatabaseError {
			log.entry("OutingManager.addOuting(...)");
			log.info("Adding outing");
			if (date == null) {
				throw new IllegalArgumentException("Date cannot be null");
			}
			if (getMember(rowers[0]) == null) {
				throw new IllegalArgumentException("rowers[0] must be a valid"
						+ " member");
			}
			if (timeOut == null) {
				throw new IllegalArgumentException("timeOut cannot be null");
			}
			if (boat == null || boat.length() == 0 || getBoat(boat) == null) {
				throw new IllegalArgumentException("boat must be a valid "
						+ "boat, cannot be null");
			}
			Calendar cal = new GregorianCalendar();
			cal.setTime(date);
			try {
				PreparedStatement ps = getOutingStatementSet(
						cal.get(GregorianCalendar.YEAR)).getPreparedStatement(
						OutingStatementType.ADD_OUTING);
				ps.setDate(1, new java.sql.Date(date.getTime()));
				ps.setInt(2, rowers[0]);
				// Go through all rowers and set to null if inexistant
				short i;
				for (i = 0; i < rowers.length - 1; i++) {
					if (rowers[i + 1] != 0) {
						ps.setInt(i + 3, rowers[i + 1]);
					} else {
						ps.setNull(i + 3, java.sql.Types.SMALLINT);
					}
				}
				while (i < 7) {
					ps.setNull(i + 3, java.sql.Types.SMALLINT);
					i++;
				}
				if (cox != 0) {
					ps.setInt(10, cox);
				} else {
					ps.setNull(10, java.sql.Types.INTEGER);
				}
				// Time in/out
				ps.setLong(11, timeOut.getTime());
				if (timeIn != null) {
					ps.setLong(12, timeIn.getTime());
				} else {
					ps.setNull(12, java.sql.Types.BIGINT);
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
					ps.setInt(16, distance);
				} else {
					ps.setNull(16, java.sql.Types.INTEGER);
				}
				ps.execute();
			} catch (SQLException e) {
				log.errorException(e);
				throw new DatabaseError(rb.getString("commandError"), e);
			}
			log.exit("OutingManager.addOuting(...)");
			return 0;
			// TODO: get the id returned.
		}

		public void modifyOuting(long id, long day, int[] rowers, int cox,
				Date timeOut, Date timeIn, String comment, String destination,
				String boat, int distance) {
			log.entry("OutingManager.modifyOuting(...)");
			log.info("Adding outing");
			// TODO : check whether the id is valid.
			if (getMember(rowers[0]) == null) {
				throw new IllegalArgumentException("rowers[0] must be a valid"
						+ " member");
			}
			if (timeOut == null) {
				throw new IllegalArgumentException("timeOut cannot be null");
			}
			if (boat == null || boat.length() == 0 || getBoat(boat) == null) {
				throw new IllegalArgumentException("boat must be a valid "
						+ "boat, cannot be null");
			}
			Calendar cal = new GregorianCalendar();
			cal.setTime(new Date(day));
			try {
				PreparedStatement ps = getOutingStatementSet(
						cal.get(GregorianCalendar.YEAR)).getPreparedStatement(
						OutingStatementType.MODIFY_OUTING);
				ps.setLong(16, id);
				ps.setInt(1, rowers[0]);
				// Go through all rowers and set to null if inexistant
				short i;
				for (i = 0; i < rowers.length - 1; i++) {
					if (rowers[i + 1] != 0) {
						ps.setInt(i + 2, rowers[i + 1]);
					} else {
						ps.setNull(i + 2, java.sql.Types.SMALLINT);
					}
				}
				while (i < 7) {
					ps.setNull(i + 2, java.sql.Types.SMALLINT);
					i++;
				}
				if (cox != 0) {
					ps.setInt(9, cox);
				} else {
					ps.setNull(9, java.sql.Types.INTEGER);
				}
				// Time in/out
				ps.setLong(10, timeOut.getTime());
				if (timeIn != null) {
					ps.setLong(11, timeIn.getTime());
				} else {
					ps.setNull(11, java.sql.Types.BIGINT);
				}
				// Comment
				if (comment != null) {
					ps.setString(12, comment);
				} else {
					ps.setNull(12, java.sql.Types.VARCHAR);
				}
				// Destination
				if (destination != null) {
					ps.setString(13, destination);
				} else {
					ps.setNull(13, java.sql.Types.VARCHAR);
				}
				// Boat
				ps.setString(14, boat);
				// Distance
				if (distance != 0) {
					ps.setInt(15, distance);
				} else {
					ps.setNull(15, java.sql.Types.INTEGER);
				}
				ps.execute();
			} catch (SQLException e) {
				log.errorException(e);
				throw new DatabaseError(rb.getString("commandError"), e);
			}
			log.exit("OutingManager.addOuting(...)");
		}

		public MemberStatistic getMemberStatistics(int id) throws DatabaseError {
			log.entry("OutingManager.getMemberStatistics()");
			log.info("Getting statistics for " + id);
			MemberInfo member = getMember(id);
			Calendar cal = new GregorianCalendar();
			cal.setTime(new Date());
			int totalDistanceThisYear = 0, totalDistanceLastYear = 0; 
			int totalOutingsThisYear = 0, totalOutingsLastYear = 0;
			try {
				PreparedStatement ps = getOutingStatementSet(
						cal.get(Calendar.YEAR)).getPreparedStatement(
						OutingStatementType.GET_OUTINGS);
				for (int i = 1; i < 10; i++) {
					ps.setInt(i, id);
				}
				ResultSet res = ps.executeQuery();
				log.info("Got ResultSet for that member, now processing.");

				while (res.next()) {
					totalDistanceThisYear += res.getInt("distance");
					totalOutingsThisYear++;
				}

				// For the previous year.
				ps = getOutingStatementSet(cal.get(Calendar.YEAR) - 1)
						.getPreparedStatement(OutingStatementType.GET_OUTINGS);
				for (int i = 1; i < 10; i++) {
					ps.setInt(i, id);
				}
				res = ps.executeQuery();
				log.info("Got ResultSet for that member, now processing.");

				while (res.next()) {
					totalDistanceLastYear += res.getInt("distance");
					totalOutingsLastYear++;
				}
			} catch (SQLException e) {
				log.errorException(e);
				throw new DatabaseError(rb.getString("commandError"), e);
			}
			log.exit("OutingManager.getMemberStatistics(int id)");
			return new MemberStatistic(id, member.getSurname(), member
					.getForename(), member.getDob(), member.getGroupInfo(),
					totalOutingsThisYear, totalDistanceThisYear,
					totalOutingsLastYear, totalDistanceLastYear);
		}

		/**
		 * Get the OutingStatementSet for a particular year. Automatically loads
		 * the set if required.
		 * 
		 * @param year
		 *            The year for which you want the set.
		 * @return The OutingStatementSet.
		 */
		private OutingStatementSet getOutingStatementSet(int year)
				throws SQLException {
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
				// Wait for 15 minutes
				try {
					Thread.sleep(900000l);
				} catch (Exception e) {
					// We don't care if the thread is interrupted.
				}
				// Data to check;
				Integer year = GregorianCalendar.getInstance().get(
						GregorianCalendar.YEAR);
				long now = new Date().getTime();
				// Now go through the cache:
				Enumeration<OutingStatementSet> e = statementCache.elements();
				while (e.hasMoreElements()) {
					OutingStatementSet os = e.nextElement();
					// If the set is older than 20 mins and not for the current
					// year then delete it.
					if ((os.getLastUsed() + 1200000 < now)
							&& !os.getYear().equals(year)) {
						statementCache.remove(os.getYear());
						os.close();
					}
				}
			}
		}

	}

	/* -------------------- OutingStatementSet (INTERNAL) ----------------- */

	private enum OutingStatementType {
		GET_OUTINGS, ADD_OUTING, MODIFY_OUTING, GET_MEMBER_STATISTICS
	};

	private class OutingStatementSet {

		/** The last time that this set was used. */
		private long lastUsed;

		/** The year for which this set is defined. */
		private Integer year;

		// The various prepared statements.
		private PreparedStatement psGetOutings;
		private PreparedStatement psAddOuting;
		private PreparedStatement psModifyOuting;
		private PreparedStatement psGetMemberStatistics;

		/**
		 * Set up an outingstatement set for a given year.
		 * 
		 * @param year
		 *            The year for which the set is needed.
		 * @param statementCache
		 *            The statement cache in use.
		 */
		public OutingStatementSet(Integer year,
				Hashtable<Integer, OutingStatementSet> statementCache)
				throws SQLException {
			log.entry("OutingStatementSet(Integer, HashTable");
			try {
				log.info("Trying to create a new table for year " + year + ".");
				con.createStatement().execute(
						MessageFormat.format(Util.loadScript("createOutings"),
								year.toString()));
				log.info("New Outings table for year " + year + " created.");
			} catch (SQLException e) {
				// Just ignore. Not worrying. This means the table exists.
				log.info("Outings table for this year already exists.");
				log.dbe(org.grlea.log.DebugLevel.L6_VERBOSE, e);
			} catch (IOException e) {
				// TODO : think here.
			}
			log.info("creating psGetOutings");

			psGetOutings = con
					.prepareStatement(MessageFormat
							.format(
									"SELECT * FROM outings_{0} WHERE day = ? ORDER BY time_out",
									year.toString()));
			psAddOuting = con
					.prepareStatement(MessageFormat
							.format(
									"INSERT INTO outings_{0} (day, rower1, rower2,"
											+ " rower3, rower4, rower5, rower6, rower7, rower8, "
											+ "cox, time_out, time_in, comment, destination,"
											+ "boat, distance) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?"
											+ ",?,?,?)", year.toString()));
			psModifyOuting = con.prepareStatement(MessageFormat.format(
			// "UPDATE boats SET name=?,"
					// + " type=?, inHouse=? WHERE name = ?"
					"UPDATE outings_{0} SET rower1 = ?, "
							+ "rower2 = ?, rower3 = ?, rower4 = ?, "
							+ "rower5 = ?, rower6 = ?, rower7 = ?, "
							+ "rower8 = ?, cox = ?, time_out = ?, "
							+ "time_in = ?, comment = ?, "
							+ "destination = ?, boat = ?, distance = ?"
							+ " WHERE id = ?", year.toString()));
			psGetMemberStatistics = con
					.prepareStatement(MessageFormat
							.format(
									"SELECT * FROM outings_{0} WHERE rower1 = ? "
											+ "OR rower2 = ? OR rower3 = ? OR rower4 = ? "
											+ "OR rower5 = ? OR rower6 = ? OR rower7 = ? "
											+ "OR rower8 = ? OR cox = ?",
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
			} else if (typ == OutingStatementType.MODIFY_OUTING) {
				return psModifyOuting;
			} else if (typ == OutingStatementType.GET_MEMBER_STATISTICS) {
				return psGetMemberStatistics;
			}
			return null;
		}

		/**
		 * Get when this set was last accessed.
		 * 
		 * @return The time the object was last used.
		 */
		public long getLastUsed() {
			return lastUsed;
		}

		/**
		 * Get the year for which the set is defined.
		 * 
		 * @return The year for which this set is defined.
		 */
		public Integer getYear() {
			return year;
		}

		/**
		 * Update the last used time. Should be called by any method of the
		 * class.
		 */
		private void updateTime() {
			lastUsed = new Date().getTime();
		}

		public void close() {
			log.info("Closing OutingStatementSet for year " + year.toString());
			try {
				psGetOutings.close();
				psAddOuting.close();
				psModifyOuting.close();
			} catch (SQLException e) {
				log.errorException(e);
			} finally {
			}
			year = null;
		}
	}

}