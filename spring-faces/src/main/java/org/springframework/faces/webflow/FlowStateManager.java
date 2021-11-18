/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.webflow;

import javax.faces.application.StateManager;
import javax.faces.application.StateManagerWrapper;
import javax.faces.context.FacesContext;

/**
 * Custom {@link StateManager} that manages ensures web flow's state is always stored server side.
 * 
 * @author Jeremy Grelle
 * @author Rossen Stoyanchev
 * @author Phillip Webb
 * 
 * @since 2.4
 */
public class FlowStateManager extends StateManagerWrapper {

	private final StateManager wrapped;

	public FlowStateManager(StateManager wrapped) {
		this.wrapped = wrapped;
	}

	public StateManager getWrapped() {
		return this.wrapped;
	}

	public boolean isSavingStateInClient(FacesContext context) {
		if (!JsfUtils.isFlowRequest()) {
			return super.isSavingStateInClient(context);
		} else {
			return false;
		}
	}
}
