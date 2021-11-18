/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.webflow;

import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.webflow.expression.spel.WebFlowSpringELExpressionParser;

/**
 * A Spring EL {@link ExpressionParser} for use with JSF. Adds JSF specific Spring EL PropertyAccessors.
 * 
 * @author Rossen Stoyanchev
 * @since 2.1
 */
public class FacesSpringELExpressionParser extends WebFlowSpringELExpressionParser {

	public FacesSpringELExpressionParser(SpelExpressionParser expressionParser) {
		super(expressionParser);
		addPropertyAccessor(new JsfManagedBeanPropertyAccessor());
	}

	public FacesSpringELExpressionParser(SpelExpressionParser expressionParser, ConversionService conversionService) {
		super(expressionParser, conversionService);
		addPropertyAccessor(new JsfManagedBeanPropertyAccessor());
	}

}
