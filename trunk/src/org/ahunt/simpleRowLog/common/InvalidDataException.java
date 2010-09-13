/*
 *    This file is part of simple rowLog: the open rowing logbook.
 *    Copyright (C) 2010  Andrzej JR Hunt
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
 *	07/03/2010:	Created.
 */

package org.ahunt.simpleRowLog.common;

/**
 * An error thrown by the database if there are errors with the data
 * 
 * for this entry (member, boat, etc.), e.g. if the entry already exists, or if
 * another condition isn't fulfilled.
 * 
 * @author Andrzej JR Hunt
 * @see org.ahunt.simpleRowLog.interfaces.Database
 */
public class InvalidDataException extends Exception {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = -2360816381244841420L;

	/**
	 * Create a new InvalidDataException.
	 * 
	 * @param errorMessage
	 *            The error message. It will be shown to the user.
	 * @param cause
	 *            The cause of this error (if available).
	 */
	public InvalidDataException(String errorMessage, Throwable cause) {
		super(errorMessage, cause);
	}
}
