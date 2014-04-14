package com.sor.restapi;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import com.sor.beans.User;

public interface UserResource {

	@Get
	public String showGet();

	@Post
	public User checkUser(Representation entity);

}
