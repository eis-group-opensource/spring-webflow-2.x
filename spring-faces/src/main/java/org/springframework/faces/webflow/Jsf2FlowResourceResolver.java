/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.webflow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @deprecated Use FlowResourceResolver
 */
@Deprecated
public class Jsf2FlowResourceResolver extends FlowResourceResolver {

	Log logger = LogFactory.getLog(FlowExternalContext.class);

	public Jsf2FlowResourceResolver() {
		this.logger.warn("Jsf2FlowResourceResolver has been deprecated, please update your faces-config.xml to use FlowResourceResolver");
	}
}
