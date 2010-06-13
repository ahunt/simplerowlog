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

/**
 * 
 * @author Andrzej JR Hunt
 * @version 1 24. August 2008
 */
public interface RowingStatistic {

	/**
	 * Get the number of outings rowed this year.
	 * 
	 * @return The number of outings rowed in the current year.
	 */
	public int getThisYearOutings();

	/**
	 * Get the number of kilometres rowed this year.
	 * 
	 * @return The number of kilometres rowed in the current year
	 */
	public int getThisYearKM();

	/**
	 * Get the number of outings rowed in the previous year.
	 * 
	 * @return The number of outings rowed in the previous year.
	 */
	public int getLastYearOutings();

	/**
	 * Get the number of kilometres rowed in the previous year.
	 * 
	 * @return The number of kilometres rowed in the previous year
	 */
	public int getLastYearKM();

}