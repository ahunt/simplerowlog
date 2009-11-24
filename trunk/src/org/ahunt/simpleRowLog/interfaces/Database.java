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
 *  24/11/2009: Major restructuring, almost finalised now.
 *	23/08/2009:	Changelog added.
 */

package org.ahunt.simpleRowLog.interfaces;

import java.awt.Color;
import java.util.Date;
import java.lang.IllegalArgumentException;

import org.ahunt.simpleRowLog.common.*;

//TODO: Update all methods doc to return null if necessary, + IllegalArgumentExceptions.
/**
 * The interface to connect to database engines used in simple rowLog. This
 * interface should be implemented in any database engines intended to be used
 * in simple rowLog. The database engine is responsible for keeping track of
 * user, boat and outing information. This interface is used by the UI's and
 * Engine to extract information from the database as requred.
 * 
 * @author Andrzej JR Hunt
 * @version draft4 - 24. November 2009
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
	 * what type of methods are included: A is add, G is get, M is modify.
	 * Additionaly, a + sign indicated that the operation is for multiple
	 * objects. (Just a help when designing the interface. Can be pretty much
	 * ignored.)
	 */
	/* -------------------- BOATS (INDIVIDUAL) [AGM] ------------------- */

	// TODO: link to the BoatInfo class where necessary. Complete the doc.
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
	 */
	public void addBoat(String name, String type, boolean inHouse)
			throws DatabaseError, IllegalArgumentException;

	/**
	 * Get the information for a specific boat.
	 * 
	 * @param name
	 *            The name of the boat.
	 * @return The information about htis boat. <code>null</code> if there is no
	 *         boat named such.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public BoatInfo getBoat(String name) throws DatabaseError;

	/**
	 * 
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public void modifyBoat(BoatInfo old, String name, String type,
			boolean inHouse) throws DatabaseError, IllegalArgumentException;

	/* -------------------- BOATS (GROUP) [G+] ------------------- */

	/**
	 * Get a list of the boats. This returns all boats.
	 * 
	 * @return A list of the boats, alphabetically sorted. Null if there are no
	 *         boats.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public BoatInfo[] getBoats() throws DatabaseError;

	/**
	 * Get a list of boats either in or without the boathouse.
	 * 
	 * @param inHouse
	 *            Whether you want the boats in the boathouse.
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
	 * @param name
	 *            The name of the boat.
	 * @return The boat's statistics.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public BoatStatistic getBoatStatistic(String name);

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

	/* -------------------- MEMBERS (INDIVIDUAL) [AGM] ----------------- */

	/**
	 * Returns new members id.
	 * 
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public short addMember(String surname, String forename, Date dob,
			short group) throws DatabaseError;

	/**
	 * Get the information for a certain member.
	 * 
	 * @param id
	 *            The id for the member.
	 * @return The member info.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public MemberInfo getMember(short id) throws DatabaseError;

	/**
	 * 
	 * @param id
	 * @param surname
	 * @param forename
	 * @param dob
	 * @param group
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public void modifyMember(short id, String surname, String forename,
			Date dob, short group) throws DatabaseError;

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
	 */
	public MemberInfo[] getMembers(int sorting) throws DatabaseError;

	// TODO: update description

	/* -------------------- MEMBERS - STATISTICS [G,G+] ----------------- */

	/**
	 * Get the statistics for a given member.
	 * 
	 * @param key
	 *            The members key.
	 * @return The members statistics.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public MemberStatistic getMemberStatistics(short id) throws DatabaseError;

	/**
	 * Get the statistics for all the members
	 * 
	 * @return A list of members statistics.
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

	/* -------------------- GROUPS [AGM,G+] ----------------- */
	/**
	 * Add a group to the database.
	 * 
	 * @param name
	 *            The group's name.
	 * @param description
	 *            The description.
	 * @param colour
	 *            The highlighting colour for the group.
	 * @return The group's id.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public short addGroup(String name, String description, Color colour,
			boolean isDefault) throws DatabaseError;

	/**
	 * Get the group information for a specific group
	 * 
	 * @param id
	 *            The group's id
	 * @return The group information.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public GroupInfo getGroup(short id) throws DatabaseError;

	/**
	 * Modify the group of given id.
	 * 
	 * @param id
	 *            The group's id.
	 * @param name
	 *            The new name. If null the old name will be used.
	 * @param description
	 *            The new description. If null the old description will be used.
	 * @param colour
	 *            The new colour. If null the old colour will be used.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public void modifyGroup(short id, String name, String description,
			Color colour, boolean isDefault) throws DatabaseError;

	/**
	 * Get a list of all the groups.
	 * 
	 * @return An array of all the groups, sorted alphabetically.
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public GroupInfo[] getGroups() throws DatabaseError;

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
	public GroupStatistic getGroupStatistic(short id) throws DatabaseError;

	/**
	 * 
	 * @return
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public GroupStatistic[] getGroupsStatistics() throws DatabaseError;

	/* -------------------- Outings [AG+M] ----------------- */

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
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public void addOuting(Date date, short[] rowers, short cox, Date timeOut,
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
	 * @param created
	 * @param cox
	 * @param seat
	 * @param out
	 * @param in
	 * @param comment
	 * @param destination
	 * @param boat
	 * @param distance
	 * @throws DatabaseError
	 *             If there is a problem connecting to or reading from the
	 *             database.
	 */
	public void modifyOuting(long created, MemberInfo cox, MemberInfo[] seat,
			Date out, Date in, String comment, String destination,
			BoatInfo boat, int distance);

}
