/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.webflow;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.NavigationHandler;
import javax.faces.component.ActionSource2;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.expression.Expression;
import org.springframework.util.StringUtils;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.webflow.definition.TransitionDefinition;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.execution.View;
import org.springframework.webflow.validation.ValidationHelper;
import org.springframework.webflow.validation.WebFlowMessageCodesResolver;

/**
 * The default {@link ActionListener} implementation to be used with Web Flow.
 * 
 * This implementation bypasses the JSF {@link NavigationHandler} mechanism to instead let the event be handled directly
 * by Web Flow.
 * <p>
 * Web Flow's model-level validation will be invoked here after an event has been detected if the event is not an
 * immediate event.
 * 
 * @author Jeremy Grelle
 */
public class FlowActionListener implements ActionListener {

	private static final Log logger = LogFactory.getLog(FlowActionListener.class);

	private static final String MESSAGES_ID = "messages";

	private final ActionListener delegate;

	private final MessageCodesResolver messageCodesResolver = new WebFlowMessageCodesResolver();

	public FlowActionListener(ActionListener delegate) {
		this.delegate = delegate;
	}

	public void processAction(ActionEvent actionEvent) throws AbortProcessingException {
		if (!JsfUtils.isFlowRequest()) {
			this.delegate.processAction(actionEvent);
			return;
		}
		FacesContext context = FacesContext.getCurrentInstance();
		ActionSource2 source = (ActionSource2) actionEvent.getSource();
		String eventId = null;
		if (source.getActionExpression() != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Invoking action " + source.getActionExpression());
			}
			eventId = (String) source.getActionExpression().invoke(context.getELContext(), null);
		}
		if (StringUtils.hasText(eventId)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Event '" + eventId + "' detected");
			}
			if (source.isImmediate() || validateModel(context, eventId)) {
				context.getExternalContext().getRequestMap().put(JsfView.EVENT_KEY, eventId);
			}
		} else {
			logger.debug("No action event detected");
			context.getExternalContext().getRequestMap().remove(JsfView.EVENT_KEY);
		}
		// tells JSF lifecycle that rendering should now happen and any subsequent phases should be skipped
		// required in the case of this action listener firing immediately (immediate=true) before validation
		context.renderResponse();
	}

	// internal helpers

	private boolean validateModel(FacesContext facesContext, String eventId) {
		boolean isValid = true;
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		Object model = getModelObject(requestContext);
		if (shouldValidate(requestContext, model, eventId)) {
			validate(requestContext, model, eventId);
			if (requestContext.getMessageContext().hasErrorMessages()) {
				isValid = false;
				if (requestContext.getExternalContext().isAjaxRequest()) {
					List<String> fragments = new ArrayList<String>();
					String formId = getModelExpression(requestContext).getExpressionString();
					if (facesContext.getViewRoot().findComponent(formId) != null) {
						fragments.add(formId);
					}
					if (facesContext.getViewRoot().findComponent(MESSAGES_ID) != null) {
						fragments.add(MESSAGES_ID);
					}
					if (fragments.size() > 0) {
						String[] fragmentsArray = new String[fragments.size()];
						for (int i = 0; i < fragments.size(); i++) {
							fragmentsArray[i] = fragments.get(i);
						}
						requestContext.getFlashScope().put(View.RENDER_FRAGMENTS_ATTRIBUTE, fragmentsArray);
					}
				}
			}
		}
		return isValid;
	}

	private Object getModelObject(RequestContext requestContext) {
		Expression model = getModelExpression(requestContext);
		if (model != null) {
			return model.getValue(requestContext);
		} else {
			return null;
		}
	}

	private Expression getModelExpression(RequestContext requestContext) {
		return (Expression) requestContext.getCurrentState().getAttributes().get("model");
	}

	private boolean shouldValidate(RequestContext requestContext, Object model, String eventId) {
		if (model == null) {
			return false;
		}
		TransitionDefinition transition = requestContext.getMatchingTransition(eventId);
		if (transition != null) {
			if (transition.getAttributes().contains("validate")) {
				return transition.getAttributes().getBoolean("validate");
			}
		}
		return true;
	}

	private void validate(RequestContext requestContext, Object model, String eventId) {
		new ValidationHelper(model, requestContext, eventId, getModelExpression(requestContext).getExpressionString(),
				null, this.messageCodesResolver, null).validate();
	}
}
