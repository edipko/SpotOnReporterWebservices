package com.sor.restapi;

import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.resource.ServerResource;

import com.sor.beans.User;
import com.sor.utils.Hash;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * The server side implementation of the Restlet resource.
 */
public class UserServerResource extends ServerResource implements UserResource {

	private Connection connect = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	@Override
	public User checkUser(Representation entity) {
		
		User user = new User();

		/*
		 * Get the parameters that were posted
		 */
		Form form = new Form(entity);
		String username = form.getFirstValue("username");
		String givenPassword = form.getFirstValue("password");

		System.out.println("Got Username/Password: " + username + "/" + givenPassword);
		
		String actualPassword = null;
		int projectID = 0;
		int orgID = 0;
		int permissionLevel = 0;
		String uuid = null;
		String projName = null;

		/*
		 * Connect to the database and pull the information for the give user
		 */
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();

			connect = DriverManager.getConnection("jdbc:mysql://localhost/sor?"
					+ "user=spotonDetail&password=spot2012!");

			/*
			 * Query to return all information the application needs
			 */
			String query = "SELECT PASSWORD, UUID, PROJECTID, ORGID, PERMISSION_LEVEL, users.ID, projects.NAME "
					+ "FROM users,profile,projects "
					+ "WHERE USERNAME = ? "
					+ "AND profile.USERID=users.ID "
					+ "AND projects.ID=profile.PROJECTID " + "LIMIT 1;";

			/*
			 * The query uses bind variables
			 */
			preparedStatement = connect.prepareStatement(query);
			preparedStatement.setString(1, username);

			/*
			 * Get the result set - just going to catch the exception if the
			 * user does not exist.
			 */
			resultSet = preparedStatement.executeQuery();
			resultSet.next();

			/*
			 * Setup the User object with the data from the database
			 */
			projectID = resultSet.getInt("PROJECTID");
			orgID = resultSet.getInt("ORGID");
			permissionLevel = resultSet.getInt("PERMISSION_LEVEL");
			uuid = resultSet.getString("UUID");
			projName = resultSet.getString("NAME");
			actualPassword = resultSet.getString("PASSWORD");

		} catch (Exception e) {
			/*
			 * There was a failure - most likely the username did not exist in
			 * the database. Return not authenticated
			 */
			user.setAuthenticated(false);
			user.setUsername("EXCEPTION1");
			user.setOrgID(0);
			user.setPermissionLevel(0);
			user.setProjectID(0);
			user.setProjName(null);
			user.setUuid(null);

			System.err.println("Error");
			return user;

		}

		/*
		 * If here - the user existed in the database Need to decrypt the
		 * password and see if we have a match Do what we did to the password
		 * initially - The salt is the first 64 characters - Loop through sha256
		 * hash 10000 times
		 */
		Hash myHash = new Hash();
		String salt = actualPassword.substring(0, 64);
		String hash = salt + givenPassword;

		// Hash the password as we did before
		for (int i = 0; i < 100000; i++) {
			try {
				hash = myHash.hash(hash);
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/*
		 * Put everything back together, if the passwords match the user is
		 * authenticated
		 */
		String hashedPassword = salt + hash;
		if (hashedPassword.equals(actualPassword)) {
			user.setAuthenticated(true);
			user.setUsername(username);
			user.setOrgID(orgID);
			user.setPermissionLevel(permissionLevel);
			user.setProjectID(projectID);
			user.setProjName(projName);
			user.setUuid(uuid);
		} else {
			user.setAuthenticated(false);
			System.out.println("Bad password given");
		}

		System.out.println("Authenticated? " + user.isAuthenticated());
		
		return user;
	}

	@Override
	public String showGet() {
		System.out.println("GET");
		return "Hello there";
	}

}