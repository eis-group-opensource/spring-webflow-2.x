/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.webflow;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.lifecycle.Lifecycle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.View;

/**
 * JSF-specific {@link View} implementation.
 * 
 * @author Jeremy Grelle
 * @author Phillip Webb
 */
public class JsfView implements View {

	private static final Log logger = LogFactory.getLog(JsfView.class);

	public static final String EVENT_KEY = "org.springframework.webflow.FacesEvent";

	private UIViewRoot viewRoot;

	private final Lifecycle facesLifecycle;

	private final RequestContext requestContext;

	private String viewId;
	
	/**
	 * Creates a new JSF view.
	 * @param viewRoot the view root
	 * @param facesLifecycle the flow faces lifecycle
	 * @param context the current flow request
	 */
	public JsfView(UIViewRoot viewRoot, Lifecycle facesLifecycle, RequestContext context) {
		this.viewRoot = viewRoot;
		this.viewId = viewRoot.getViewId();
		this.facesLifecycle = facesLifecycle;
		this.requestContext = context;
	}

	/**
	 * Returns the underlying view root.
	 * @return the view root
	 */
	public UIViewRoot getViewRoot() {
		return this.viewRoot;
	}

	public void setViewRoot(UIViewRoot viewRoot) {
		this.viewRoot = viewRoot;
	}

	/**
	 * Performs the standard duties of the JSF RENDER_RESPONSE phase.
	 */
	public void render() throws IOException {
		FacesContext facesContext = FlowFacesContext.getCurrentInstance();
		if (facesContext.getResponseComplete()) {
			return;
		}
		facesContext.setViewRoot(this.viewRoot);
		try {
			logger.debug("Asking faces lifecycle to render");
			this.facesLifecycle.render(facesContext);

			// Ensure serialized view state is always updated even if JSF didn't call StateManager.writeState().
			if (this.requestContext.getExternalContext().isAjaxRequest()) {
				saveState();
			}
		} finally {
			logger.debug("View rendering complete");
			facesContext.responseComplete();
		}
	}

	public boolean userEventQueued() {
		return this.requestContext.getRequestParameters().contains("javax.faces.ViewState");
	}

	/**
	 * Executes postback-processing portions of the standard JSF lifecycle including APPLY_REQUEST_VALUES through
	 * INVOKE_APPLICATION.
	 */
	public void processUserEvent() {
		FacesContext facesContext = FlowFacesContext.getCurrentInstance();
		facesContext.setViewRoot(this.viewRoot);
		// Must respect these flags in case user set them during RESTORE_VIEW phase
		if (!facesContext.getRenderResponse() && !facesContext.getResponseComplete()) {
			this.facesLifecycle.execute(facesContext);
		}
	}

	/**
	 * Updates the component state stored in View scope so that it remains in sync with the updated flow execution
	 * snapshot
	 */
	public void saveState() {
		FacesContext facesContext = FlowFacesContext.getCurrentInstance();
		facesContext.setViewRoot(this.viewRoot);
		facesContext.getApplication().getStateManager().saveView(facesContext);
	}

	public Serializable getUserEventState() {
		// Set the temporary UIViewRoot state so that it will be available across the redirect (see comments in render()
		// method)
		return new ViewRootHolder(getViewRoot());
	}

	public boolean hasFlowEvent() {
		return this.requestContext.getExternalContext().getRequestMap().contains(EVENT_KEY);
	}

	public Event getFlowEvent() {
		return new Event(this, getEventId());
	}

	private String getEventId() {
		return (String) this.requestContext.getExternalContext().getRequestMap().get(EVENT_KEY);
	}

	public String toString() {
		return "[JSFView = '" + this.viewId + "']";
	}
}
