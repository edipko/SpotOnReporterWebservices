package com.sor.beans;

public class User {

	private boolean authenticated;
	private String username;
	private int projectID;
	private int permissionLevel;
	private int orgID;
	private String uuid;
	private String projName;
	
	
	public User() {
	  super();
  
	}



	/**
	 * @return the authenticated
	 */
	public boolean isAuthenticated() {
		return authenticated;
	}



	/**
	 * @param authenticated the authenticated to set
	 */
	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}



	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}



	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}



	/**
	 * @return the projectID
	 */
	public int getProjectID() {
		return projectID;
	}



	/**
	 * @param projectID the projectID to set
	 */
	public void setProjectID(int projectID) {
		this.projectID = projectID;
	}



	/**
	 * @return the permissionLevel
	 */
	public int getPermissionLevel() {
		return permissionLevel;
	}



	/**
	 * @param permissionLevel the permissionLevel to set
	 */
	public void setPermissionLevel(int permissionLevel) {
		this.permissionLevel = permissionLevel;
	}



	/**
	 * @return the orgID
	 */
	public int getOrgID() {
		return orgID;
	}



	/**
	 * @param orgID the orgID to set
	 */
	public void setOrgID(int orgID) {
		this.orgID = orgID;
	}



	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}



	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}



	/**
	 * @return the projName
	 */
	public String getProjName() {
		return projName;
	}



	/**
	 * @param projName the projName to set
	 */
	public void setProjName(String projName) {
		this.projName = projName;
	}

}