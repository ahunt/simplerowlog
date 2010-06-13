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
 * An error thrown by the database if there are problems either connecting,
 * reading from, or writing to, the database. This signalises a deep problem in
 * the program, the user should be warned, and the program exited.
 * 
 * @author Andrzej JR Hunt
 * @see org.ahunt.simpleRowLog.interfaces.Database
 */
public class DatabaseError extends Error {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -2360816381244841420L;

	/**
	 * Create a new Database Error.
	 * 
	 * @param errorMessage
	 *            The error message. This is to be logged, and is not shown to
	 *            the user. (User information is done higher up, if necessary.)
	 * @param cause
	 *            The cause of this error.
	 */
	public DatabaseError(String errorMessage, Throwable cause) {
		super(errorMessage, cause);
	}
}
