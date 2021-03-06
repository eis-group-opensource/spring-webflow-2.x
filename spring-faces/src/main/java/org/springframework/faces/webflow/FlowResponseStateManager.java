/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.webflow;

import java.io.IOException;
import java.io.Writer;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.ResponseStateManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.faces.support.ResponseStateManagerWrapper;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;

/**
 * A custom ResponseStateManager that writes JSF state to a Web Flow managed view-scoped variable. This class is plugged
 * in via {@link FlowRenderKit}.
 * 
 * @author Rossen Stoyanchev
 * @author Phillip Webb
 * 
 * @since 2.2.0
 */
public class FlowResponseStateManager extends ResponseStateManagerWrapper {

	private static final Log logger = LogFactory.getLog(FlowResponseStateManager.class);

	static final String FACES_VIEW_STATE = "facesViewState";

	private static final char[] STATE_FIELD_START = ("<input type=\"hidden\" name=\""
			+ ResponseStateManager.VIEW_STATE_PARAM + "\" id=\"" + ResponseStateManager.VIEW_STATE_PARAM + "\" value=\"")
			.toCharArray();

	private static final char[] STATE_FIELD_END = "\" />".toCharArray();

	private final ResponseStateManager wrapped;

	public FlowResponseStateManager(ResponseStateManager wrapped) {
		this.wrapped = wrapped;
	}

	public ResponseStateManager getWrapped() {
		return this.wrapped;
	}

	@Override
	public void writeState(FacesContext facesContext, Object state) throws IOException {
		if (!JsfUtils.isFlowRequest()) {
			super.writeState(facesContext, state);
		} else {
			saveState(state);
			ResponseWriter writer = facesContext.getResponseWriter();
			writeViewStateField(facesContext, writer);
			writeRenderKitIdField(facesContext, writer);
		}
	}

	@Override
	public Object getState(FacesContext facesContext, String viewId) {
		if (!JsfUtils.isFlowRequest()) {
			return super.getState(facesContext, viewId);
		}
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		Object state = requestContext.getViewScope().get(FACES_VIEW_STATE);
		if (state == null) {
			logger.debug("No matching view in view scope");
		}
		return state;
	}

	/**
	 * This method returns the flow execution key to be used as the value for the "javax.faces.ViewState" hidden input
	 * field. The value of this key is not important because JSF state is stored in a Web Flow managed view scoped
	 * variable. However the presence of the view state parameter alone is important for triggering actions. Hence we
	 * return the most logical value, which is the flow execution key.
	 */
	@Override
	public String getViewState(FacesContext facesContext, Object state) {
		if (!JsfUtils.isFlowRequest()) {
			return super.getViewState(facesContext, state);
		}
		saveState(state);
		return retrieveViewStateToken(facesContext);
	}

	private void saveState(Object state) {
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		requestContext.getViewScope().put(FACES_VIEW_STATE, state);
	}

	private String getFlowExecutionKey() {
		RequestContext requestContext = RequestContextHolder.getRequestContext();
		return requestContext.getFlowExecutionContext().getKey().toString();
	}

	/**
	 * See comments on {@link ResponseStateManager#VIEW_STATE_PARAM}.
	 * 
	 * @param context the <code>FacesContext</code> for the current request
	 * @param writer the <code>ResponseWriter</code> to write to
	 * @throws IOException if an error occurs writing to the client
	 */
	private void writeViewStateField(FacesContext context, Writer writer) throws IOException {
		writer.write(STATE_FIELD_START);
		writer.write(retrieveViewStateToken(context));
		writer.write(STATE_FIELD_END);
	}

	/**
	 * Check conversation attributes for randomized token value and returns it or flowExecutionKey.
	 * @return viewState token or flowExecutionKey if viewState token is not found
	 */
	private String retrieveViewStateToken(FacesContext context) {
		String viewStateToken = (String) RequestContextHolder.getRequestContext().getConversationScope().get(ResponseStateManager.VIEW_STATE_PARAM);
		if (viewStateToken == null) {
			viewStateToken = getFlowExecutionKey();
		}
		return viewStateToken;
	}

	/**
	 * See comments on <code>ResponseStateManager.RENDER_KIT_ID_PARAM</code> in
	 * {@link ResponseStateManager#writeState(FacesContext, Object)}.
	 * 
	 * @param context the <code>FacesContext</code> for the current request
	 * @param writer the <code>ResponseWriter</code> to write to
	 * @throws IOException if an error occurs writing to the client
	 */
	private void writeRenderKitIdField(FacesContext context, ResponseWriter writer) throws IOException {
		String result = context.getApplication().getDefaultRenderKitId();
		if (result != null && !RenderKitFactory.HTML_BASIC_RENDER_KIT.equals(result)) {
			writer.startElement("input", context.getViewRoot());
			writer.writeAttribute("type", "hidden", "type");
			writer.writeAttribute("name", ResponseStateManager.RENDER_KIT_ID_PARAM, "name");
			writer.writeAttribute("value", result, "value");
			writer.endElement("input");
		}
	}
}
