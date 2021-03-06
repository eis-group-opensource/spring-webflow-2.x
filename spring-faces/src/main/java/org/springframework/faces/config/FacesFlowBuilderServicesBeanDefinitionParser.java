/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * Parser for the flow-builder-services tag.
 * 
 * @author Jeremy Grelle
 * @author Christian Dupuis
 */
public class FacesFlowBuilderServicesBeanDefinitionParser extends AbstractSingleBeanDefinitionParser implements
		BeanDefinitionParser {

	// --------------------------- Full qualified class names ----------------------- //
	private static final String FACES_CONVERSION_SERVICE_CLASS_NAME = "org.springframework.faces.model.converter.FacesConversionService";
	private static final String FLOW_BUILDER_SERVICES_CLASS_NAME = "org.springframework.webflow.engine.builder.support.FlowBuilderServices";
	private static final String JSF_VIEW_FACTORY_CREATOR_CLASS_NAME = "org.springframework.faces.webflow.JsfViewFactoryCreator";
	private static final String FACES_SPRING_EL_EXPRESSION_PARSER_CLASS_NAME = "org.springframework.faces.webflow.FacesSpringELExpressionParser";
	private static final String WEBFLOW_SPRING_EL_EXPRESSION_PARSER_CLASS_NAME = "org.springframework.webflow.expression.spel.WebFlowSpringELExpressionParser";

	// --------------------------- XML Config Attributes ----------------------- //
	private static final String CONVERSION_SERVICE_ATTR = "conversion-service";
	private static final String DEVELOPMENT_ATTR = "development";
	private static final String ENABLE_MANAGED_BEANS_ATTR = "enable-managed-beans";
	private static final String EXPRESSION_PARSER_ATTR = "expression-parser";
	private static final String VIEW_FACTORY_CREATOR_ATTR = "view-factory-creator";

	// --------------------------- Bean Configuration Properties --------------------- //
	private static final String CONVERSION_SERVICE_PROPERTY = "conversionService";
	private static final String DEVELOPMENT_PROPERTY = "development";
	private static final String EXPRESSION_PARSER_PROPERTY = "expressionParser";
	private static final String VIEW_FACTORY_CREATOR_PROPERTY = "viewFactoryCreator";

	protected String getBeanClassName(Element element) {
		return FLOW_BUILDER_SERVICES_CLASS_NAME;
	}

	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder definitionBuilder) {
		CompositeComponentDefinition componentDefinition = new CompositeComponentDefinition(element.getLocalName(),
				parserContext.extractSource(element));
		parserContext.pushContainingComponent(componentDefinition);

		parseConversionService(element, parserContext, definitionBuilder);
		parseExpressionParser(element, parserContext, definitionBuilder,
				parseEnableManagedBeans(element, definitionBuilder));
		parseViewFactoryCreator(element, parserContext, definitionBuilder);
		parseDevelopment(element, definitionBuilder);

		parserContext.popAndRegisterContainingComponent();
	}

	private boolean parseEnableManagedBeans(Element element, BeanDefinitionBuilder definitionBuilder) {
		String enableManagedBeans = element.getAttribute(ENABLE_MANAGED_BEANS_ATTR);
		if (StringUtils.hasText(enableManagedBeans)) {
			return Boolean.valueOf(enableManagedBeans);
		} else {
			return false;
		}
	}

	private void parseConversionService(Element element, ParserContext context, BeanDefinitionBuilder definitionBuilder) {
		String conversionService = element.getAttribute(CONVERSION_SERVICE_ATTR);
		if (!StringUtils.hasText(conversionService)) {
			BeanDefinitionBuilder conversionServiceBuilder = BeanDefinitionBuilder
					.genericBeanDefinition(FACES_CONVERSION_SERVICE_CLASS_NAME);
			conversionService = registerInfrastructureComponent(element, context, conversionServiceBuilder);
		}
		definitionBuilder.addPropertyReference(CONVERSION_SERVICE_PROPERTY, conversionService);
	}

	private void parseViewFactoryCreator(Element element, ParserContext context, BeanDefinitionBuilder definitionBuilder) {
		String viewFactoryCreator = element.getAttribute(VIEW_FACTORY_CREATOR_ATTR);
		if (!StringUtils.hasText(viewFactoryCreator)) {
			BeanDefinitionBuilder viewFactoryCreatorBuilder = BeanDefinitionBuilder
					.genericBeanDefinition(JSF_VIEW_FACTORY_CREATOR_CLASS_NAME);
			viewFactoryCreator = registerInfrastructureComponent(element, context, viewFactoryCreatorBuilder);
		}
		definitionBuilder.addPropertyReference(VIEW_FACTORY_CREATOR_PROPERTY, viewFactoryCreator);
	}

	private void parseExpressionParser(Element element, ParserContext context, BeanDefinitionBuilder definitionBuilder,
			boolean enableManagedBeans) {

		// String conversionService = getConversionService(definitionBuilder);
		String expressionParser = element.getAttribute(EXPRESSION_PARSER_ATTR);

		if (!StringUtils.hasText(expressionParser)) {

			BeanDefinitionBuilder spelExpressionParser = BeanDefinitionBuilder
					.genericBeanDefinition("org.springframework.expression.spel.standard.SpelExpressionParser");

			BeanDefinitionBuilder expressionParserBuilder;

			if (enableManagedBeans) {
				expressionParserBuilder = BeanDefinitionBuilder
						.genericBeanDefinition(FACES_SPRING_EL_EXPRESSION_PARSER_CLASS_NAME);
			} else {
				expressionParserBuilder = BeanDefinitionBuilder
						.genericBeanDefinition(WEBFLOW_SPRING_EL_EXPRESSION_PARSER_CLASS_NAME);
			}

			expressionParserBuilder.addConstructorArgValue(spelExpressionParser.getBeanDefinition());
			expressionParserBuilder.addConstructorArgReference(getConversionService(definitionBuilder));
			expressionParser = registerInfrastructureComponent(element, context, expressionParserBuilder);

		} else if (enableManagedBeans) {
			context.getReaderContext().error(
					"Do not specify a custom expression-parser when enable-managed-beans is true",
					context.extractSource(element));
		}
		definitionBuilder.addPropertyReference(EXPRESSION_PARSER_PROPERTY, expressionParser);

	}

	private void parseDevelopment(Element element, BeanDefinitionBuilder definitionBuilder) {
		String development = element.getAttribute(DEVELOPMENT_ATTR);
		if (StringUtils.hasText(development)) {
			definitionBuilder.addPropertyValue(DEVELOPMENT_PROPERTY, development);
		}
	}

	private String getConversionService(BeanDefinitionBuilder definitionBuilder) {
		RuntimeBeanReference conversionServiceReference = (RuntimeBeanReference) definitionBuilder.getBeanDefinition()
				.getPropertyValues().getPropertyValue(CONVERSION_SERVICE_PROPERTY).getValue();
		return conversionServiceReference.getBeanName();
	}

	private String registerInfrastructureComponent(Element element, ParserContext context,
			BeanDefinitionBuilder componentBuilder) {
		String beanName = context.getReaderContext().generateBeanName(componentBuilder.getRawBeanDefinition());
		componentBuilder.getRawBeanDefinition().setSource(context.extractSource(element));
		componentBuilder.getRawBeanDefinition().setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		context.registerBeanComponent(new BeanComponentDefinition(componentBuilder.getBeanDefinition(), beanName));
		return beanName;
	}
}
