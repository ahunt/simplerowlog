package org.ahunt.simpleRowLog.common;

import java.util.ArrayList;

public class AdminPermissionList {

	private ArrayList<String> permissionsList = new ArrayList<String>();
	
	private String username;
	
	public AdminPermissionList(String username) {
		this.username = username;
	}
	
	public boolean isPermissionSet(String permission) {
		return permissionsList.contains(permission);
	}
}
