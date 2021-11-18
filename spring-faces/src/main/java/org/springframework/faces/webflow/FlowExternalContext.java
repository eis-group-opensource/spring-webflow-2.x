/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.webflow;

import java.io.IOException;

import javax.faces.context.ExternalContext;
import javax.faces.context.ExternalContextWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.webflow.execution.RequestContext;

/**
 * Custom {@link ExternalContext} implementation that supports custom response objects other than
 * {@link HttpServletResponse}.
 * 
 * @author Jeremy Grelle
 * @author Phillip Webb
 * @author Rossen Stoyanchev
 * 
 * @since 2.4
 */
public class FlowExternalContext extends ExternalContextWrapper {

	Log logger = LogFactory.getLog(FlowExternalContext.class);

	private static final String CUSTOM_RESPONSE = FlowExternalContext.class.getName() + ".customResponse";

	private final ExternalContext wrapped;

	private final RequestContext context;

	public FlowExternalContext(RequestContext context, ExternalContext wrapped) {
		this.context = context;
		this.wrapped = wrapped;
	}

	public ExternalContext getWrapped() {
		return this.wrapped;
	}

	public Object getResponse() {
		if (this.context.getRequestScope().contains(CUSTOM_RESPONSE)) {
			return this.context.getRequestScope().get(CUSTOM_RESPONSE);
		}
		return super.getResponse();
	}

	public void setResponse(Object response) {
		this.context.getRequestScope().put(CUSTOM_RESPONSE, response);
		super.setResponse(response);
	}

	public void responseSendError(int statusCode, String message) throws IOException {
		this.logger.debug("Sending error HTTP status code " + statusCode + " with message '" + message + "'");
		super.responseSendError(statusCode, message);
	}

}
