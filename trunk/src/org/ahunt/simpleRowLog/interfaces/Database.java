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

package org.ahunt.simpleRowLog.interfaces;


import java.sql.SQLException;
import java.util.Date;

import org.ahunt.simpleRowLog.common.*;

/**
 * The interface to connect to database engines used in simple rowLog. This
 * interface should be implemented in any database engines intended to be used
 * in simple rowLog. The database engine is responsible for keeping track of
 * user, boat and outing information. This interface is used by the UI's and
 * Engine to extract information from the database.
 * 
 * 
 * @author Andrzej JR Hunt
 * @version draft3 - 24. August 2008
 */
public interface Database {

	/**
	 * Specifies that lists should be sorted alphabetically.
	 * 
	 */
	public static final int SORTED_ALPHABETICALLY = 0;

	/**
	 * Specifies that lists should be sorted by group. (Or, in the case of
	 * boats, by type of boat.)
	 */
	public static final int SORTED_GROUPS = 1;

	/**
	 * Get a list of the members.
	 * 
	 * @return An Array of the members. Sorted alphabetically by name.
	 */
	public MemberInfo[] getMembers();

	/**
	 * Get a list of the members.
	 * 
	 * @param sorting
	 *            How the returned list is to be sorted.
	 * @return An Array of the members.
	 */
	public MemberInfo[] getMembers(int sorting);

	/**
	 * Get the information for a specific boat.
	 * 
	 * @param name
	 *            The name of the boat.
	 * @return The information about htis boat.
	 */
	public BoatInfo getBoatInfo(String name);

	/**
	 * Get the information for a certain member.
	 * 
	 * @param id
	 *            The id for the member.
	 * @return The member info.
	 */
	public MemberInfo getMember(int id);

	/**
	 * Get the statistics for a given member.
	 * 
	 * @param key
	 *            The members key.
	 * @return The members statistics.
	 */
	public MemberStatistic getMemberStatistics(int key);

	/**
	 * Get the statistics for all the members
	 * 
	 * @return A list of members statistics.
	 */
	public MemberStatistic[] getMembersStatistics();

	/**
	 * Get the statistics for all the members
	 * 
	 * @param sorting
	 *            How the returned list is to be sorted.
	 * @return A list of members statistics.
	 */
	public MemberStatistic[] getMembersStatistics(int sorting);

	/**
	 * Get a list of all the groups.
	 * 
	 * @return An array of all the groups, sorted alphabetically.
	 */
	public GroupInfo[] getGroups();

	/**
	 * Get a list of all the groups.
	 * 
	 * @param sorting
	 *            How the returned list is to be sorted.
	 * @return An array of all the groups.
	 */
	public GroupInfo[] getGroups(int sorting);

	/**
	 * Get the statistics for a given group. If the name is null the statistics
	 * for the entire club are returned.
	 * 
	 * @param name
	 *            The name of the group. Null for whole club.
	 * @return The groups statistics.
	 */
	public GroupStatistic getGroupStatistic(String name);

	/**
	 * Get a list of the boats.
	 * 
	 * @return A list of the boats, alphabetically sorted.
	 */
	public BoatInfo[] getBoats();

	/**
	 * Get a list of the boats.
	 * 
	 * @param sorting
	 *            How the returned list is to be sorted.
	 * @return A list of the boats.
	 */
	public BoatInfo[] getBoats(int sorting);

	/**
	 * Get the statistics for a specific boat.
	 * 
	 * @param name
	 *            The name of the boat.
	 * @return The boat's statistics.
	 */
	public BoatStatistic getBoatStatistic(String name);

	/**
	 * Get the statistics for all the boats.
	 * 
	 * @return The statistics for all the boats. Boats are sorted
	 *         alphabetically.
	 */
	public BoatStatistic[] getBoatStatistics();

	/**
	 * Get the statistics for all the boats.
	 * 
	 * @param sorting
	 *            How the returned list is to be sorted.
	 * @return The statistics for all the boats.
	 */
	public BoatStatistic[] getBoatStatistics(int sorting);

	/**
	 * Get all the outings for a specific date.
	 * 
	 * @param date
	 *            The date for which outings are to be found. Can be any time
	 *            during the day from 00:00 to 23:59:59.99.
	 * @return
	 * @throws SQLException
	 */
	public OutingInfo[] getOutings(Date date) throws SQLException;

	
	/**
	 * Create a new outing in the database.
	 * @param date The date on which the outing exists. Only the day/month/year
	 * 		values are used from this.
	 * @param rowers An array of the ids rowers in the boat. The element
	 * 		<code>rowers[0]</code> must be a valid reference, all other elements
	 * 		 may be 0, or ommited. Only the first eight elements will be
	 * 		considered.
	 * @param cox The id of the cox. 0 denotes no cox.
	 * @param timeOut The time of starting the outing.
	 * @param timeIn The time of finishing the outing. Can be <code>null</null>.
	 * @param comment A comment. Can be <code>null</null>.
	 * @param dest The destination. Can be <code>null</null>.
	 * @param boat The name of the boat. Must be a valid reference to the name
	 * 		of a boat from <code>getBoats()</code>.
	 * @param distance The distance rowed. Can be 0 to denote no distance.
	 * @throws DatabaseError Thrown if there are problems writing to the
	 * 		database or if the data is in the incorrect format.
	 */
	public void addOuting(Date date, int[] rowers,
			int cox, Date timeOut, Date timeIn, String comment, String dest,
			String boat, int distance) throws DatabaseError;
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
	 */
	public void modifyOuting(long created, MemberInfo cox, MemberInfo[] seat,
			Date out, Date in, String comment, String destination,
			BoatInfo boat, int distance);
}
