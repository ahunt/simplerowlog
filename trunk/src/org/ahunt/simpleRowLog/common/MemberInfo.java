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
 * An object carrying all the information about a member. One of the possible
 * uses is showing members in the gui.
 * 
 * @author Andrzej JR Hunt
 * @version 0.04 - 8. August 2008
 */
public class MemberInfo {

	/* Stores the members key. */
	private short key;

	/* Stores the members surname. */
	private String surname;

	/* Stores the members forename. */
	private String forename;

	/* Stores the members date of birth. */
	private Date dob;

	/* Stores the members group. */
	private GroupInfo groupInfo;

	/**
	 * Create a new MemberInfo.
	 * 
	 * @param key
	 *            The members key.
	 * @param surname
	 *            The members surname.
	 * @param forename
	 *            The members forename.
	 * @param dob
	 *            The members date of birth.
	 * @param groupInfo
	 *            The Group Information for this member.
	 */
	public MemberInfo(short key, String surname, String forename, Date dob,
			GroupInfo groupInfo) {
		this.key = key;
		this.surname = surname;
		this.forename = forename;
		this.dob = dob;
		this.groupInfo = groupInfo;
	}

	/**
	 * Get the members key, a unique identifier for the member.
	 * 
	 * @return The members key.
	 */
	public short getKey() {
		return key;
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

}
