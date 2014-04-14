package com.sor.restapi;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import com.sor.beans.EventReportResponse;


public interface EventReportResource {
	    @Get
	    public String testConnection();
	    
	    @Post
	    public EventReportResponse addEvent(Representation entity);
	        
	    
}
