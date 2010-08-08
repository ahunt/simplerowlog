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
 *  07/08/2010: Added methods to delete members, boats, etc. Also added
 *  			EntryAlreadyExistsException to all relevant methods.
 *  25/01/2010: Modified modifyOuting to use ints of members.
 *  29/11/2009: Completed.
 *  24/11/2009: Major restructuring, almost finalised now.
 *	23/08/2009:	Changelog added.
 */

package org.ahunt.simpleRowLog.interfaces;

import java.awt.Color;
import java.util.Date;
import java.lang.IllegalArgumentException;

import org.ahunt.simpleRowLog.common.*;

/**
 * The interface to connect to database engines used in simple rowLog. This
 * interface should be implemented in any database engines intended to be used
 * in simple rowLog. The database engine is responsible for keeping track of
 * user, boat and outing information. This interface is used by the UI's and
 * Engine to extract information from the database as requred.
 * 
 * If you are wanting certain results, it is recommended to statically import
 * the constants here as such: <code>
 * import static org.ahunt.simpleRowLog.interfaces.Database.*;
 * </code> Meaning you can now directly reference
 * the SORTING_* constants.
 * 
 * 
 * @author Andrzej JR Hunt
 * @version draft7 - 7. August 2010
 */
public interface Database {

	/**
	 * Specifies that lists should be sorted alphabetically.
	 * 
	 */
	public static final int SORT_ALPHABETICALLY_FORENAME = 0;

	/**
	 * Specifies that lists should be sorted by surname.
	 */
	public static final int SORTED_ALPHABETICALLY_SURNAME = 1;

	/**
	 * Specifies that the list should be sorted by group. (This is of greater
	 * significance than the alphabetical sorting.) Can be bitwise OR'd with
	 * alphabetical sorting. E.g. to get surname and group sorting use
	 * <code>SORTED_GROUP|SORTED_SURNAME</code>.
	 */
	public static final int SORTED_GROUP = 2;

	/**
	 * Specifies that the list should be sorted by individuals (i.e. ignoring
	 * groups).
	 */
	public static final int SORTED_INDIVIDUAL = 0;

	/*
	 * Note: In the "categories below: The square bracketed code is to tell you
	 * what type of methods are included: A is add, G is get, M is modify, R is
	 * remove. Additionally, a + sign indicated that the operation is for
	 * multiple objects. (Just a help when designing the interface. Can be
	 * pretty much ignored.)
	 */
	/* -------------------- BOATS (INDIVIDUAL) [AGMR] ------------------- */

	/**
	 * Add a new boat to the list of boats.
	 * 
	 * @param name
	 *            The name of the boat. Cannot be null or empty.
	 * @param type
	 *            A string describing the type of boat. May be null.
	 * @param inHouse
	 *            Whether the boat is in the boats house, or available.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 * @throws IllegalArgumentException
	 *             If the supplied data is not valid.
	 * @throws EntryAlreadyExistsException
	 *             If there already is a boat with the specified name in the
	 *             database.
	 * @see BoatInfo
	 */
	public void addBoat(String name, String type, boolean inHouse)
			throws DatabaseError, IllegalArgumentException,
			EntryAlreadyExistsException;

	/**
	 * Get the information for a specific boat.
	 * 
	 * @param name
	 *            The name of the boat.
	 * @return The information about this boat. <code>null</code> if there is no
	 *         boat named such.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public BoatInfo getBoat(String name) throws DatabaseError;

	/**
	 * Modify a boat entry in the database.
	 * 
	 * @param old
	 *            The old BoatInfo object to be modified. (Note this itself
	 *            isn't modified.) Cannot be null.
	 * @param name
	 *            The new name. Cannot be null.
	 * @param type
	 *            The type of boat.
	 * @param inHouse
	 *            Whether the boat is in the boathouse.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 * @throws EntryAlreadyExistsException
	 *             If there already is a boat with the specified name in the
	 *             database.
	 * @see BoatInfo
	 */
	public void modifyBoat(BoatInfo old, String name, String type,
			boolean inHouse) throws DatabaseError, EntryAlreadyExistsException;

	/**
	 * Remove a boat from the database, replacing all its' entries in Outings
	 * with a different boat.
	 * 
	 * @param boat
	 *            The boat to be removed.
	 * @param replacement
	 *            The boat that should replace this boat in all relevant entries
	 *            in the database, e.g. in Outings.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public void removeBoat(BoatInfo boat, BoatInfo replacement)
			throws DatabaseError;

	/* -------------------- BOATS (GROUP) [G+] ------------------- */

	/**
	 * Get a list of the boats.
	 * 
	 * @return A list of all the boats, alphabetically sorted. Null if there are
	 *         no boats.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public BoatInfo[] getBoats() throws DatabaseError;

	/**
	 * Get a list of boats either in or without the boathouse.
	 * 
	 * @param inHouse
	 *            Whether you want the boats that are available, or those that
	 *            are away.
	 * @return A list of boats.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public BoatInfo[] getBoats(boolean inHouse) throws DatabaseError;

	/* -------------------- BOATS - STATISTICS [G,G+] ------------------- */

	/**
	 * Get the statistics for a specific boat.
	 * 
	 * @param boat
	 *            The boat whose statistics are to be retrieved.
	 * @return The boat's statistics.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public BoatStatistic getBoatStatistic(BoatInfo boat);

	/**
	 * Get the statistics for all the boats.
	 * 
	 * @return The statistics for all the boats. Boats are sorted
	 *         alphabetically.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public BoatStatistic[] getBoatsStatistics();

	/* -------------------- MEMBERS (INDIVIDUAL) [AGMR] ----------------- */

	/**
	 * 
	 * Add a new member to the database.
	 * 
	 * @return The new member's id.
	 * @param surname
	 *            The person's surname. Cannot be null or empty.
	 * @param forename
	 *            The person's forname.
	 * @param dob
	 *            The date of birth. Cannot be null.
	 * @param group
	 *            The person's group. Must be a valid group id.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 * @throws EntryAlreadyExistsException
	 *             If there already is a member with the same names and dob in
	 *             database.
	 */
	public int addMember(String surname, String forename, Date dob, int group)
			throws DatabaseError, EntryAlreadyExistsException;

	/**
	 * Get the information for a certain member.
	 * 
	 * @param id
	 *            The member's id.
	 * @return The member info. null if there is no member with the specified
	 *         id.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public MemberInfo getMember(int id) throws DatabaseError;

	/**
	 * Modify the details for a member in the database.
	 * 
	 * Note: All details must be filled in: if you aren't changing a detail,
	 * then use the information currently available. Leaving an argument empty
	 * means that that property will be set as empty.
	 * 
	 * @param member
	 *            The member to be modified.
	 * @param surname
	 *            The member's new surname. Cannot be null or empty.
	 * @param forename
	 *            The member's new forename.
	 * @param dob
	 *            The member's new date of birth.
	 * @param group
	 *            The member's new group.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 * @throws EntryAlreadyExistsException
	 *             If there already is a member with the same names and dob in
	 *             database.
	 */
	public void modifyMember(MemberInfo member, String surname,
			String forename, Date dob, int group) throws DatabaseError,
			EntryAlreadyExistsException;

	/**
	 * Remove a member from the database, replacing all their entries in Outings
	 * with a different member.
	 * 
	 * @param member
	 *            The member to be removed.
	 * @param replacement
	 *            The member to replace the removed member in all relevant
	 *            entries in the databse.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public void removeMember(MemberInfo member, MemberInfo replacement)
			throws DatabaseError;

	/* -------------------- MEMBERS (GROUP) [G+] ----------------- */

	/**
	 * Get a list of the members.
	 * 
	 * @return An Array of the members. Sorted alphabetically by name.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public MemberInfo[] getMembers() throws DatabaseError;

	/**
	 * Get a list of the members.
	 * 
	 * @param sorting
	 *            How the returned list is to be sorted.
	 * @return An Array of the members.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 * @see #SORT_ALPHABETICALLY_FORENAME
	 * @see #SORTED_ALPHABETICALLY_SURNAME
	 * @see #SORTED_GROUP
	 * @see #SORTED_INDIVIDUAL
	 */
	public MemberInfo[] getMembers(int sorting) throws DatabaseError;

	/* -------------------- MEMBERS - STATISTICS [G,G+] ----------------- */

	/**
	 * Get the statistics for a given member.
	 * 
	 * @param member
	 *            The member whose statistics are to be retrieved.
	 * @return The member's statistics.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public MemberStatistic getMemberStatistics(MemberInfo member)
			throws DatabaseError;

	/**
	 * Get the statistics for all the members.
	 * 
	 * @return A list of members' statistics.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public MemberStatistic[] getMembersStatistics() throws DatabaseError;

	/**
	 * Get the statistics for all the members
	 * 
	 * @param sorting
	 *            How the returned list is to be sorted.
	 * @return A list of members statistics.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public MemberStatistic[] getMembersStatistics(int sorting)
			throws DatabaseError;

	/* -------------------- GROUPS [AGMR,G+] ----------------- */

	/**
	 * Add a group to the database.
	 * 
	 * @param name
	 *            The group's name. Cannot be null or empty.
	 * @param description
	 *            The description.
	 * @param colour
	 *            The highlighting colour for the group. Cannot be null.
	 * @return The group's id.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 * @throws EntryAlreadyExistsException
	 *             If there already is a group with the same name in the
	 *             database.
	 */
	public int addGroup(String name, String description, Color colour,
			boolean isDefault) throws DatabaseError,
			EntryAlreadyExistsException;

	/**
	 * Get the group information for a specific group
	 * 
	 * @param id
	 *            The group's id.
	 * @return The group information. Null if there is no such group.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public GroupInfo getGroup(int id) throws DatabaseError;

	/**
	 * Modify the group of given id. Also, all fields are overwritten using the
	 * data here, so explicitly supply the old data if you don't modify it.
	 * 
	 * @param group
	 *            The group to be modified.
	 * @param name
	 *            The new name. Cannot be null or empty.
	 * @param description
	 *            The new description.
	 * @param colour
	 *            The new colour.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 * @throws EntryAlreadyExistsException
	 *             If there already is a group with the same name in the
	 *             database.
	 */
	public void modifyGroup(GroupInfo group, String name, String description,
			Color colour, boolean isDefault) throws DatabaseError,
			EntryAlreadyExistsException;

	/**
	 * Remove a group from the database, changing all members of the group to a
	 * different group.
	 * 
	 * @param group
	 *            The group to be removed.
	 * @param replacement
	 *            The replacement group.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public void removeGroup(GroupInfo group, GroupInfo replacement)
			throws DatabaseError;

	/**
	 * Get a list of all the groups.
	 * 
	 * @return An array of all the groups, sorted alphabetically.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public GroupInfo[] getGroups() throws DatabaseError;

	/**
	 * Get the default group.
	 * 
	 * @return The default group.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public GroupInfo getDefaultGroup() throws DatabaseError;

	/* -------------------- GROUPS - STATISTICS [G,G+] ----------------- */

	/**
	 * Get the statistics for a given group. If the name is null the statistics
	 * for the entire club are returned.
	 * 
	 * @param name
	 *            The name of the group. 0 for whole club.
	 * @return The groups statistics.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public GroupStatistic getGroupStatistic(int id) throws DatabaseError;

	/**
	 * Get the statistics for all the groups.
	 * 
	 * @return The statistics for all the groups.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public GroupStatistic[] getGroupsStatistics() throws DatabaseError;

	/* -------------------- Outings [AG+MR] ----------------- */

	/**
	 * Create a new outing in the database.
	 * 
	 * @param date
	 *            The date on which the outing exists. Only the day/month/year
	 *            values are used from this.
	 * @param rowers
	 *            An array of the ids rowers in the boat. The element
	 *            <code>rowers[0]</code> must be a valid reference, all other
	 *            elements may be 0, or ommited. Only the first eight elements
	 *            will be considered.
	 * @param cox
	 *            The id of the cox. 0 denotes no cox.
	 * @param timeOut
	 *            The time of starting the outing.
	 * @param timeIn
	 *            The time of finishing the outing. Can be <code>null</null>.
	 * @param comment
	 *            A comment. Can be <code>null</null>.
	 * @param dest
	 *            The destination. Can be <code>null</null>.
	 * @param boat
	 *            The name of the boat. Must be a valid reference to the name of
	 *            a boat from <code>getBoats()</code>.
	 * @param distance
	 *            The distance rowed. Can be 0 to denote no distance.
	 * @return The outing's creation time (id).
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public long addOuting(Date date, int[] rowers, int cox, Date timeOut,
			Date timeIn, String comment, String dest, String boat, int distance)
			throws DatabaseError;

	/**
	 * Get all the outings for a specific date.
	 * 
	 * @param date
	 *            The date for which outings are to be found. Can be any time
	 *            during the day from 00:00 to 23:59:59.99.
	 * @return
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public OutingInfo[] getOutings(Date date) throws DatabaseError;

	/**
	 * Modify an outing.
	 * 
	 * @param outing
	 *            The outing to be modified.
	 * @param created
	 *            The creation time / day.
	 * @param date
	 *            The date on which the outing exists. Only the day/month/year
	 *            values are used from this.
	 * @param rowers
	 *            An array of the ids rowers in the boat. The element
	 *            <code>rowers[0]</code> must be a valid reference, all other
	 *            elements may be 0, or ommited. Only the first eight elements
	 *            will be considered.
	 * @param cox
	 *            The id of the cox. 0 denotes no cox.
	 * @param timeOut
	 *            The time of starting the outing.
	 * @param timeIn
	 *            The time of finishing the outing. Can be <code>null</null>.
	 * @param comment
	 *            A comment. Can be <code>null</null>.
	 * @param dest
	 *            The destination. Can be <code>null</null>.
	 * @param boat
	 *            The name of the boat. Must be a valid reference to the name of
	 *            a boat from <code>getBoats()</code>.
	 * @param distance
	 *            The distance rowed. Can be 0 to denote no distance.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public void modifyOuting(OutingInfo outing, long day, int[] rowers,
			int cox, Date out, Date in, String comment, String destination,
			String boat, int distance) throws DatabaseError;

	/**
	 * Remove an outing from the database.
	 * 
	 * @param outing
	 *            The outing to be removed.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public void removeOuting(OutingInfo outing) throws DatabaseError;

	/* -------------------- Admins [AG,G+,M,R] ----------------- */

	/**
	 * Add an administrator to the databse.
	 * 
	 * @param username
	 *            The username for the administrator.
	 * @param password
	 *            The password: this array will be nulled after use by this
	 *            method.
	 * @param name
	 *            The real name of the administrator.
	 * @param isRoot
	 *            Whether or not this admin is to be set as the root
	 *            administrator.
	 * @param comment
	 *            Any comments about the administrator.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 * @throws EntryAlreadyExistsException
	 *             If an admin with the same username already exists in the
	 *             database.
	 */
	public void addAdmin(String username, char[] password, String name,
			boolean isRoot, String comment) throws DatabaseError,
			EntryAlreadyExistsException;

	/**
	 * Get the admin info for a specific admin.
	 * 
	 * @param username
	 *            The username.
	 * @return The AdminInfo for a specific admin.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public AdminInfo getAdmin(String username) throws DatabaseError;

	/**
	 * Get a list of the admins in the databse.
	 * 
	 * @return A list of all admins.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public AdminInfo[] getAdmins() throws DatabaseError;

	/**
	 * Modify an administrators information in the database. Note that to change
	 * their password you will need to use
	 * {@link #setNewAdminPassword(AdminInfo, char[])}.
	 * 
	 * @param admin
	 *            The administrator to be modified.
	 * @param username
	 *            The username for the administrator.
	 * @param name
	 *            The real name of the administrator.
	 * @param isRoot
	 *            Whether or not this admin is to be set as the root
	 *            administrator.
	 * @param comment
	 *            Any comments about the administrator.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 * @throws EntryAlreadyExistsException
	 *             If an admin with the same username already exists in the
	 *             database.
	 */
	public void modifyAdmin(AdminInfo admin, String username, String name,
			boolean isRoot, String comment) throws DatabaseError,
			EntryAlreadyExistsException;

	/**
	 * Change an admins password.
	 * 
	 * @param admin
	 *            The admin whose password is to be changed.
	 * @param password
	 *            The new password: this array will be nulled after use by this
	 *            method.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public void setNewAdminPassword(AdminInfo admin, char[] password)
			throws DatabaseError;

}
