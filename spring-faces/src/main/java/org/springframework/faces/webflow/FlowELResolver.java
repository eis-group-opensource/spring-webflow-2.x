/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.webflow;

import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELResolver;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.binding.expression.el.MapAdaptableELResolver;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.expression.el.FlowResourceELResolver;
import org.springframework.webflow.expression.el.ImplicitFlowVariableELResolver;
import org.springframework.webflow.expression.el.RequestContextELResolver;
import org.springframework.webflow.expression.el.SpringBeanWebFlowELResolver;
import org.springframework.webflow.expression.el.ScopeSearchingELResolver;

/**
 * Custom {@link ELResolver} for resolving web flow specific expressions.
 * 
 * @author Jeremy Grelle
 * @author Phillip Webb
 * 
 * @since 2.4
 */
public class FlowELResolver extends CompositeELResolver {

	public FlowELResolver() {
		add(new RequestContextELResolver());
		add(new ImplicitFlowVariableELResolver());
		add(new FlowResourceELResolver());
		add(new ScopeSearchingELResolver());
		add(new MapAdaptableELResolver());
		// Spring 5.x forward compatibility
		add(new SpringBeanWebFlowELResolver());
	}

}
