/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.model.converter;

import javax.faces.model.DataModel;

import org.springframework.binding.convert.ConversionService;
import org.springframework.binding.convert.service.DefaultConversionService;
import org.springframework.faces.model.OneSelectionTrackingListDataModel;

/**
 * Convenient {@link ConversionService} implementation for JSF that composes JSF-specific converters with the standard
 * Web Flow converters.
 * 
 * <p>
 * In addition to the standard Web Flow conversion, this service provide conversion from a list into a
 * {@link OneSelectionTrackingListDataModel} using a "dataModel" alias for the type.
 * </p>
 * 
 * @author Jeremy Grelle
 */
public class FacesConversionService extends DefaultConversionService {

	public FacesConversionService() {
		addFacesConverters();
	}

	protected void addFacesConverters() {
		addConverter(new DataModelConverter());
		addAlias("dataModel", DataModel.class);
	}
}
