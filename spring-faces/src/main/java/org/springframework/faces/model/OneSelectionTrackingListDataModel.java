/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.model;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.DataModel;

import org.springframework.util.Assert;

/**
 * A {@link DataModel} implementation that tracks the currently selected row, allowing only one selection at a time.
 * 
 * @author Jeremy Grelle
 */
public class OneSelectionTrackingListDataModel<T> extends SerializableListDataModel<T> implements SelectionAware<T> {

	/**
	 * The list of currently selected row data objects.
	 */
	private List<T> selections = new ArrayList<T>();

	public OneSelectionTrackingListDataModel() {
		super();
	}

	public OneSelectionTrackingListDataModel(List<T> list) {
		super(list);
	}

	public List<T> getSelections() {
		return this.selections;
	}

	public boolean isCurrentRowSelected() {
		return this.selections.contains(getRowData());
	}

	public void select(T rowData) {
		Assert.isTrue((getWrappedData()).contains(rowData), "The object to select is not contained in this DataModel.");
		this.selections.clear();
		this.selections.add(rowData);
	}

	public void selectAll() {
		if ((getWrappedData()).size() > 1) {
			throw new UnsupportedOperationException("This DataModel only allows one selection.");
		}
	}

	public void setCurrentRowSelected(boolean rowSelected) {
		if (!isRowAvailable()) {
			return;
		}

		if (!rowSelected) {
			this.selections.remove(getRowData());
		} else if (rowSelected && !this.selections.contains(getRowData())) {
			this.selections.clear();
			this.selections.add(getRowData());
		}
	}

	public void setSelections(List<T> selections) {
		Assert.isTrue(selections.size() <= 1, "This DataModel only allows one selection.");
		this.selections = selections;
	}

	public Object getSelectedRow() {
		if (this.selections.size() == 1) {
			return this.selections.get(0);
		} else {
			return null;
		}
	}

}
