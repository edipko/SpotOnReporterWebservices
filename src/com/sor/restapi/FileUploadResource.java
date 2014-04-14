package com.sor.restapi;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import com.sor.beans.FileUploadResponse;


public interface FileUploadResource {
	    @Get
	    public String testConnection();
	    
	    @Post
	    public FileUploadResponse upload(Representation entity);
	        
	    
}