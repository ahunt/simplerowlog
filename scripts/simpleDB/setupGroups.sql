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
#	23/08/2009:	Changelog added.
#
#
# Script: setupGroups
# Requires:
CREATE TABLE groups (
	id INT NOT NULL GENERATED BY DEFAULT (START WITH 0, INCREMENT BY 1) UNIQUE CONSTRAINT WISH_PK PRIMARY KEY,
	name VARCHAR(32) NOT NULL,
	description VARCHAR(64),
	colour INT NOT NULL,
	isDefault BIT NOT NULL,
	isPermanent BIT NOT NULL
)