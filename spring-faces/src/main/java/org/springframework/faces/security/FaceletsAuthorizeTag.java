/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.security;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagHandler;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * A concrete implementation of {@link AbstractAuthorizeTag} for use with standard Facelets rendering technology.
 * 
 * @author Rossen Stoyanchev
 * @since 2.2.0
 */
public class FaceletsAuthorizeTag extends AbstractAuthorizeTag {

	/**
	 * A class constructor for use in a {@link TagHandler}. Accepts all possible tag attributes as {@link TagAttribute}
	 * instances. The constructor extracts the attribute values by evaluating them as Unified EL expressions. This
	 * excludes the access attribute, which is expected to be a Spring EL expression.
	 * 
	 * @param faceletContext the current FaceletContext
	 * @param access the access attribute or null
	 * @param url the url attribute or null
	 * @param method the method attribute or null
	 * @param ifAllGranted the ifAllGranted attribute or null
	 * @param ifAnyGranted the ifAnyGranted attribute or null
	 * @param ifNotGranted the ifNotGranted attribute or null
	 */
	public FaceletsAuthorizeTag(FaceletContext faceletContext, TagAttribute access, TagAttribute url,
			TagAttribute method, TagAttribute ifAllGranted, TagAttribute ifAnyGranted, TagAttribute ifNotGranted) {
		setAccess(getAttributeValue(faceletContext, access, false));
		setUrl(getAttributeValue(faceletContext, url, true));
		setMethod(getAttributeValue(faceletContext, method, true));
		setIfAllGranted(getAttributeValue(faceletContext, ifAllGranted, true));
		setIfAnyGranted(getAttributeValue(faceletContext, ifAnyGranted, true));
		setIfNotGranted(getAttributeValue(faceletContext, ifNotGranted, true));
	}

	/**
	 * A default constructor. Callers of this constructor are responsible for setting one or more of the tag attributes
	 * in {@link AbstractAuthorizeTag}.
	 */
	public FaceletsAuthorizeTag() {
	}

	protected ServletRequest getRequest() {
		return (ServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	}

	protected ServletResponse getResponse() {
		return (ServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
	}

	protected ServletContext getServletContext() {
		return (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
	}

	/*---- Pirvate helper methods ----*/

	private String getAttributeValue(FaceletContext faceletContext, TagAttribute tagAttribute, boolean evaluate) {
		String value = null;
		if (tagAttribute != null) {
			if (evaluate) {
				ValueExpression expression = tagAttribute.getValueExpression(faceletContext, String.class);
				value = (String) expression.getValue(faceletContext.getFacesContext().getELContext());
			} else {
				value = tagAttribute.getValue();
			}
		}
		return value;
	}

}
