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
# Script: setupAdminsTrigger
# Makes sure if there's an update, only one user is root.
CREATE TRIGGER trig_adminRoot1 AFTER UPDATE ON admins
REFERENCING NEW AS mod
FOR EACH ROW MODE DB2SQL
UPDATE  SET admins = 0 WHERE (isRoot = 1) AND ((NOT (username = mod.username)) AND (mod.isRoot = 1))

