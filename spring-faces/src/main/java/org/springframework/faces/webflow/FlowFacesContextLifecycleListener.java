/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.webflow;

import javax.faces.context.FacesContext;

import org.springframework.webflow.execution.FlowExecutionListener;
import org.springframework.webflow.execution.FlowExecutionListenerAdapter;
import org.springframework.webflow.execution.RequestContext;

/**
 * A {@link FlowExecutionListener} that creates a {@link FlowFacesContext} instance when a flow request is submitted and
 * releases it when the request has been processed.
 * 
 * @author Rossen Stoyanchev
 */
public class FlowFacesContextLifecycleListener extends FlowExecutionListenerAdapter {

	/**
	 * Creates a new instance of {@link FlowFacesContext} that is then available for the duration of the request.
	 * @param context the current flow request context
	 */
	public void requestSubmitted(RequestContext context) {
		FlowFacesContext.newInstance(context, FlowLifecycle.newInstance());
	}

	/**
	 * Releases the current {@link FlowFacesContext} instance.
	 * @param context the source of the event
	 */
	public void requestProcessed(RequestContext context) {
		FacesContext.getCurrentInstance().release();
	}
}
