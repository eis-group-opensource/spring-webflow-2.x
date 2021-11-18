/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.webflow;

import javax.faces.lifecycle.Lifecycle;

import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.expression.Expression;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.validation.Validator;
import org.springframework.webflow.engine.builder.BinderConfiguration;
import org.springframework.webflow.engine.builder.ViewFactoryCreator;
import org.springframework.webflow.execution.ViewFactory;

/**
 * A {@link ViewFactoryCreator} implementation for creating instances of a JSF-specific {@link ViewFactory}.
 *
 * @author Jeremy Grelle
 */
public class JsfViewFactoryCreator implements ViewFactoryCreator {

	private static final String FACELETS_EXTENSION = ".xhtml";

	private Lifecycle lifecycle;

	public ViewFactory createViewFactory(Expression viewIdExpression, ExpressionParser expressionParser,
			ConversionService conversionService, BinderConfiguration binderConfiguration, Validator validator) {
		return new JsfViewFactory(viewIdExpression, getLifecycle());
	}

	public String getViewIdByConvention(String viewStateId) {
		return viewStateId + FACELETS_EXTENSION;
	}

	private Lifecycle getLifecycle() {
		if (this.lifecycle == null) {
			this.lifecycle = FlowLifecycle.newInstance();
		}
		return this.lifecycle;
	}

}
