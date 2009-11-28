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
 *  28/11/2009: Added checking in constructor. Finished doc.
 *	17/10/2009: Updated to store id.
 *	23/08/2009:	Changelog added.
 */

package org.ahunt.simpleRowLog.common;

import java.awt.Color;

/**
 * All the information for a specific group.
 * 
 * @author Andrzej JR Hunt
 * @version 0.05 - 28. November 2009
 */
public class GroupInfo {

	/** Stores the groups id. */
	private int id;

	/** Stores the name of the group. */
	private String name;

	/** Stores a description of the group. */
	private String description;

	/** Stores the highlighting colour of the group. */
	private Color displayColour;

	/** Stores whether this group is default. */
	private boolean isDefault;

	/**
	 * Create the information for a group. The colour of the group is the colour
	 * used to highlight members of the group in a member listing.
	 * 
	 * @param id
	 *            The groups's id.
	 * @param name
	 *            The name of the group. Cannot be null or empty.
	 * @param description
	 *            A description of the group.
	 * @param displayColour
	 *            The highlighting colour for the group. Cannot be null.
	 * @param isDefault
	 *            Whether this is the default Group.
	 */
	public GroupInfo(int id, String name, String description,
			Color displayColour, boolean isDefault) {
		this.id = id;
		if (name != null && name.length() > 0) {
			this.name = name;
		} else {
			throw new IllegalArgumentException(
					"Name of group cannot be null or length 0.");
		}
		this.description = description;
		if (displayColour != null) {
			this.displayColour = displayColour;
		} else {
			throw new IllegalArgumentException("Colour cannot be null");
		}
		this.isDefault = isDefault;
	}

	/**
	 * Get the group's id.
	 * 
	 * @return
	 */
	public int getId() {
		return id;
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

	/**
	 * Check whether this is the default group.
	 * 
	 * @return Whether the group is default.
	 */
	public boolean getIsDefault() {
		return isDefault;
	}

}
