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
 *  10/03/2010: Created.
 */
package org.ahunt.simpleRowLog.common;

import java.util.ArrayList;

/**
 * A list of permissions for an administrator, listing what they can do. Note
 * that if an admin is also root, they automatically gain no permissions,
 * regardless of what their permission list states.
 * 
 * @author Andrzej JR Hunt
 *
 */
public class AdminPermissionList {

	private ArrayList<String> permissionsList = new ArrayList<String>();

	private String username;

	/**
	 * Create a new, blank, list of permissions.
	 * 
	 * @param username
	 *            The admins username.
	 */
	public AdminPermissionList(String username) {
		this.username = username;
	}

	/**
	 * Create a new list and add the specificed permissions.
	 * 
	 * @param username
	 *            The admins username.
	 * @param permissions
	 *            The permissions to be set.
	 */
	public AdminPermissionList(String username, String[] permissions) {
		new AdminPermissionList(username);
		addPermissions(permissions);
	}

	/**
	 * Add all the permissions in an array to the list. All permissions already
	 * present will be ignored.
	 * 
	 * @param permissions
	 *            Ana array of the permissions to be added.
	 */
	public void addPermissions(String[] permissions) {
		for (String permission : permissions) {
			addPermission(permission);
		}
	}

	/**
	 * Remove all the permissions in an array from the list. All permissions not
	 * present in the list will be ignored.
	 * 
	 * @param permissions
	 *            The permissions to be removed.
	 */
	public void removePermissions(String[] permissions) {
		for (String permission : permissions) {
			removePermission(permission);
		}
	}

	/**
	 * Clear (remove) all the permissions in the list.
	 */
	public void clearAllPermissions() {
		permissionsList.clear();
	}

	/**
	 * Add a permission to the list.
	 * 
	 * @param permission
	 *            The permission to be added. If the permission is already
	 *            within the list then nothing is done.
	 */
	public void addPermission(String permission) {
		if (!isPermissionSet(permission)) {
			permissionsList.add(permission);
		}
	}

	/**
	 * Check whether a specific permission is set in the list.
	 * 
	 * @param permission
	 * @return
	 */
	public boolean isPermissionSet(String permission) {
		return permissionsList.contains(permission);
	}

	/**
	 * Remove a permission from the list. If the permission isn't contained in
	 * the list nothing is done.
	 * 
	 * @param permission
	 *            The permission to be removed.
	 * @return Whether or not the permission was removed.
	 */
	public boolean removePermission(String permission) {
		return permissionsList.remove(permission);
	}

	/**
	 * Get all the permissions stored in the list.
	 * 
	 * @return An array of all the permissions stored in the list.
	 */
	public String[] getAllPermissions() {
		return permissionsList.toArray(new String[permissionsList.size()]);
	}
}
