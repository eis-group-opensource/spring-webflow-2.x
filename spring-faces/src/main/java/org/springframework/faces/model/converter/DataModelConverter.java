/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package org.springframework.faces.model.converter;

import java.lang.reflect.Constructor;
import java.util.List;

import javax.faces.model.DataModel;

import org.springframework.binding.convert.converters.Converter;
import org.springframework.faces.model.OneSelectionTrackingListDataModel;
import org.springframework.util.ClassUtils;

/**
 * A {@link Converter} implementation that converts an Object, Object array, or {@link List} into a JSF
 * {@link DataModel}.
 * 
 * @author Jeremy Grelle
 */
public class DataModelConverter implements Converter {

	public Class getSourceClass() {
		return List.class;
	}

	public Class getTargetClass() {
		return DataModel.class;
	}

	public Object convertSourceToTargetClass(Object source, Class targetClass) throws Exception {
		if (targetClass.equals(DataModel.class)) {
			targetClass = OneSelectionTrackingListDataModel.class;
		}
		Constructor<?> emptyConstructor = ClassUtils.getConstructorIfAvailable(targetClass, new Class[] {});
		DataModel<?> model = (DataModel<?>) emptyConstructor.newInstance(new Object[] {});
		model.setWrappedData(source);
		return model;
	}

}
