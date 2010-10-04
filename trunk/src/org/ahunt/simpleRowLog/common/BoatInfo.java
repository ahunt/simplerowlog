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
 *  04/10/2010: Added an id field.
 *  24/11/2009: Cleaned up the documentation and added an exception for the
 *  			Constructor. Added data checking.
 *	23/08/2009:	Changelog added.
 */

package org.ahunt.simpleRowLog.common;

/**
 * All the information for a specific boat. This is an immutable object: data
 * can only be modified by directly addressing the database. Only the database
 * implementation should be creating these.
 * 
 * @author Andrzej JR Hunt
 * @version 0.02 - 24. November 2008
 * @see org.ahunt.simpleRowLog.interfaces.Database
 */
public class BoatInfo {

	/** Stores the boat's id. */
	private int id;

	/** Stores the name of the baot. */
	private String name;

	/** Type of Boat. E.g. 4x, 8+... */
	private String type;

	/** Whether or nor the boat is in the boathouse. */
	private boolean inHouse;

	/**
	 * Create the information for a Boat.
	 * 
	 * @param id
	 *            The boat's id.
	 * @param name
	 *            The name of the boat. Cannot be null or empty.
	 * @param type
	 *            The type of boat, in whatever style the end user wants. This
	 *            could potentially be used for categorisation of boats, so it
	 *            would be good to enforce a pattern in the UI.
	 * @param inHouse
	 *            Whether or not the boat is in the current boathouse. This
	 *            determines whether a boat is "available", i.e. visible for
	 *            selection.
	 * @throws IllegalArgumentException
	 *             If the data is supplied incorrectly.
	 */
	public BoatInfo(int id, String name, String type, boolean inHouse)
			throws IllegalArgumentException {
		if (name == null | name.length() == 0) { // Check that the boat has a
			// name.
			throw new IllegalArgumentException(
					"Name of boat cannot be null or length 0.");
		}
		this.id = id;
		this.name = name;
		if (type.length() != 0) { // If there is a string or is null assign it.
			this.type = type;
		} else { // But for zero length set to null.
			this.type = null;
		}
		this.inHouse = inHouse;
	}

	/**
	 * Get the boat's id.
	 * 
	 * @return The boat's id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Get the boats name.
	 * 
	 * @return The boats name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the type of boat.
	 * 
	 * @return The type of boat. null if no type is defined.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Check whether the boat is in the boathouse.
	 * 
	 * @return Whether the boat is in the boathouse.
	 */
	public boolean inHouse() {
		return inHouse;
	}

}
