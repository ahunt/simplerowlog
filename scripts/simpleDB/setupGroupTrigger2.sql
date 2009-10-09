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
#   09/10/2009: Modified and tested -- now works.
#	23/08/2009:	Changelog added.
#
#
# Script: setupGroupTrigger2
CREATE TRIGGER trig_defaultGroup2 AFTER UPDATE ON groups
REFERENCING NEW AS mod
FOR EACH ROW MODE DB2SQL
UPDATE groups SET isDefault = 0 WHERE (isDefault = 1) AND ((NOT (id = mod.id)) AND (mod.isDefault = 1)) 