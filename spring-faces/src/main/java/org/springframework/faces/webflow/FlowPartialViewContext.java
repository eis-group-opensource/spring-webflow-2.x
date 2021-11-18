/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.webflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.faces.context.PartialViewContext;
import javax.faces.context.PartialViewContextWrapper;

import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.execution.View;

/**
 * Web Flow {@link PartialViewContext} implementation allowing IDs for partial rendering to be specified from the
 * server-side. This is done in a flow definition with the &lt;render fragments="..." /&gt; action.
 * 
 * @author Rossen Stoyanchev
 */
public class FlowPartialViewContext extends PartialViewContextWrapper {

	private final PartialViewContext wrapped;

	public FlowPartialViewContext(PartialViewContext wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public PartialViewContext getWrapped() {
		return this.wrapped;
	}

	@Override
	public void setPartialRequest(boolean isPartialRequest) {
		getWrapped().setPartialRequest(isPartialRequest);
	}

	@Override
	public Collection<String> getRenderIds() {
		if (JsfUtils.isFlowRequest()) {
			RequestContext requestContext = RequestContextHolder.getRequestContext();
			String[] fragmentIds = (String[]) requestContext.getFlashScope().get(View.RENDER_FRAGMENTS_ATTRIBUTE);
			if (fragmentIds != null && fragmentIds.length > 0) {
				return new ArrayList<String>(Arrays.asList(fragmentIds));
			}
		}
		return getWrapped().getRenderIds();
	}

}
