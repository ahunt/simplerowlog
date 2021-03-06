#
#    This file is part of simple rowLog: the open rowing logbook.
#    Copyright (C) 2010  Andrzej JR Hunt
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
#   08/03/2010: Created.
#
# Script: setupAdmins
# Requires: -
CREATE TABLE admins (
    username VARCHAR(32) NOT NULL CONSTRAINT ADMINS_PK PRIMARY KEY,
    password VARCHAR(32) FOR BIT DATA,
    salt VARCHAR(64) FOR BIT DATA,
    name VARCHAR(32),
    isRoot INT,
    comment VARCHAR(512)
)