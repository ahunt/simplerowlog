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
# Script: createOutings
# Requires: mebers, boats
# note: a seperate table is made for each year. (Question mark in outings_? is
# for the year.)
CREATE TABLE outings_? (
	id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1)CONSTRAINT WISH_PK PRIMARY KEY,
	day DATE NOT NULL,
	rower1 INT NOT NULL,
	rower2 INT,
	rower3 INT,
	rower4 INT,
	rower5 INT,
	rower6 INT,
	rower7 INT,
	rower8 INT,
	cox INT,
	time_out BIGINT NOT NULL,
	time_in BIGINT,
	comment VARCHAR(256),
	destination VARCHAR(128),
	boat VARCHAR(32) NOT NULL,
	distance INT,
	CONSTRAINT rw1_fk FOREIGN KEY (rower1) references members (id),
	CONSTRAINT rw2_fk FOREIGN KEY (rower2) references members (id),
	CONSTRAINT rw3_fk FOREIGN KEY (rower3) references members (id),
	CONSTRAINT rw4_fk FOREIGN KEY (rower4) references members (id),
	CONSTRAINT rw5_fk FOREIGN KEY (rower5) references members (id),
	CONSTRAINT rw6_fk FOREIGN KEY (rower6) references members (id),
	CONSTRAINT rw7_fk FOREIGN KEY (rower7) references members (id),
	CONSTRAINT rw8_fk FOREIGN KEY (rower8) references members (id),
	CONSTRAINT cox_fk FOREIGN KEY (cox) references members (id),
	CONSTRAINT boat_fk FOREIGN KEY (boat) references boats (name)
)