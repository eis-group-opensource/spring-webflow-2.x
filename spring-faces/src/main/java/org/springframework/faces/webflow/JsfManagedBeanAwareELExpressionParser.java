/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.webflow;

import java.util.ArrayList;
import java.util.List;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;

import org.springframework.binding.expression.el.DefaultELResolver;
import org.springframework.binding.expression.el.ELContextFactory;
import org.springframework.binding.expression.el.ELExpressionParser;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.expression.el.ActionMethodELResolver;
import org.springframework.webflow.expression.el.FlowResourceELResolver;
import org.springframework.webflow.expression.el.ImplicitFlowVariableELResolver;
import org.springframework.webflow.expression.el.RequestContextELResolver;
import org.springframework.webflow.expression.el.ScopeSearchingELResolver;
import org.springframework.webflow.expression.el.SpringBeanWebFlowELResolver;

/**
 * A JSF-specific ExpressionParser that allows beans managed by either JSF, Spring, or Web Flow to be referenced in
 * expressions in the FlowDefinition.
 * 
 * @author Jeremy Grelle
 */
public class JsfManagedBeanAwareELExpressionParser extends ELExpressionParser {

	public JsfManagedBeanAwareELExpressionParser(ExpressionFactory expressionFactory) {
		super(expressionFactory);
		putContextFactory(RequestContext.class, new RequestContextELContextFactory());
	}

	private static class RequestContextELContextFactory implements ELContextFactory {
		public ELContext getELContext(Object target) {
			RequestContext context = (RequestContext) target;
			List<ELResolver> customResolvers = new ArrayList<ELResolver>();
			customResolvers.add(new RequestContextELResolver(context));
			customResolvers.add(new FlowResourceELResolver(context));
			customResolvers.add(new ImplicitFlowVariableELResolver(context));
			customResolvers.add(new ScopeSearchingELResolver(context));
			customResolvers.add(new SpringBeanWebFlowELResolver(context));
			customResolvers.add(new ActionMethodELResolver());
			customResolvers.add(new JsfManagedBeanResolver());
			ELResolver resolver = new DefaultELResolver(customResolvers);
			return new WebFlowELContext(resolver);
		}
	}

	private static class WebFlowELContext extends ELContext {

		private final ELResolver resolver;

		public WebFlowELContext(ELResolver resolver) {
			this.resolver = resolver;
		}

		public ELResolver getELResolver() {
			return this.resolver;
		}

		public FunctionMapper getFunctionMapper() {
			return null;
		}

		public VariableMapper getVariableMapper() {
			return null;
		}
	}

}
