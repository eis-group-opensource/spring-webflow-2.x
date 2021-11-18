/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.support;

import java.io.IOException;

import javax.faces.FacesWrapper;
import javax.faces.application.StateManager.SerializedView;
import javax.faces.context.FacesContext;
import javax.faces.render.ResponseStateManager;

/**
 * Provides a simple implementation of {@link ResponseStateManager} that can be subclassed by developers wishing to
 * provide specialized behavior to an existing {@link ResponseStateManager instance} . The default implementation of all
 * methods is to call through to the wrapped {@link ResponseStateManager}.
 * 
 * @author Phillip Webb
 * 
 * @since 2.4
 */
@SuppressWarnings("deprecation")
public abstract class ResponseStateManagerWrapper extends ResponseStateManager implements
		FacesWrapper<ResponseStateManager> {

	public abstract ResponseStateManager getWrapped();

	@Override
	public void writeState(FacesContext context, Object state) throws IOException {
		getWrapped().writeState(context, state);
	}

	@Override
	@Deprecated
	public void writeState(FacesContext context, SerializedView state) throws IOException {
		getWrapped().writeState(context, state);
	}

	@Override
	public Object getState(FacesContext context, String viewId) {
		return getWrapped().getState(context, viewId);
	}

	@Override
	@Deprecated
	public Object getTreeStructureToRestore(FacesContext context, String viewId) {
		return getWrapped().getTreeStructureToRestore(context, viewId);
	}

	@Override
	@Deprecated
	public Object getComponentStateToRestore(FacesContext context) {
		return getWrapped().getComponentStateToRestore(context);
	}

	@Override
	public boolean isPostback(FacesContext context) {
		return getWrapped().isPostback(context);
	}

	@Override
	public String getViewState(FacesContext context, Object state) {
		return getWrapped().getViewState(context, state);
	}
}
