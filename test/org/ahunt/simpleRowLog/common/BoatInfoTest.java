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
 *	06/02/2010:	Created.
 */
package org.ahunt.simpleRowLog.common;

import junit.framework.TestCase;

public class BoatInfoTest extends TestCase {

	/**
	 * Do a complete test of the class, i.e. construct and test getters.
	 */
	public void testBoatInfo() {
		String name = "Name";
		String type = "ACoolType";
		BoatInfo b = new BoatInfo(name, type, true);
		if (!b.getName().equals(name)) {
			fail("getName() not returning correct String.");
		}
		if (!b.getType().equals(type)) {
			fail("getName() not returning correct String.");
		}
		if (b.getInHouse() != true) {
			fail("inHouse has changed.");
		}
		b = new BoatInfo(name, type, false); // Test again with false for inHouse
		if (!b.getName().equals(name)) {
			fail("getName() not returning correct String.");
		}
		if (!b.getType().equals(type)) {
			fail("getName() not returning correct String.");
		}
		if (b.getInHouse() != false) {
			fail("inHouse has changed.");
		}
		// All fine.
	}

}
