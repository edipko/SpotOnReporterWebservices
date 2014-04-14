package com.sor.beans;

public class EventReport {

	private String name;
	private String severity;
	private String type;
	private String description;
	private String filename;
	private Long lat;
	private Long lon;
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the severity
	 */
	public String getSeverity() {
		return severity;
	}
	/**
	 * @param severity the severity to set
	 */
	public void setSeverity(String severity) {
		this.severity = severity;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}
	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
	/**
	 * @return the lat
	 */
	public Long getLat() {
		return lat;
	}
	/**
	 * @param lat the lat to set
	 */
	public void setLat(Long lat) {
		this.lat = lat;
	}
	/**
	 * @return the lon
	 */
	public Long getLon() {
		return lon;
	}
	/**
	 * @param lon the lon to set
	 */
	public void setLon(Long lon) {
		this.lon = lon;
	}
	
	
}
