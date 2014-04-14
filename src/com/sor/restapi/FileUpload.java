package com.sor.restapi;

import java.io.File;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ServerResource;

import com.sor.beans.FileUploadResponse;

public class FileUpload extends ServerResource implements
		FileUploadResource {
	
	private  String RETURN_FILENAME = null;
	private final String UPLOAD_PATH = "/usr/local/spotonresponse/UPLOADS";
	
	@Override
	public String testConnection() {
		return "Alive";
	}

	@Override
	public FileUploadResponse upload(Representation entity) {
		
		
	    FileUploadResponse fur = new FileUploadResponse();
	    
		/*
		 * Handle fileUpload if present
		 */
		try {
			if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(),
					true)) {

				// The Apache FileUpload project parses HTTP requests which
				// conform to RFC 1867, "Form-based File Upload in HTML". That
				// is, if an HTTP request is submitted using the POST method,
				// and with a content type of "multipart/form-data", then
				// FileUpload can parse that request, and get all uploaded files
				// as FileItem.

				// 1/ Create a factory for disk-based file items
				DiskFileItemFactory factory = new DiskFileItemFactory();
				factory.setSizeThreshold(1000240);
				
				// 2/ Create a new file upload handler based on the Restlet
				// FileUpload extension that will parse Restlet requests and
				// generates FileItems.
				RestletFileUpload upload = new RestletFileUpload(factory);
				List<FileItem> items = upload.parseRepresentation(entity);

				// Process only the uploaded item called "fileToUpload" and
				// save it on disk
				boolean found = false;		
				
				/*
				 * Get the current unixtimestamp to prepend to the filename
				 */
				String ts =  String.valueOf((int) (System.currentTimeMillis() / 1000L));
				
				/*
				 * Loop through the fileItems and write out the files
				 * NOTE - our application, there will only be one for now
				 */
				for(FileItem fi : items){ 
                    File file = new File(UPLOAD_PATH  + File.separator + ts.toString() + "-" + fi.getName()); 
                    fi.write(file); 
                    System.out.println(fi.getName()); 
                    found = file.exists();
	                RETURN_FILENAME = file.getName();
                }

				if (found) {
					/*
					 * This look good
					 */
					fur.setStatus("Good");
					fur.setFilename(RETURN_FILENAME);
					setStatus(Status.SUCCESS_OK);
					
					JsonRepresentation jr = new JsonRepresentation(fur);
					String jsonResponse = null;
					try {
						jsonResponse = jr.getText();
					} catch (Exception ex) {
						jsonResponse = "JSON ERROR";
					}
					System.out.println("Sending Good response: \n" + jsonResponse);
					
					
					return fur;
					
				} else {
					/*
					 * Some error occurred, file not upload
					 */
					fur.setStatus("Bad - file not found");
					fur.setFilename("");
					setStatus(Status.SERVER_ERROR_INTERNAL);
					
					JsonRepresentation jr = new JsonRepresentation(fur);
					String jsonResponse = null;
					try {
						jsonResponse = jr.getText();
					} catch (Exception ex) {
						jsonResponse = "JSON ERROR";
					}
					System.out.println("Sending Bad response: \n" + jsonResponse);
					
					return fur;
				}
			}
			        
		} catch (Exception ex) {
			fur.setStatus("Bad - Exception: " + ex.getMessage());
			fur.setFilename("");
			setStatus(Status.SERVER_ERROR_INTERNAL);
			
			JsonRepresentation jr = new JsonRepresentation(fur);
			String jsonResponse = null;
			try {
				jsonResponse = jr.getText();
			} catch (Exception e) {
				jsonResponse = "JSON ERROR";
			}
			System.out.println("Sending Exception response: \n" + jsonResponse);
			
			ex.printStackTrace();
			return fur;
		}
		
		return null;
	        
	}

}