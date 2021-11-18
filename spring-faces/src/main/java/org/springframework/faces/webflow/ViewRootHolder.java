/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.webflow;

import java.io.Serializable;

import javax.faces.component.UIViewRoot;

/**
 * Holder for the JSF UIViewRoot
 * 
 * @author Scott Andrews
 */
class ViewRootHolder implements Serializable {

	private transient UIViewRoot viewRoot;

	public ViewRootHolder(UIViewRoot viewRoot) {
		this.viewRoot = viewRoot;
	}

	public UIViewRoot getViewRoot() {
		return this.viewRoot;
	}

}