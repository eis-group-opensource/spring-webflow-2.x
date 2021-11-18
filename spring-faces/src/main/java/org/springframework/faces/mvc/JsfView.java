/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.mvc;

import static org.springframework.faces.webflow.JsfRuntimeInformation.isPortletRequest;

import java.util.Map;

import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.faces.webflow.FacesContextHelper;
import org.springframework.faces.webflow.JsfUtils;
import org.springframework.util.Assert;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

/**
 * JSF View that renders a transient (stateless) JSF view template. The UIViewRoot will not be saved and thus the JSF
 * lifecycle will not be able to be invoked on postback.
 * 
 * @author Jeremy Grelle
 */
public class JsfView extends AbstractUrlBasedView {

	private Lifecycle facesLifecycle;

	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		this.facesLifecycle = createFacesLifecycle();
	}

	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		FacesContextHelper facesContextHelper = new FacesContextHelper();
		FacesContext facesContext = facesContextHelper.getFacesContext(getServletContext(), request, response);

		populateRequestMap(facesContext, model);

		JsfUtils.notifyBeforeListeners(PhaseId.RESTORE_VIEW, this.facesLifecycle, facesContext);

		ViewHandler viewHandler = facesContext.getApplication().getViewHandler();

		if (!isPortletRequest(facesContext)) {
			viewHandler.initView(facesContext);
		}

		UIViewRoot viewRoot = viewHandler.createView(facesContext, getUrl());
		Assert.notNull(viewRoot, "A JSF view could not be created for " + getUrl());
		viewRoot.setLocale(RequestContextUtils.getLocale(request));
		viewRoot.setTransient(true);

		facesContext.setViewRoot(viewRoot);

		JsfUtils.notifyAfterListeners(PhaseId.RESTORE_VIEW, this.facesLifecycle, facesContext);

		facesContext.setViewRoot(viewRoot);
		facesContext.renderResponse();
		try {
			this.logger.debug("Asking faces lifecycle to render");
			this.facesLifecycle.render(facesContext);
		} finally {
			this.logger.debug("View rendering complete");
			facesContextHelper.releaseIfNecessary();
		}
	}

	private void populateRequestMap(FacesContext facesContext, Map<String, Object> model) {
		Map<String, Object> requestMap = facesContext.getExternalContext().getRequestMap();
		for (Map.Entry<String, Object> entry : model.entrySet()) {
			// JSF does not insist that putAll is implemented, hence we use individual put calls
			requestMap.put(entry.getKey(), entry.getValue());
		}
	}

	private Lifecycle createFacesLifecycle() {
		LifecycleFactory lifecycleFactory = JsfUtils.findFactory(LifecycleFactory.class);
		return lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
	}

}
