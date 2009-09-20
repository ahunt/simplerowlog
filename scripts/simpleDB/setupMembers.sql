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
# Script: setupMembers
# Requires: groups
CREATE TABLE members (
	id INT NOT NULL GENERATED BY DEFAULT (START WITH 0, INCREMENT BY 1) CONSTRAINT WISH_PK PRIMARY KEY,
	surname VARCHAR(32),
	forename VARCHAR(32),
	dob DATE NOT NULL,
	group INT,
	CONSTRAINT group_fk FOREIGN KEY (group) references groups (id),
)