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

import java.awt.Color;

/**
 * All the information for a specific group.
 * 
 * @author Andrzej JR Hunt
 * @version 0.03 - 24. August 2008
 */
public class GroupInfo {

	/* Stores the name of the group. */
	private String name;

	/* Stores a description of the group. */
	private String description;

	/* Stores the highlighting colour of the group. */
	private Color displayColour;

	/**
	 * Create the information for a group. The colour of the group is the colour
	 * used to highlight members of the group in a member listing.
	 * 
	 * @param name
	 *            The name of the group.
	 * @param description
	 *            A description of the group.
	 * @param displayColour
	 *            The highlighting colour for the group.
	 */
	public GroupInfo(String name, String description, Color displayColour) {
		this.name = name;
		this.description = description;
		this.displayColour = displayColour;
	}

	/**
	 * Get the groups name.
	 * 
	 * @return The groups name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get a description of the group.
	 * 
	 * @return A description of the group.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Get the highlighting colour for the group.
	 * 
	 * @return The highlighting colour for the group.
	 */
	public Color getDisplayColour() {
		return displayColour;
	}

}
