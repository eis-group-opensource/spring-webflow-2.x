/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.webflow;

import java.io.IOException;

import javax.faces.application.ResourceHandler;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.support.WebApplicationObjectSupport;

/**
 * Handles a request by delegating to the JSF ResourceHandler, which serves web application and classpath resources such
 * as images, CSS and JavaScript files from well-known locations.
 * 
 * @since 2.2.0
 * @author Rossen Stoyanchev
 * @see ResourceHandler
 */
public class JsfResourceRequestHandler extends WebApplicationObjectSupport implements HttpRequestHandler {

	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		FacesContextHelper helper = new FacesContextHelper();
		try {
			FacesContext facesContext = helper.getFacesContext(getServletContext(), request, response);
			ResourceHandler resourceHandler = facesContext.getApplication().getResourceHandler();
			resourceHandler.handleResourceRequest(facesContext);
		} finally {
			helper.releaseIfNecessary();
		}
	}

}
