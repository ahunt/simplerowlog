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
#   11/10/2009: Update & tested. Now works.
#	23/08/2009:	Changelog added.
#
#
# Script: setupMembers
# Requires: groups
CREATE TABLE members (
    id INT NOT NULL GENERATED ALWAYS AS IDENTITY UNIQUE,
    surname VARCHAR(32),
    forename VARCHAR(32),
    dob DATE NOT NULL,
    usergroup INT,
    PRIMARY KEY (surname, forename, dob),
    CONSTRAINT MEMBERS_GROUPS_FK FOREIGN KEY (usergroup) references groups (id)
)