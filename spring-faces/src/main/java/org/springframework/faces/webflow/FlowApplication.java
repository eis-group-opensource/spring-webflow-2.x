/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.webflow;

import javax.faces.application.Application;
import javax.faces.application.ApplicationWrapper;
import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;

import org.springframework.util.Assert;

/**
 * Wraps an {@link Application} instance in order to ensure Web Flow specific implementations of {@link ViewHandler} and
 * {@link StateManager} are inserted at the front of the processing chain in JSF environments. This is done by
 * intercepting the corresponding setters. All other methods are simple delegation methods.
 * 
 * @author Rossen Stoyanchev
 * @author Phillip Webb
 */
public class FlowApplication extends ApplicationWrapper {

	private final Application wrapped;

	/**
	 * Class constructor that accepts a delegate Application instance. If the delegate has default instantiation logic
	 * for its StateManager and ViewHandler instances, those will be wrapped with {@link FlowStateManager} and a
	 * {@link FlowViewHandler} instance.
	 * 
	 * @param wrapped the wrapped Application instance.
	 */
	public FlowApplication(Application wrapped) {
		Assert.notNull(wrapped, "The wrapped Application instance must not be null!");
		this.wrapped = wrapped;

		ViewHandler handler = this.wrapped.getViewHandler();
		if (shouldWrap(handler)) {
			wrapAndSetViewHandler(handler);
		}

		StateManager manager = this.wrapped.getStateManager();
		if (shouldWrap(manager)) {
			wrapAndSetStateManager(manager);
		}
	}

	public Application getWrapped() {
		return this.wrapped;
	}

	/**
	 * Inserts {@link FlowStateManager} in front of the given StateManager (if not already done).
	 */
	public void setStateManager(StateManager manager) {
		if (shouldWrap(manager)) {
			wrapAndSetStateManager(manager);
		} else {
			super.setStateManager(manager);
		}
	}

	private boolean shouldWrap(StateManager manager) {
		return (manager != null) && (!(manager instanceof FlowStateManager));
	}

	private void wrapAndSetStateManager(StateManager target) {
		super.setStateManager(new FlowStateManager(target));
	}

	/**
	 * Inserts a {@link FlowViewHandler} in front of the given ViewHandler (if not already done).
	 */
	public void setViewHandler(ViewHandler handler) {
		if (shouldWrap(handler)) {
			wrapAndSetViewHandler(handler);
		} else {
			super.setViewHandler(handler);
		}
	}

	private boolean shouldWrap(ViewHandler delegateViewHandler) {
		return (delegateViewHandler != null) && (!(delegateViewHandler instanceof FlowViewHandler));
	}

	private boolean wrapAndSetViewHandler(ViewHandler target) {
		if ((target != null) && (!(target instanceof FlowViewHandler))) {
			ViewHandler handler = new FlowViewHandler(target);
			super.setViewHandler(handler);
			return true;
		}
		return false;
	}
}
