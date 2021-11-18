/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.model;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.DataModel;

import org.springframework.util.Assert;

/**
 * A {@link DataModel} implementation that tracks the currently selected rows, allowing any number of rows to be
 * selected at one time.
 * 
 * @author Jeremy Grelle
 */
public class ManySelectionTrackingListDataModel<T> extends SerializableListDataModel<T> implements SelectionAware<T> {

	private List<T> selections = new ArrayList<T>();

	public ManySelectionTrackingListDataModel() {
		super();
	}

	public ManySelectionTrackingListDataModel(List<T> list) {
		super(list);
	}

	public List<T> getSelections() {
		return this.selections;
	}

	public boolean isCurrentRowSelected() {
		return this.selections.contains(getRowData());
	}

	public void selectAll() {
		this.selections.clear();
		this.selections.addAll(getWrappedData());
	}

	public void setCurrentRowSelected(boolean rowSelected) {
		if (!isRowAvailable()) {
			return;
		}
		if (rowSelected && !this.selections.contains(getRowData())) {
			this.selections.add(getRowData());
		} else if (!rowSelected) {
			this.selections.remove(getRowData());
		}
	}

	public void setSelections(List<T> selections) {
		this.selections = selections;
	}

	public void select(T rowData) {
		Assert.isTrue((getWrappedData()).contains(rowData), "The object to select is not contained in this DataModel.");
		if (!this.selections.contains(rowData)) {
			this.selections.add(rowData);
		}
	}

}
