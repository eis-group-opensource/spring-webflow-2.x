/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.webflow;

import javax.faces.component.UIComponent;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.event.PostRestoreStateEvent;

/**
 * A VisitCallback used to deliver a PostRestoreStataEvent similar to
 * {@code RestoreViewPhase.deliverPostRestoreStateEvent(..)} in Sun's JSF.
 * 
 * @since 2.3.1
 */
class PostRestoreStateEventVisitCallback implements VisitCallback {

	private PostRestoreStateEvent event;

	public VisitResult visit(VisitContext context, UIComponent target) {
		if (this.event == null) {
			this.event = new PostRestoreStateEvent(target);
		} else {
			this.event.setComponent(target);
		}
		target.processEvent(this.event);
		return VisitResult.ACCEPT;
	}
}
