/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.model;

import java.lang.reflect.Method;

import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIViewRoot;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.faces.webflow.FlowActionListener;
import org.springframework.util.ReflectionUtils;

/**
 * Custom {@link ActionListener} that inspects the {@link UIComponent} that signaled the current {@link ActionEvent} to
 * determine whether it is a child of any iterator type of component (such as {@link UIData}) that uses a
 * {@link SelectionAware} data model implementation. If a containing SelectionAware model is found, the row containing
 * the event-signaling component instance will be selected. This enables convenient access to the selected model state
 * at any time through EL expressions such as #{model.selectedRow.id} without having to rely on the whether or not the
 * current row index is pointing to the desired row as it would need to be to use an expression such as
 * #{model.rowData.id}
 * 
 * @author Jeremy Grelle
 */
public class SelectionTrackingActionListener implements ActionListener {

	private static final Log logger = LogFactory.getLog(FlowActionListener.class);

	private final ActionListener delegate;

	public SelectionTrackingActionListener(ActionListener delegate) {
		this.delegate = delegate;
	}

	public void processAction(ActionEvent event) throws AbortProcessingException {
		trackSelection(event.getComponent());
		this.delegate.processAction(event);
	}

	private void trackSelection(UIComponent component) {
		// Find parent component with a SelectionAware model if it exists
		UIComponent currentComponent = component;
		while (currentComponent.getParent() != null && !(currentComponent.getParent() instanceof UIViewRoot)) {
			UIComponent parent = currentComponent.getParent();
			Method valueAccessor = ReflectionUtils.findMethod(parent.getClass(), "getValue");
			if (valueAccessor != null) {
				Object value = ReflectionUtils.invokeMethod(valueAccessor, parent);
				if (value != null && value instanceof SelectionAware) {
					((SelectionAware<?>) value).setCurrentRowSelected(true);
					if (logger.isDebugEnabled()) {
						logger.debug("Row selection has been set on the current SelectionAware data model.");
					}
					break;
				}
			}
			currentComponent = currentComponent.getParent();
		}
	}

}
