/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.webflow;

import java.beans.FeatureDescriptor;
import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import org.springframework.util.Assert;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * ELResolver that checks request, session, and application scopes for existing JSF-managed beans. This allows
 * traditional JSF-managed beans (defined in faces-config.xml) to be resolved through expressions in a flow definition.
 * The preferred approach is to instead use Spring to configure such beans, but this is meant to ease migration for
 * users with existing JSF artifacts. This resolver will delegate to a temporary FacesContext so that JSF managed bean
 * initialization will be triggered if the bean has not already been initialized by JSF.
 * 
 * @author Jeremy Grelle
 * @author Phillip Webb
 */
public class JsfManagedBeanResolver extends ELResolver {

	public Class<?> getCommonPropertyType(ELContext context, Object base) {
		return Object.class;
	}

	public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
		return null;
	}

	public Class<?> getType(ELContext context, Object base, Object property) {
		if (base == null) {
			Object bean = getFacesBean(property);
			if (bean != null) {
				context.setPropertyResolved(true);
				return bean.getClass();
			}
		}
		return null;
	}

	public Object getValue(ELContext context, Object base, Object property) {
		if (base == null) {
			Object bean = getFacesBean(property);
			if (bean != null) {
				context.setPropertyResolved(true);
				return bean;
			}
		}
		return null;
	}

	public boolean isReadOnly(ELContext context, Object base, Object property) {
		if (base == null) {
			RequestContext requestContext = RequestContextHolder.getRequestContext();
			if (requestContext.getExternalContext().getRequestMap().contains(property.toString())
					|| requestContext.getExternalContext().getSessionMap().contains(property.toString())
					|| requestContext.getExternalContext().getApplicationMap().contains(property.toString())) {
				context.setPropertyResolved(true);
			}
		}
		return false;
	}

	/**
	 * Sets a bean value if a corresponding key is found in one of the ExternalContext scopes.
	 */
	public void setValue(ELContext context, Object base, Object property, Object value) {
		if (base == null) {
			RequestContext requestContext = RequestContextHolder.getRequestContext();
			if (requestContext.getExternalContext().getRequestMap().contains(property.toString())) {
				context.setPropertyResolved(true);
				requestContext.getExternalContext().getRequestMap().put(property.toString(), value);
			} else if (requestContext.getExternalContext().getSessionMap().contains(property.toString())) {
				context.setPropertyResolved(true);
				requestContext.getExternalContext().getSessionMap().put(property.toString(), value);
			} else if (requestContext.getExternalContext().getApplicationMap().contains(property.toString())) {
				context.setPropertyResolved(true);
				requestContext.getExternalContext().getApplicationMap().put(property.toString(), value);
			}
		}
	}

	/**
	 * This resolver is only meant to be called from the Flow Execution, thus it assumes that the FacesContext will not
	 * be available and creates a temporary one on the fly.
	 * @return The initialized FacesContext.
	 */
	private FacesContext getFacesContext() {
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		Assert.notNull(requestContext, "RequestContext cannot be null - This resolver is only intended to be invoked "
				+ "from an active Flow Execution.");
		FacesContext facesContext = FlowFacesContext.newInstance(requestContext, FlowLifecycle.newInstance());
		return facesContext;
	}

	/**
	 * Uses a temporary FacesContext to try and resolve a JSF Managed Bean
	 * @param beanName - The name of the bean to resolve.
	 * @return The JSF Managed Bean instance if found.
	 */
	private Object getFacesBean(Object beanName) {
		FacesContext context = getFacesContext();
		try {
			Application application = context.getApplication();
			String expression = "#{" + beanName + "}";
			return application.evaluateExpressionGet(context, expression, Object.class);
		} finally {
			context.release();
		}
	}
}
