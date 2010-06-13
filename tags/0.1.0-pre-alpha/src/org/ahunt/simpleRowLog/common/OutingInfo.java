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
 *  25/01/2010: Cleaned up again, bug-fix where comment and destination were
 *  			mixed up.
 *  29/11/2009: Cleaned up and added checking in the constructor.
 *	23/08/2009:	Changelog added.
 */

package org.ahunt.simpleRowLog.common;

import java.util.Date;

/**
 * Create the information for a specific outing.
 * 
 * @author Andrzej JR Hunt
 * @version 0.03 - 25. January 2010
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
	 * Creating a new outing info. Unless otherwise noted below, arguments can
	 * be null.
	 * 
	 * @param created
	 *            Time of creation of this outing: Is made at time of entering
	 *            into database, and used as a reference to this particular
	 *            outing.
	 * @param day
	 *            The day that this outing is logged as being in. Cannot be
	 *            null.
	 * @param seat
	 *            Array of Members in the outing. Must be an array of length 8,
	 *            with member 0 not null.
	 * @param cox
	 *            The Member coxing the outing.
	 * @param out
	 *            Time the outing was started at. Cannot be null.
	 * @param in
	 *            Time the outing finished at.
	 * @param destination
	 *            The destination.
	 * @param comment
	 *            A comment.
	 * @param boat
	 *            The Boat used. Cannot be null.
	 * @param distance
	 *            The distance rowed.
	 */

	public OutingInfo(long id, Date day, MemberInfo[] seat, MemberInfo cox,
			Date out, Date in, String comment, String destination,
			BoatInfo boat, int distance) {
		this.id = id;
		// Day of outing
		if (day != null) {
			this.day = day;
		} else {
			throw new IllegalArgumentException("Day cannot be null.");
		}
		// The array of members
		if (seat == null) { // Various checking for seat[]
			throw new IllegalArgumentException("seat[] cannot be null.");
		} else if (seat.length != 8) {
			throw new IllegalArgumentException("seat[] must have length 8");
		} else if (seat[0] == null) {
			throw new IllegalArgumentException(
					"seat[0] (member 0) cannot be null");
		} else {
			this.seat = seat;
		}
		// cox
		this.cox = cox;
		// Time out.
		if (out != null) {
			this.out = out;
		} else {
			throw new IllegalArgumentException("out cannot be null");
		}
		// Time in
		this.in = in;
		// Comment
		if (comment != null) {
			this.comment = comment;
		} else {
			this.comment = ""; // Empty string if null is given as comment
		}
		// destination
		if (destination != null) {
			this.destination = destination;
		} else {
			this.destination = ""; // Empty string if null is given as comment
		}
		// boat
		if (boat != null) {
			this.boat = boat;
		} else {
			throw new IllegalArgumentException("Boat cannot be null.");
		}
		// distance
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
	 * @return The cox. null if there is none.
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
	 * @return The arrival time. null if no time has been filled in.
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
	public MemberInfo[] getRowers() {
		return seat;
	}

}
