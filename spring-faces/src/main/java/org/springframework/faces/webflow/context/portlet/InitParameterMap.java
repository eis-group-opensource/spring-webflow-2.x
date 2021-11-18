/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.webflow.context.portlet;

import java.util.Iterator;

import javax.portlet.PortletContext;

import org.springframework.binding.collection.StringKeyedMapAdapter;
import org.springframework.webflow.core.collection.CollectionUtils;

/**
 * Map backed by a PortletContext for accessing Portlet initialization parameters.
 * 
 * @author Rossen Stoyanchev
 * @since 2.2.0
 */
public class InitParameterMap extends StringKeyedMapAdapter {

	final private PortletContext portletContext;

	public InitParameterMap(PortletContext portletContext) {
		this.portletContext = portletContext;
	}

	@Override
	protected String getAttribute(String key) {
		return portletContext.getInitParameter(key);
	}

	@Override
	protected void setAttribute(String key, Object value) {
		throw new UnsupportedOperationException("Cannot set PortletContext InitParameter");
	}

	@Override
	protected void removeAttribute(String key) {
		throw new UnsupportedOperationException("Cannot remove PortletContext InitParameter");
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Iterator<String> getAttributeNames() {
		return CollectionUtils.toIterator(portletContext.getInitParameterNames());
	}

}
