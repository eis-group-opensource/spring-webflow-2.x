/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/

package org.springframework.faces.webflow;

import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.springframework.faces.webflow.context.portlet.PortletFacesContextImpl;

/**
 * Provides helper methods for getting a FacesContext that is suitable for use outside of Web Flow. Inside a running
 * Flow session {@link FlowFacesContext} is typically used instead.
 *
 * @author Rossen Stoyanchev
 * @author Phillip Webb
 *
 * @since 2.2.0
 */
public class FacesContextHelper {

	private boolean release = false;

	/**
	 * Returns a faces context that can be used outside of Web Flow. The context must be {@link #releaseIfNecessary()
	 * released} after use.
	 *
	 * @param context the native context
	 * @param request the native request
	 * @param response the native response
	 * @return a {@link FacesContext} instance.
	 * @see #release
	 */
	public FacesContext getFacesContext(Object context, Object request, Object response) {
		FacesContext facesContext = null;
		if (FacesContext.getCurrentInstance() != null) {
			facesContext = FacesContext.getCurrentInstance();
		} else {
			facesContext = newDefaultInstance(context, request, response, FlowLifecycle.newInstance());
			this.release = true;
		}
		return facesContext;
	}

	/**
	 * Release any previously {@link #getFacesContext obtained} {@link FacesContext} if necessary.
	 *
	 * @see #getFacesContext(Object, Object, Object)
	 */
	public void releaseIfNecessary() {
		if (release) {
			FacesContext.getCurrentInstance().release();
		}
	}

	/**
	 * Factory method that can be used to create a new default {@link FacesContext} instance for the running
	 * (Portlet/Servlet) environment.
	 *
	 * @param context the native context
	 * @param request the native request
	 * @param response the native response
	 * @param lifecycle the JSF lifecycle
	 * @return a new {@link FacesContext} instance
	 */
	public static FacesContext newDefaultInstance(Object context, Object request, Object response, Lifecycle lifecycle) {
		if (JsfRuntimeInformation.isPortletContext(context)) {
			return new PortletFacesContextImpl((PortletContext) context, (PortletRequest) request, (PortletResponse) response);
		}
		FacesContextFactory facesContextFactory = JsfUtils.findFactory(FacesContextFactory.class);
		return facesContextFactory.getFacesContext(context, request, response, lifecycle);
	}

}
