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

/**
 * All the information for a specific group.
 * 
 * @author Andrzej JR Hunt
 * @version 0.01 - 21. September 2008
 */
public class BoatInfo {

	/** Stores the name of the group. */
	private String name;

	/** Type of Boat. E.g. 4x, 8+... */
	private String type;
	
	/** Whether or nor the boat is in the boathouse. */
	private boolean inHouse;

	/**
	 * Create the information for a Boat.
	 * 
	 * @param name
	 *            The name of the group.
	 * @param type
	 *            The type of boat, in whatever style the end user wants.
	 */
	public BoatInfo(String name, String type, boolean inHouse) {
		this.name = name;
		this.type = type;
		this.inHouse = inHouse;
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
	 * @return The type of boat.
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Check whether the boat is in the boathouse.
	 * @return Whether the boat is in the boathouse.
	 */
	public boolean getInHouse() {
		return inHouse;
	}

}
