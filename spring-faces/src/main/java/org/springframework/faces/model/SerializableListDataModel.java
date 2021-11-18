/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.DataModel;
import javax.faces.model.DataModelEvent;
import javax.faces.model.DataModelListener;

import org.springframework.util.Assert;

/**
 * A simple List-to-JSF-DataModel adapter that is also serializable.
 * 
 * @author Jeremy Grelle
 */
public class SerializableListDataModel<T> extends DataModel<T> implements Serializable {

	private int rowIndex = 0;

	private List<T> data;

	public SerializableListDataModel() {
		this(new ArrayList<T>());
	}

	/**
	 * Adapt the list to a data model;
	 * @param list the list
	 */
	public SerializableListDataModel(List<T> list) {
		if (list == null) {
			list = new ArrayList<T>();
		}
		setWrappedData(list);
	}

	public int getRowCount() {
		return this.data.size();
	}

	public T getRowData() {
		Assert.isTrue(isRowAvailable(), getClass()
				+ " is in an illegal state - no row is available at the current index.");
		return this.data.get(this.rowIndex);
	}

	public int getRowIndex() {
		return this.rowIndex;
	}

	public List<T> getWrappedData() {
		return this.data;
	}

	public boolean isRowAvailable() {
		return this.rowIndex >= 0 && this.rowIndex < this.data.size();
	}

	public void setRowIndex(int newRowIndex) {
		if (newRowIndex < -1) {
			throw new IllegalArgumentException("Illegal row index for " + getClass() + ": " + newRowIndex);
		}
		int oldRowIndex = this.rowIndex;
		this.rowIndex = newRowIndex;
		if (this.data != null && oldRowIndex != this.rowIndex) {
			Object row = isRowAvailable() ? getRowData() : null;
			DataModelEvent event = new DataModelEvent(this, this.rowIndex, row);
			DataModelListener[] listeners = getDataModelListeners();
			for (DataModelListener listener : listeners) {
				listener.rowSelected(event);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void setWrappedData(Object data) {
		if (data == null) {
			data = new ArrayList<T>();
		}
		Assert.isInstanceOf(List.class, data, "The data object for " + getClass() + " must be a List");
		this.data = (List<T>) data;
		int newRowIndex = 0;
		setRowIndex(newRowIndex);
	}

	public String toString() {
		return this.data.toString();
	}

}
