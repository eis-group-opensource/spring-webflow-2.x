/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.security;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;

import org.springframework.security.core.context.SecurityContextHolder;

/**
 * A standard Facelets {@link TagHandler} for performing Spring Security authorization decisions. The tag supports the
 * following combinations attributes for authorization:
 * <ul>
 * <li>access</li>
 * <li>url, method</li>
 * <li>ifAllGranted, ifAnyGranted, ifNotGranted</li>
 * </ul>
 * The var attribute can be used to store the result of the authorization decision for later use in the view.
 *
 * @author Rossen Stoyanchev
 * @since 2.2.0
 * @see FaceletsAuthorizeTag
 */
public class FaceletsAuthorizeTagHandler extends TagHandler {

	private final TagAttribute access;
	private final TagAttribute url;
	private final TagAttribute method;
	private final TagAttribute ifAllGranted;
	private final TagAttribute ifAnyGranted;
	private final TagAttribute ifNotGranted;
	private final TagAttribute var;

	/**
	 * @see TagHandler#TagHandler(TagConfig)
	 */
	public FaceletsAuthorizeTagHandler(TagConfig config) {
		super(config);
		this.access = this.getAttribute("access");
		this.url = this.getAttribute("url");
		this.method = this.getAttribute("method");
		this.ifAllGranted = this.getAttribute("ifAllGranted");
		this.ifAnyGranted = this.getAttribute("ifAnyGranted");
		this.ifNotGranted = this.getAttribute("ifNotGranted");
		this.var = this.getAttribute("var");
	}

	/**
	 * @see TagHandler#apply(FaceletContext, UIComponent)
	 */
	public void apply(FaceletContext faceletContext, UIComponent parent) throws IOException {
		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			return;
		}

		FaceletsAuthorizeTag authorizeTag = new FaceletsAuthorizeTag(faceletContext, this.access, this.url, this.method, this.ifAllGranted,
				this.ifAnyGranted, this.ifNotGranted);

		boolean isAuthorized = authorizeTag.authorize();

		if (isAuthorized) {
			this.nextHandler.apply(faceletContext, parent);
		}

		if (this.var != null) {
			faceletContext.setAttribute(this.var.getValue(faceletContext), Boolean.valueOf(isAuthorized));
		}
	}

}
