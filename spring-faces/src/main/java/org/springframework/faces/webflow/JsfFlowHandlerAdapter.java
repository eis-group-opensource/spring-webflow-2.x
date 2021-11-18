/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.webflow;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.js.ajax.AjaxHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.webflow.mvc.servlet.FlowHandlerAdapter;

/**
 * An extension of {@link FlowHandlerAdapter} that replaces the default {@link AjaxHandler} instance with a
 * {@link JsfAjaxHandler}.
 * 
 * @author Rossen Stoyanchev
 * @since 2.2.0
 */
public class JsfFlowHandlerAdapter extends FlowHandlerAdapter {

	public void afterPropertiesSet() throws Exception {
		boolean initializeAjaxHandler = getAjaxHandler() == null;
		super.afterPropertiesSet();
		if (initializeAjaxHandler) {
			JsfAjaxHandler ajaxHandler = new JsfAjaxHandler();
			ajaxHandler.setApplicationContext(getApplicationContext());
			setAjaxHandler(ajaxHandler);
		}
	}

	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		return super.handle(request, response, handler);
	}
}
