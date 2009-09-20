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

package org.ahunt.simpleRowLog.common;

import java.util.Date;

/**
 * Create the information for a specific outing.
 * 
 * @author Andrzej JR Hunt
 * @version 0.01 - 04. November 2008
 */

public class OutingInfo {

	// Creation time.
	private long id;

	// The day this outing is registered on.
	private Date day;

	// Members at specific seats.
	private MemberInfo[] seat;

	// The coxwain.
	private MemberInfo cox;

	// Leaving time.
	private Date out;

	// Arrival time.
	private Date in;

	// The comment.
	private String comment;

	// The destination.
	private String destination;

	// The boat being used.
	private BoatInfo boat;

	// The distance rowed.
	private int distance;

	/**
	 * Creating a new outing info.
	 * 
	 * @param created
	 *            Time of creation of this outing: Is made at time of entering
	 *            into database, and used as a reference to this particular
	 *            outing.
	 * @param day
	 *            The day that this outing is logged as being in.
	 * @param seat
	 *            Array of Members in the outing.
	 * @param cox
	 *            The Member coxing the outing.
	 * @param out
	 *            Time the outing was started at.
	 * @param in
	 *            Time the outing finished at.
	 * @param destination
	 *            The destination.
	 * @param comment
	 *            A comment.
	 * @param boat
	 *            The Boat used.
	 * @param distance
	 *            The distance rowed.
	 */

	public OutingInfo(long id, Date day, MemberInfo[] seat,
			MemberInfo cox, Date out, Date in, String comment,
			String destination, BoatInfo boat, int distance) {
		this.id = id;
		this.day = day;
		this.seat = seat;
		this.cox = cox;
		this.out = out;
		this.in = in;
		this.comment = comment;
		this.destination = destination;
		this.boat = boat;
		this.distance = distance;
	}

	/**
	 * Get the boat being used in this outing.
	 * 
	 * @return The boat.
	 */
	public BoatInfo getBoat() {
		return boat;
	}

	/**
	 * Get the comment on this outing.
	 * 
	 * @return The comment.
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Get the coxwain of the boat.
	 * 
	 * @return The cox.
	 */
	public MemberInfo getCox() {
		return cox;
	}

	/**
	 * Get the unique identifier of this outing.
	 * 
	 * @return The outing id.
	 */
	public long getId() {
		return id;
	}

	/**
	 * Get the day the outing is on. The time contained in the date object is
	 * not relevant in any way.f
	 * 
	 * @return The day the outing is registered on.
	 * @see #getOut()
	 */
	public Date getDay() {
		return day;
	}

	/**
	 * Get the destination.
	 * 
	 * @return The destination.
	 */
	public String getDestination() {
		return destination;
	}

	/**
	 * Get the distance rowed.
	 * 
	 * @return The distance rowed.
	 */
	public int getDistance() {
		return distance;
	}

	/**
	 * Get the arrival time.
	 * 
	 * @return The arrival time.
	 */
	public Date getIn() {
		return in;
	}

	/**
	 * Get the departure time. Please note the Date object returned does not
	 * necessarily contain the correct date, but only the correct time. To get
	 * the date of this outing use {@link #getDay()}.
	 * 
	 * @return Departure time.
	 * @see #getDay()
	 */
	public Date getOut() {
		return out;
	}

	/**
	 * Get members in the boat.
	 * 
	 * @return The member at the specified position.
	 */
	public MemberInfo getAt(short position) {
		return seat[position];
	}

}
