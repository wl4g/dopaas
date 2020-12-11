package com.wl4g.devops.doc.plugin.swagger.example.jaxrs2;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.fileupload.FileUploadException;

@Path("/demo/jaxrs2")
public class Jaxrs2SampleController {

	@GET
	@Path("/getUserInfo")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public List<String> getUserInfo(@Context HttpServletRequest request) throws FileUploadException {
		return Arrays.asList();
	}

}
