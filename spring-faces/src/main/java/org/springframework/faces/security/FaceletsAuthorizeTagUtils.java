/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.security;

import java.io.IOException;

/**
 * This class provides static methods that are registered as EL functions and available for use in Unified EL
 * expressions in standard Facelets views.
 * 
 * @author Rossen Stoyanchev
 * @since 2.2.0
 */
public abstract class FaceletsAuthorizeTagUtils {

	/**
	 * Returns true if the user has all of of the given authorities.
	 * 
	 * @param authorities a comma-separated list of user authorities.
	 */
	public static boolean areAllGranted(String authorities) throws IOException {
		FaceletsAuthorizeTag authorizeTag = new FaceletsAuthorizeTag();
		authorizeTag.setIfAllGranted(authorities);
		return authorizeTag.authorizeUsingGrantedAuthorities();
	}

	/**
	 * Returns true if the user has any of the given authorities.
	 * 
	 * @param authorities a comma-separated list of user authorities.
	 */
	public static boolean areAnyGranted(String authorities) throws IOException {
		FaceletsAuthorizeTag authorizeTag = new FaceletsAuthorizeTag();
		authorizeTag.setIfAnyGranted(authorities);
		return authorizeTag.authorizeUsingGrantedAuthorities();
	}

	/**
	 * Returns true if the user does not have any of the given authorities.
	 * 
	 * @param authorities a comma-separated list of user authorities.
	 */
	public static boolean areNotGranted(String authorities) throws IOException {
		FaceletsAuthorizeTag authorizeTag = new FaceletsAuthorizeTag();
		authorizeTag.setIfNotGranted(authorities);
		return authorizeTag.authorizeUsingGrantedAuthorities();
	}

	/**
	 * Returns true if the user is allowed to access the given URL and HTTP method combination. The HTTP method is
	 * optional and case insensitive.
	 */
	public static boolean isAllowed(String url, String method) throws IOException {
		FaceletsAuthorizeTag authorizeTag = new FaceletsAuthorizeTag();
		authorizeTag.setUrl(url);
		authorizeTag.setMethod(method);
		return authorizeTag.authorizeUsingUrlCheck();
	}

}
