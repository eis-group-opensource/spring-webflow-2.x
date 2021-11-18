/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.webflow;

import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;

import org.springframework.util.Assert;

/**
 * Custom {@link ApplicationFactory} that ensures the FlowApplication is the first {@link Application} in the chain,
 * which in turn guarantees the install order for other JSF components.
 * 
 * @see FlowApplication
 * 
 * @author Jeremy Grelle
 * @author Phillip Webb
 */
public class FlowApplicationFactory extends ApplicationFactory {

	private final ApplicationFactory wrapped;

	public FlowApplicationFactory(ApplicationFactory wrapped) {
		Assert.notNull(wrapped, "The wrapped ApplicationFactory instance must not be null!");
		this.wrapped = wrapped;
	}

	public Application getApplication() {
		Application application = this.wrapped.getApplication();
		if (application != null && (!(application instanceof FlowApplication))) {
			setApplication(new FlowApplication(application));
		}
		return this.wrapped.getApplication();
	}

	public void setApplication(Application application) {
		this.wrapped.setApplication(application);
	}

	public ApplicationFactory getWrapped() {
		return this.wrapped;
	}
}
