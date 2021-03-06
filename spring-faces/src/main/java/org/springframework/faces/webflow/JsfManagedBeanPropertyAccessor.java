/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.webflow;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.util.Assert;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * <p>
 * Spring EL PropertyAccessor that checks request, session, and application scopes for existing JSF-managed beans. This
 * allows traditional JSF-managed beans (defined in faces-config.xml) to be resolved through expressions in a flow
 * definition.
 * </p>
 * <p>
 * The preferred approach is to instead use Spring to configure such beans, but this is meant to ease migration for
 * users with existing JSF artifacts. This resolver will delegate to a temporary FacesContext so that JSF managed bean
 * initialization will be triggered if the bean has not already been initialized by JSF.
 * </p>
 * <p>
 * Source code adapted from {@link JsfManagedBeanResolver}.
 * </p>
 * 
 * @author Jeremy Grelle
 * @author Rossen Stoyanchev
 * 
 * @since 2.1
 */
public class JsfManagedBeanPropertyAccessor implements PropertyAccessor {

	public Class<?>[] getSpecificTargetClasses() {
		return null;
	}

	public boolean canRead(EvaluationContext context, Object target, String name) throws AccessException {
		return (getJsfManagedBean(name) != null);
	}

	public TypedValue read(EvaluationContext context, Object target, String name) throws AccessException {
		return new TypedValue(getJsfManagedBean(name));
	}

	public boolean canWrite(EvaluationContext context, Object target, String name) throws AccessException {
		return (getScopeForBean(name) != null);
	}

	public void write(EvaluationContext context, Object target, String name, Object newValue) throws AccessException {
		MutableAttributeMap map = getScopeForBean(name);
		if (map != null) {
			map.put(name, newValue);
		}
	}

	/**
	 * Locates a JSF managed bean through a temporary FacesContext. This method is only meant to be called from the Flow
	 * Execution. It assumes the FacesContext will not be available and creates a temporary one on the fly.
	 * 
	 * @param name The name of the bean to resolve.
	 * @return The JSF Managed Bean instance if found.
	 */
	private Object getJsfManagedBean(String name) {
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		Assert.notNull(requestContext, "RequestContext cannot be null. "
				+ "This PropertyAccessor is only intended to be invoked from an active Flow Execution.");
		FacesContext facesContext = FlowFacesContext.newInstance(requestContext, FlowLifecycle.newInstance());
		try {
			ExpressionFactory factory = facesContext.getApplication().getExpressionFactory();
			ELContext elContext = facesContext.getELContext();
			ValueExpression expression = factory.createValueExpression(elContext, "#{" + name + "}", Object.class);
			return expression.getValue(facesContext.getELContext());
		} finally {
			facesContext.release();
		}
	}

	private MutableAttributeMap getScopeForBean(String name) {
		ExternalContext externalContext = RequestContextHolder.getRequestContext().getExternalContext();
		if (externalContext.getRequestMap().contains(name)) {
			return externalContext.getRequestMap();
		} else if (externalContext.getSessionMap().contains(name)) {
			return externalContext.getSessionMap();
		} else if (externalContext.getApplicationMap().contains(name)) {
			return externalContext.getApplicationMap();
		}
		return null;
	}
}
