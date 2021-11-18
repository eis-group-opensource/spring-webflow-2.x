/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.webflow.context.portlet;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.portlet.PortletRequest;

import org.springframework.binding.collection.StringKeyedMapAdapter;
import org.springframework.webflow.core.collection.CollectionUtils;

/**
 * Map backed by a PortletContext for accessing Portlet request properties. Request properties can have multiple values.
 * The {@link RequestPropertyMap#setUseArrayForMultiValueAttributes(Boolean)} property allows choosing whether the map
 * will return:
 * <ul>
 * <li>String - selects the first element in case of multiple values</li>
 * <li>String[] - wraps single-values attributes as array</li>
 * <li>String or String[] - depends on the values of the property</li>
 * </ul>
 * 
 * @author Rossen Stoyanchev
 * @since 2.2.0
 * 
 * @see PortletRequest#getProperty(String)
 * @see PortletRequest#getProperties(String)
 */
public class RequestPropertyMap extends StringKeyedMapAdapter {

	private Boolean useArrayForMultiValueAttributes;

	private final PortletRequest portletRequest;

	public RequestPropertyMap(PortletRequest portletRequest) {
		this.portletRequest = portletRequest;
	}

	/**
	 * This property allows choosing what kind of attributes the map will return:
	 * <ol>
	 * <li>String - selects the first element in case of multiple values</li>
	 * <li>String[] - wraps single-values attributes as array</li>
	 * <li>String or String[] - depends on the values of the property</li>
	 * </ol>
	 * The above choices correspond to the following values for useArrayForMultiValueAttributes:
	 * <ol>
	 * <li>False</li>
	 * <li>True</li>
	 * <li>null</li>
	 * </ol>
	 * 
	 * @param useArrayForMultiValueAttributes
	 */
	public void setUseArrayForMultiValueAttributes(Boolean useArrayForMultiValueAttributes) {
		this.useArrayForMultiValueAttributes = useArrayForMultiValueAttributes;
	}

	public Boolean useArrayForMultiValueAttributes() {
		return useArrayForMultiValueAttributes;
	}

	@Override
	protected Object getAttribute(String key) {
		if (null == useArrayForMultiValueAttributes) {
			List<String> list = Collections.list(portletRequest.getProperties(key));
			if (1 == list.size()) {
				return list.get(0);
			} else {
				return list.toArray(new String[list.size()]);
			}
		} else {
			if (useArrayForMultiValueAttributes) {
				List<String> list = Collections.list(portletRequest.getProperties(key));
				return list.toArray(new String[list.size()]);
			} else {
				return portletRequest.getProperty(key);
			}
		}
	}

	@Override
	protected void setAttribute(String key, Object value) {
		throw new UnsupportedOperationException("Cannot set PortletRequest property");
	}

	@Override
	protected void removeAttribute(String key) {
		throw new UnsupportedOperationException("Cannot remove PortletRequest property");
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Iterator<String> getAttributeNames() {
		return CollectionUtils.toIterator(portletRequest.getPropertyNames());
	}

}
