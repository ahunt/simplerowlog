#
#    This file is part of simple rowLog: the open rowing logbook.
#    Copyright (C) 2009  Andrzej JR Hunt
#    
#    simple rowLog is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    simple rowLog is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with simple rowLog.  If not, see <http://www.gnu.org/licenses/>.
#
#
#	Changelog:
#   29/11/2009: And now from INT to SMALLINT for space reasons.
#   11/10/2009: Update inHouse from BIT to INT, now works.
#	23/08/2009:	Changelog added.
#
#
# Script: setupBoats
# no prerequisites.
CREATE TABLE boats (
	name VARCHAR(32) NOT NULL CONSTRAINT WISH_PK PRIMARY KEY,
	type VARCHAR(16),
	inHouse SMALLINT
)