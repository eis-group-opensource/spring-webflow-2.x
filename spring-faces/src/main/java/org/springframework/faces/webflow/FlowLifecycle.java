/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.webflow;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.faces.support.LifecycleWrapper;

/**
 * Custom {@link Lifecycle} for Spring Web Flow that only executes the APPLY_REQUEST_VALUES through INVOKE_APPLICATION
 * phases.
 * <p>
 * This Lifecycle does not execute the RESTORE_VIEW phase since view creation and restoration are now handled by the
 * {@link JsfViewFactory}.
 * </p>
 * 
 * @author Jeremy Grelle
 * @author Phillip Webb
 */
public class FlowLifecycle extends LifecycleWrapper {

	private static final Log logger = LogFactory.getLog(FlowLifecycle.class);

	private final Lifecycle wrapped;

	public static Lifecycle newInstance() {
		LifecycleFactory lifecycleFactory = JsfUtils.findFactory(LifecycleFactory.class);
		Lifecycle defaultLifecycle = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
		return new FlowLifecycle(defaultLifecycle);

	}

	FlowLifecycle(Lifecycle wrapped) {
		this.wrapped = wrapped;
	}

	public Lifecycle getWrapped() {
		return this.wrapped;
	}

	/**
	 * Executes APPLY_REQUEST_VALUES through INVOKE_APPLICATION.
	 */
	public void execute(FacesContext context) throws FacesException {
		logger.debug("Executing view post back lifecycle");
		for (int p = PhaseId.APPLY_REQUEST_VALUES.getOrdinal(); p <= PhaseId.INVOKE_APPLICATION.getOrdinal(); p++) {
			PhaseId phaseId = PhaseId.VALUES.get(p);
			if (!skipPhase(context, phaseId)) {
				context.setCurrentPhaseId(phaseId);
				invokePhase(context, phaseId);
			}
		}
	}

	private boolean skipPhase(FacesContext context, PhaseId phaseId) {
		if (context.getResponseComplete()) {
			return true;
		} else if (context.getRenderResponse()) {
			return true;
		} else {
			return false;
		}
	}

	private void invokePhase(FacesContext context, PhaseId phaseId) {
		JsfUtils.notifyBeforeListeners(phaseId, this, context);
		if (phaseId == PhaseId.APPLY_REQUEST_VALUES) {
			logger.debug("Processing decodes");
			context.getViewRoot().processDecodes(context);
		} else if (phaseId == PhaseId.PROCESS_VALIDATIONS) {
			logger.debug("Processing validators");
			context.getViewRoot().processValidators(context);
		} else if (phaseId == PhaseId.UPDATE_MODEL_VALUES) {
			logger.debug("Processing model updates");
			context.getViewRoot().processUpdates(context);
		} else {
			logger.debug("Processing application");
			context.getViewRoot().processApplication(context);
		}
		JsfUtils.notifyAfterListeners(phaseId, this, context);
	}
}
