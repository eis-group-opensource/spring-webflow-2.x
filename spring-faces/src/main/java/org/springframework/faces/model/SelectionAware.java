/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.model;

import java.util.List;

import javax.faces.model.DataModel;

/**
 * Interface for {@link DataModel} implementations that need to track selected rows.
 * 
 * @author Jeremy Grelle
 */
public interface SelectionAware<T> {

	/**
	 * Checks whether the row pointed to by the model's current index is selected.
	 * @return true if the current row data object is selected
	 */
	public boolean isCurrentRowSelected();

	/**
	 * Sets whether the row pointed to by the model's current index is selected
	 * @param rowSelected true to select the current row
	 */
	public void setCurrentRowSelected(boolean rowSelected);

	/**
	 * Sets the list of selected row data objects for the model.
	 * @param selections the list of selected row data objects
	 */
	public void setSelections(List<T> selections);

	/**
	 * Returns the list of selected row data objects for the model.
	 * @return the list of selected row data objects
	 */
	public List<T> getSelections();

	/**
	 * Selects all row data objects in the model.
	 */
	public void selectAll();

	/**
	 * Selects the given row data object in the model.
	 * @param rowData the row data object to select.
	 */
	public void select(T rowData);
}
