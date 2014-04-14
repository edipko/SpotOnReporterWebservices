package com.sor.restapi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;

import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ServerResource;

import com.sor.beans.EventReportResponse;

public class EventServer extends ServerResource implements EventReportResource {

	private Connection connect = null;
	private PreparedStatement preparedStatement = null;
	private final String SERVER_URL = "https://dev.spotonresponse.com/UPLOADS/";

	@Override
	public String testConnection() {
		return "Alive";
	}

	@Override
	public EventReportResponse addEvent(Representation entity) {
		/*
		 * Get the current date/time in ISO 8601 format
		 */
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
		df.setTimeZone(tz);
		String nowAsISO = df.format(new Date());

		/*
		 * Will use this Bean to return data
		 */
		EventReportResponse event_rr = new EventReportResponse();

		/*
		 * Get the parameters that were posted
		 */
		Form form = new Form(entity);
		String name = form.getFirstValue("name");
		String severity = form.getFirstValue("severity");
		String type = form.getFirstValue("type");
		String disposition = form.getFirstValue("disposition");
		String description = form.getFirstValue("description");

		NumberFormat _format = NumberFormat.getInstance(Locale.US);
		Number number = null;
		try {
			number = _format.parse(form.getFirstValue("lat"));
		} catch (Exception ex) {
			number = 0;
		}
		Double lat = Double.parseDouble(number.toString());

		try {
			number = _format.parse(form.getFirstValue("lon"));
		} catch (Exception ex) {
			number = 0;
		}
		Double lon = Double.parseDouble(number.toString());

		try {
			number = _format.parse(form.getFirstValue("projectID"));
		} catch (Exception ex) {
			number = 0;
		}
		int projID = Integer.parseInt(number.toString());

		String username = form.getFirstValue("username");
		String uuid = form.getFirstValue("uuid");

		try {
			number = _format.parse(form.getFirstValue("orgID"));
		} catch (Exception ex) {
			number = 0;
		}
		int orgID = Integer.parseInt(number.toString());

		// String fileName = form.getFirstValue("fileName");
		ArrayList<String> fpArray = new ArrayList<String>();
		
		try {
			
			
			String jsonFilePaths = form.getFirstValue("fileNames");
			JSONObject jsonObject = new JSONObject(jsonFilePaths);
			JSONArray files = (JSONArray) jsonObject.get("files");

			if (files != null) { 
			   int len = files.length();
			   for (int i=0;i<len;i++){ 
			    	fpArray.add(files.get(i).toString());	    
			   } 
			}
			} catch (Exception ex) {
				
			}
		
		
		
		

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();

			connect = DriverManager.getConnection("jdbc:mysql://localhost/sor?"
					+ "user=spotonDetail&password=spot2012!");

			/*
			 * Insert into the geofence table
			 */
			String query = "INSERT INTO geofence(active,circle,radius,lat,lon,points) VALUES(?,?,?,?,?,?)";

			/*
			 * The query uses bind variables
			 */
			preparedStatement = connect.prepareStatement(query);
			preparedStatement.setInt(1, 1); // active
			preparedStatement.setInt(2, 1); // circle
			preparedStatement.setInt(3, 1); // radius
			preparedStatement.setDouble(4, lat); // lat
			preparedStatement.setDouble(5, lon); // lon
			preparedStatement.setString(6, null); // points

			preparedStatement.execute();

			/*
			 * Get the Last_insert_id to use in the incident table
			 */
			int geofenceID = 0;
			PreparedStatement getLastInsertId = connect
					.prepareStatement("SELECT LAST_INSERT_ID()");
			ResultSet rs = getLastInsertId.executeQuery();
			if (rs.next()) {
				geofenceID = rs.getInt("last_insert_id()");
			}

			System.out.println("Inserting into incidents");
			/*
			 * Now do the insert into the incident table
			 */
			query = "INSERT INTO incidents(projectID,Name,Snippet,Code,Created,CreatedBy,"
					+ "Descriptor,Reason,Disposition,LastUpdated,LastUpdatedBy,State,Type,Ver,geofenceID) "
					+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

			preparedStatement = connect.prepareStatement(query);
			preparedStatement.setInt(1, projID); // projectID
			preparedStatement.setString(2, name); // Name
			preparedStatement.setString(3, " "); // Snippet
			preparedStatement.setString(4, type); // Code
			preparedStatement.setString(5, nowAsISO); // Created
			preparedStatement.setString(6, uuid); // CreateBy
			preparedStatement.setString(7, description); // Description
			preparedStatement.setString(8, severity); // Reason
			preparedStatement.setString(9, disposition); // Disposition
			preparedStatement.setString(10, nowAsISO); // LastUpdated
			preparedStatement.setString(11, uuid); // LastUpdatedBy
			preparedStatement.setString(12, "Active"); // State
			preparedStatement.setString(13, type); // Type
			preparedStatement.setInt(14, 1); // Version
			preparedStatement.setInt(15, geofenceID); // geofenceID

			preparedStatement.execute();

			/*
			 * Get the Last_insert_id to use in the incident table
			 */
			int incidentID = 0;
			getLastInsertId = connect
					.prepareStatement("SELECT LAST_INSERT_ID()");
			rs = getLastInsertId.executeQuery();
			if (rs.next()) {
				incidentID = rs.getInt("last_insert_id()");
			}

			System.out.println("Inserting into uicdsIncidentMgmt");
			/*
			 * insert into the UICDS table
			 */
			query = "INSERT INTO uicdsIncidentMgmt(incidentID, projectID,action,uuid,orgID,type,title,"
					+ "description,status,reason,address,lat,lon,"
					+ "radius,deviceLat,deviceLon,updateTimestamp) "
					+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
			preparedStatement = connect.prepareStatement(query);
			preparedStatement.setInt(1, incidentID); // incidentID
			preparedStatement.setInt(2, projID); // projectID
			preparedStatement.setString(3, "ADD"); // action
			preparedStatement.setString(4, uuid); // uuid
			preparedStatement.setInt(5, orgID); // orgID
			preparedStatement.setString(6, type); // type
			preparedStatement.setString(7, name); // title
			preparedStatement.setString(8, description); // description
			preparedStatement.setString(9, "Active"); // status
			preparedStatement.setString(10, ""); // reason
			preparedStatement.setString(11, ""); // address
			preparedStatement.setDouble(12, lat); // lat
			preparedStatement.setDouble(13, lon); // lon
			preparedStatement.setInt(14, 1); // radius
			preparedStatement.setDouble(15, lat); // deviceLat
			preparedStatement.setDouble(16, lon); // deviceLon
			preparedStatement.setString(17, nowAsISO); // updateTimestamp

			preparedStatement.execute();

			System.out.println("Done");
			/*
			 * Determine if a file was uploaded. If so, create an SOI for the
			 * event and attach the file
			 */
			//if (fileName.length() > 4) {
           if (fpArray.size() > 0) {
				System.out.println("Inserting into incidentUpload");

			 for (String f : fpArray) {
				query = "INSERT into incidentUpload(UUID, subject, data, incidentID, lat, lon, mediaFile, status, need, category) "
						+ "VALUES(?,?,?,?,?,?,?,?,?,?);";
				preparedStatement = connect.prepareStatement(query);
				preparedStatement.setString(1, uuid);
				preparedStatement.setString(2, name);
				preparedStatement.setString(3, "MOBILE FILE UPLOAD");
				preparedStatement.setInt(4, incidentID);
				preparedStatement.setDouble(5, lat);
				preparedStatement.setDouble(6, lon);
				preparedStatement.setString(7, f);
				preparedStatement.setString(8, " ");
				preparedStatement.setString(9, " ");
				preparedStatement.setString(10, " ");

				preparedStatement.execute();
				System.out.println("Done");

				getLastInsertId = connect
						.prepareStatement("SELECT LAST_INSERT_ID()");
				rs = getLastInsertId.executeQuery();
				if (rs.next()) {
					int uploadID = rs.getInt("last_insert_id()");

					String uploadURL = SERVER_URL + f;

					System.out.println("Inserting into uicdsSOIMgmt");
					query = "INSERT INTO uicdsSOIMgmt(id, projectID, action, UUID, fileName, SOI) "
							+ "VALUES(?,?,?,?,?,?);";
					preparedStatement = connect.prepareStatement(query);
					preparedStatement.setInt(1, uploadID);
					preparedStatement.setInt(2, projID);
					preparedStatement.setString(3, "ADD");
					preparedStatement.setString(4, uuid);
					preparedStatement.setString(5, uploadURL);
					preparedStatement.setString(6, " ");

					preparedStatement.execute();
					System.out.println("Done");
				}
			 }
			}

			System.out.println("Sending Goor Response");
			// getResponse().setStatus(Status.SUCCESS_ACCEPTED);
			getResponse().setStatus(Status.SUCCESS_OK);
			event_rr.setStatus("Good");

		} catch (Exception ex) {
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
			event_rr.setStatus("Bad: " + ex.getMessage());
			System.out.println("Exception occurred: " + ex.getMessage());
			ex.printStackTrace();
			return event_rr;
		}

		JsonRepresentation jr = new JsonRepresentation(event_rr);
		String jsonResponse = null;
		try {
			jsonResponse = jr.getText();
		} catch (Exception ex) {
			jsonResponse = "JSON ERROR";
		}
		System.out.println("Sending response: \n" + jsonResponse);

		return event_rr;
	}

}
