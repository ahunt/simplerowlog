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
 * that if an admin is also root, they automatically gain all permissions,
 * regardless of what their permission list states.
 * 
 * @author Andrzej JR Hunt
 * 
 */
public abstract class AdminPermissionList {

	private ArrayList<String> permissionsList = new ArrayList<String>();

	private String username;

	/**
	 * Whether or not to store any modification directly, as opposed to waiting
	 * to be told to do so.
	 */
	private boolean autoStore;
	
	/**
	 * Whether or not this is the root user.
	 */
	private boolean isRoot;

	/**
	 * Create a new, blank, list of permissions.
	 * 
	 * @param username
	 *            The admin's username.
	 * @param autoStore
	 *            Whether or not the list should automatically be stored on
	 *            modification.
	 */
	public AdminPermissionList(String username, boolean isRoot, boolean autoStore) {
		this.username = username;
		this.isRoot = isRoot;
		this.autoStore = autoStore;
	}

	/**
	 * Create a new, blank, list of permissions, with auto-storing of
	 * modifications enabled.
	 * 
	 * @param username
	 *            The admin's username.
	 */
	public AdminPermissionList(String username, boolean isRoot) {
		this(username, isRoot, true);
	}

	/**
	 * Create a new list and add the specificed permissions.
	 * 
	 * @param username
	 *            The admins username.
	 * @param permissions
	 *            The permissions to be set.
	 * @param autoStore
	 *            Whether or not auto-storing should be enabled.
	 */
	public AdminPermissionList(String username, boolean isRoot, String[] permissions,
			boolean autoStore) {
		this(username, isRoot, autoStore);
		addPermissions(permissions);
	}

	/**
	 * Create a new list and add the specificed permissions, with auto-storing
	 * enabled.
	 * 
	 * @param username
	 *            The admins username.
	 * @param permissions
	 *            The permissions to be set.
	 */
	public AdminPermissionList(String username, boolean isRoot, String[] permissions) {
		this(username, isRoot, permissions, true);
	}

	/**
	 * Set whether the list should automatically be stored on modification, i.e.
	 * whether or not the changes are stored directly to the database. If many
	 * changes are being made it is wise to switch this off, and then store at
	 * the end of the changes.
	 * 
	 * @param autoStore
	 */
	public void setAutoStore(boolean autoStore) {
		this.autoStore = autoStore;
	}

	/**
	 * Whether or not auto-storing is turned on.
	 * 
	 * @return Auto-storing status.
	 */
	public boolean getAutoStore() {
		return autoStore;
	}

	/**
	 * What user these permissions are for.
	 * 
	 * @return The user for which these permissions are valid.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Add all the permissions in an array to the list. All permissions already
	 * present will be ignored.
	 * 
	 * @param permissions
	 *            An array of the permissions to be added.
	 */
	public void addPermissions(String[] permissions) {
		boolean autoStoreWasSet = false; // Don't store each change but store
		if (autoStore) { // once the changes are complete.
			autoStoreWasSet = true;
			autoStore = false;
		}

		for (String permission : permissions) {
			addPermission(permission);
		}

		if (autoStoreWasSet) { // Change back and store if required.
			autoStore = true;
			storePermissions();
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
		boolean autoStoreWasSet = false; // Don't store each change but store
		if (autoStore) { // once the changes are complete.
			autoStoreWasSet = true;
			autoStore = false;
		}

		for (String permission : permissions) {
			removePermission(permission);
		}

		if (autoStoreWasSet) { // Change back and store if required.
			autoStore = true;
			storePermissions();
		}
	}

	/**
	 * Clear (remove) all the permissions in the list.
	 */
	public void clearAllPermissions() {
		permissionsList.clear();
		if (autoStore)
			storePermissions();
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
		if (autoStore) // Store if wanted
			storePermissions();
	}

	/**
	 * Check whether a specific permission is set in the list.
	 * 
	 * @param permission
	 * @return
	 */
	public boolean isPermissionSet(String permission) {
		if (isRoot) return true;
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
		boolean result = permissionsList.remove(permission);
		if (autoStore && result) // Only if storing and something was removed.
			storePermissions();
		return result;
	}

	/**
	 * Get all the permissions stored in the list.
	 * 
	 * @return An array of all the permissions stored in the list.
	 */
	public String[] getAllPermissions() {
		return permissionsList.toArray(new String[permissionsList.size()]);
	}

	/**
	 * Store the permissions to the database.
	 */
	public abstract void storePermissions();

}
