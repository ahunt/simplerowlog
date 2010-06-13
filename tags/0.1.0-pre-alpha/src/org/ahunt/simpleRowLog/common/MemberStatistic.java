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
 *  29/11/2009: Cleaned up.
 *	23/08/2009:	Changelog added.
 */

package org.ahunt.simpleRowLog.common;

import java.util.Date;

import org.ahunt.simpleRowLog.interfaces.RowingStatistic;

/**
 * The stastics for a member. This is a simple extension of MemberInfo and can
 * be used in the same way.
 * 
 * @author Andrzej JR Hunt
 * @version 0.05 - 29. November 2009
 */
public class MemberStatistic extends MemberInfo implements RowingStatistic {

	/** Stores the number of outings in this year. */
	private int thisYearOutings;

	/** Stores the number of kilometres rowed in this year. */
	private int thisYearKM;

	/** Stores the number of outings in the last year. */
	private int lastYearOutings;

	/** Stores the number of kilometres rowed in the last year. */
	private int lastYearKM;

	/**
	 * Create a new MemberStatistic.
	 * 
	 * @param key
	 *            The members key.
	 * @param surname
	 *            The members surname. Cannot be null or empty.
	 * @param forename
	 *            The members forename.
	 * @param dob
	 *            The members date of birth. Cannot be null.
	 * @param groupInfo
	 *            The Group Information for this member. Cannot be null.
	 * @param thisYearOutings
	 *            The number of outings the member has made in this year.
	 * @param thisYearKM
	 *            The number of kilometres the member has rowed in this year.
	 * @param lastYearOutings
	 *            The number of outings the member has made the previous year.
	 * @param lastYearKM
	 *            The number of kilometres the member has rowed in the previous
	 *            year
	 */
	public MemberStatistic(int key, String surname, String forename, Date dob,
			GroupInfo groupInfo, int thisYearOutings, int thisYearKM,
			int lastYearOutings, int lastYearKM) {
		super(key, surname, forename, dob, groupInfo);
		this.thisYearOutings = thisYearOutings;
		this.thisYearKM = thisYearKM;
		this.lastYearOutings = thisYearOutings;
		this.lastYearKM = lastYearKM;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getThisYearOutings() {
		return thisYearOutings;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getThisYearKM() {
		return thisYearKM;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getLastYearOutings() {
		return lastYearOutings;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getLastYearKM() {
		return lastYearKM;
	}
}
