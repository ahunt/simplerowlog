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
#   11/10/2009: Updated & corrected.
#	23/08/2009:	Changelog added.
#
#
# Script: createOutings
# Requires: mebers, boats
# note: a seperate table is made for each year. (Question mark in outings_? is
# for the year.) This must be called using MessageFormat.format(loadFile(), year);
CREATE TABLE outings_{0} (
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY CONSTRAINT OUTINGS{0}_PK PRIMARY KEY,
	day DATE NOT NULL,
	rower1 SMALLINT NOT NULL,
	rower2 SMALLINT,
	rower3 SMALLINT,
	rower4 SMALLINT,
	rower5 SMALLINT,
	rower6 SMALLINT,
	rower7 SMALLINT,
	rower8 SMALLINT,
	cox SMALLINT,
	time_out BIGINT NOT NULL,
	time_in BIGINT,
	comment VARCHAR(256),
	destination VARCHAR(128),
	boat VARCHAR(32) NOT NULL,
	distance INT,
	CONSTRAINT rw1_fk_{0} FOREIGN KEY (rower1) references members (id),
	CONSTRAINT rw2_fk_{0} FOREIGN KEY (rower2) references members (id),
	CONSTRAINT rw3_fk_{0} FOREIGN KEY (rower3) references members (id),
	CONSTRAINT rw4_fk_{0} FOREIGN KEY (rower4) references members (id),
	CONSTRAINT rw5_fk_{0} FOREIGN KEY (rower5) references members (id),
	CONSTRAINT rw6_fk_{0} FOREIGN KEY (rower6) references members (id),
	CONSTRAINT rw7_fk_{0} FOREIGN KEY (rower7) references members (id),
	CONSTRAINT rw8_fk_{0} FOREIGN KEY (rower8) references members (id),
	CONSTRAINT cox_fk_{0} FOREIGN KEY (cox) references members (id),
	CONSTRAINT boat_fk_{0} FOREIGN KEY (boat) references boats (name)
)