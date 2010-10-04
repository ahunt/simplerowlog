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
 *  29/11/2009: Cleaned up. Not yet completed (formatting of name).
 *	23/08/2009:	Changelog added.
 */

package org.ahunt.simpleRowLog.common;

import java.text.MessageFormat;
import java.util.Date;

import org.ahunt.simpleRowLog.conf.Configuration;

/**
 * All the information for a member. One of the possible uses is showing members
 * in the gui.
 * 
 * @author Andrzej JR Hunt
 * @version 0.05 - 29. November 2009
 */
public class MemberInfo {

	/** Stores the members id. */
	private int id;

	/** Stores the members surname. */
	private String surname;

	/** Stores the members forename. */
	private String forename;

	/** Stores the members date of birth. */
	private Date dob;

	/** Stores the members group. */
	private GroupInfo groupInfo;

	/**
	 * Create the information for a member
	 * 
	 * @param id
	 *            The members id.
	 * @param surname
	 *            The members surname. Cannot be null or empty.
	 * @param forename
	 *            The members forename. Can be empty or null.
	 * @param dob
	 *            The members date of birth. Cannot be null.
	 * @param groupInfo
	 *            The Group Information for this member. Cannot be null.
	 */
	public MemberInfo(int id, String surname, String forename, Date dob,
			GroupInfo groupInfo) {
		this.id = id;
		if (surname != null && surname.length() != 0) {
			this.surname = surname;
		} else {
			throw new IllegalArgumentException(
					"Member surname cannot be null or empty.");
		}
		if (forename != null) {
			this.forename = forename;
		} else {
			this.forename = "";
		}
		if (dob != null) {
			this.dob = dob;
		} else {
			throw new IllegalArgumentException("Date of birth cannot be null.");
		}
		if (groupInfo != null) {
			this.groupInfo = groupInfo;
		} else {
			throw new IllegalArgumentException("groupInfo cannot be null.");
		}
	}

	/**
	 * Get the members's id, a unique identifier for the member.
	 * 
	 * @return The member's id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Get the members surname.
	 * 
	 * @return The members surname.
	 */
	public String getSurname() {
		return surname;
	}

	/**
	 * Get the members forename.
	 * 
	 * @return The members forename.
	 */
	public String getForename() {
		return forename;
	}

	/**
	 * Get the members date of birth.
	 * 
	 * @return The members date of birth.
	 */
	public Date getDob() {
		return dob;
	}

	/**
	 * Get the members group.
	 * 
	 * @return The information for the members group.
	 */
	public GroupInfo getGroupInfo() {
		return groupInfo;
	}

	/**
	 * Get the member's name. The name is formatted according to the format
	 * string in main.conf[srl.name_format].
	 * 
	 * @return The member's name
	 */
	public String getName() {
		if (forename.length() == 0) { // If only a surname (e.g. Guest)
			return surname;
		} else {
			try {
				Configuration c = Configuration.getConf("main"); // Get the conf.
				return MessageFormat.format(c.getProperty("srl.name_format"), surname,
						forename);
			} catch (Exception e) {
				// TODO: error checking
			}
			return surname + "," + forename;
		}
	}

}
