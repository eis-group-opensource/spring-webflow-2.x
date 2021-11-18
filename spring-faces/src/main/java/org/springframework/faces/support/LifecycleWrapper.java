/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.support;

import javax.faces.FacesException;
import javax.faces.FacesWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;

/**
 * Provides a simple implementation of {@link Lifecycle} that can be subclassed by developers wishing to provide
 * specialized behavior to an existing {@link Lifecycle instance} . The default implementation of all methods is to call
 * through to the wrapped {@link Lifecycle}.
 * 
 * @author Phillip Webb
 * 
 * @since 2.4
 */
public abstract class LifecycleWrapper extends Lifecycle implements FacesWrapper<Lifecycle> {

	public abstract Lifecycle getWrapped();

	public void addPhaseListener(PhaseListener listener) {
		getWrapped().addPhaseListener(listener);
	}

	public void execute(FacesContext context) throws FacesException {
		getWrapped().execute(context);
	}

	public PhaseListener[] getPhaseListeners() {
		return getWrapped().getPhaseListeners();
	}

	public void removePhaseListener(PhaseListener listener) {
		getWrapped().removePhaseListener(listener);
	}

	public void render(FacesContext context) throws FacesException {
		getWrapped().render(context);
	}
}
