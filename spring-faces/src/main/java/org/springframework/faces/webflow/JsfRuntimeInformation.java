/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/

package org.springframework.faces.webflow;

import javax.faces.context.FacesContext;

import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.webflow.execution.RequestContext;

/**
 * Helper class to provide information about the JSF runtime environment such as JSF version and implementation.
 * 
 * @author Rossen Stoyanchev
 * @author Phillip Webb
 */
public class JsfRuntimeInformation {

	/**
	 * JSF Version 1.1
	 * 
	 * @deprecated As of Web Flow 2.4.0 JSF 2.0 is a minimum requirement
	 */
	@Deprecated
	public static final int JSF_11 = 0;

	/**
	 * JSF Version 1.2
	 * 
	 * @deprecated As of Web Flow 2.4.0 JSF 2.0 is a minimum requirement
	 */
	@Deprecated
	public static final int JSF_12 = 1;

	/** JSF Version 2.0 */
	public static final int JSF_20 = 2;

	private static final int jsfVersion;

	private static final ClassLoader CLASS_LOADER = JsfUtils.class.getClassLoader();

	private static final boolean myFacesPresent = ClassUtils.isPresent("org.apache.myfaces.webapp.MyFacesServlet", CLASS_LOADER);

	private static boolean portletPresent = ClassUtils.isPresent("javax.portlet.Portlet", CLASS_LOADER);
	
	private static boolean springPortletPresent = ClassUtils.isPresent("org.springframework.web.portlet.DispatcherPortlet", CLASS_LOADER);

	static {
		if (ReflectionUtils.findMethod(FacesContext.class, "isPostback") != null) {
			jsfVersion = JSF_20;
		} else if (ReflectionUtils.findMethod(FacesContext.class, "getELContext") != null) {
			jsfVersion = JSF_12;
		} else {
			jsfVersion = JSF_11;
		}
	}

	/**
	 * @deprecated As of Web Flow 2.4.0 JSF 2.0 is a minimum requirement
	 */
	@Deprecated
	public static boolean isAtLeastJsf20() {
		return jsfVersion >= JSF_20;
	}

	/**
	 * @deprecated As of Web Flow 2.4.0 JSF 2.0 is a minimum requirement
	 */
	@Deprecated
	public static boolean isAtLeastJsf12() {
		return jsfVersion >= JSF_12;
	}

	/**
	 * @deprecated As of Web Flow 2.4.0 JSF 2.0 is a minimum requirement
	 */
	@Deprecated
	public static boolean isLessThanJsf20() {
		return jsfVersion < JSF_20;
	}

	public static boolean isMyFacesPresent() {
		return myFacesPresent;
	}

	/**
	 * Determines if the container has support for portlets and if Spring MVC portlet support is available
	 * 
	 * @return <tt>true</tt> if a portlet environment is detected
	 */
	public static boolean isSpringPortletPresent() {
		return portletPresent && springPortletPresent;
	}

	/**
	 * Determine if the specified {@link FacesContext} is from a portlet request.
	 * 
	 * @param context the faces context
	 * @return <tt>true</tt> if the request is from a portlet
	 */
	public static boolean isPortletRequest(FacesContext context) {
		Assert.notNull(context, "Context must not be null");
		return isPortletContext(context.getExternalContext().getContext());
	}

	/**
	 * Determine if the specified {@link RequestContext} is from a portlet request.
	 * 
	 * @param context the request context
	 * @return <tt>true</tt> if the request is from a portlet
	 */
	public static boolean isPortletRequest(RequestContext context) {
		Assert.notNull(context, "Context must not be null");
		return isPortletContext(context.getExternalContext().getNativeContext());
	}

	/**
	 * Determine if the specified context object is from portlet.
	 * 
	 * @param nativeContext the native context
	 * @return <tt>true</tt> if the context is from a portlet
	 */
	public static boolean isPortletContext(Object nativeContext) {
		Assert.notNull(nativeContext, "Context must not be null");
		return ClassUtils.getMethodIfAvailable(nativeContext.getClass(), "getPortletContextName") != null;
	}
}
