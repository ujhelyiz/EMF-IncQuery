/*******************************************************************************
 * Copyright (c) 2010-2012, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.databinding.runtime.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.viatra2.emf.incquery.runtime.api.IPatternMatch;
import org.eclipse.viatra2.patternlanguage.core.helper.CorePatternLanguageHelper;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Annotation;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.StringValue;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.ValueReference;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;

import com.google.common.base.Preconditions;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;

public class DatabindingAdapterUtil {
	
	public static final String OBSERVABLEVALUE_ANNOTATION = "ObservableValue";

    private DatabindingAdapterUtil() {}
	
	/**
	 * Returns an IObservableValue for the given match based on the given expression.
	 * If an attribute is not present in the expression than it tries with the 'name' attribute.
	 * If it is not present the returned value will be null.
	 * 
	 * @param match the match object
	 * @param expression the expression
	 * @return IObservableValue instance or null 
	 */
	public static IObservableValue getObservableValue(IPatternMatch match, String expression) {
		IObservableValue val = null;
		String[] objectTokens = expression.split("\\.");
		
		if (objectTokens.length > 0) {
			Object o = null;
			EStructuralFeature feature = null;
			
			if (objectTokens.length == 2) {
				o = match.get(objectTokens[0]);
				feature = getFeature(o, objectTokens[1]);
			}
			if (objectTokens.length == 1) {
				o = match.get(objectTokens[0]);
				feature = getFeature(o, "name");
			}
			if (o != null && feature != null) {
				val = EMFProperties.value(feature).observe(o);
			}
		}
		
		return val;
	}
	
	/**
	 * Registers the given changeListener for the appropriate features of the given signature.
	 * The features will be computed based on the message parameter.
	 * 
	 * @param signature the signature instance
	 * @param changeListener the changle listener 
	 * @param message the message which can be found in the appropriate PatternUI annotation
	 * @return the list of IObservableValue instances for which the IValueChangeListener was registered
	 */
	public static List<IObservableValue> observeFeatures(IPatternMatch match,	IValueChangeListener changeListener, String message) {
		List<IObservableValue> affectedValues = new ArrayList<IObservableValue>();
		if (message != null) {
			String[] tokens = message.split("\\$");

			for (int i = 0; i < tokens.length; i++) {
				
				//odd tokens 
				if (i % 2 != 0) {
					IObservableValue value = DatabindingAdapterUtil.getObservableValue(match, tokens[i]);
					if (value != null) {
						value.addValueChangeListener(changeListener);
						affectedValues.add(value);
					}
				}
			}
		}
		return affectedValues;
	}
	
	/**
	 * Registers the given change listener on the given object's all accessible fields. 
	 * This function uses Java Reflection.
	 * 
	 * @param changeListener the changle listener 
	 * @param object the observed object
	 * @return the list of IObservableValue instances for which the IValueChangeListener was registered
	 */
	public static List<IObservableValue> observeAllAttributes(IValueChangeListener changeListener, Object object) {
		List<IObservableValue> affectedValues = new ArrayList<IObservableValue>();
		if (object instanceof EObject) {
			for (EStructuralFeature feature : ((EObject) object).eClass().getEAllStructuralFeatures()) {
				IObservableValue val = EMFProperties.value(feature).observe(object);
				affectedValues.add(val);
				val.addValueChangeListener(changeListener);
			}
		}
		return affectedValues;
	}
	
	/**
	 * Get the structural feature with the given name of the given object.
	 * 
	 * @param o the object (must be an EObject)
	 * @param featureName the name of the feature
	 * @return the EStructuralFeature of the object or null if it can not be found
	 */
	public static EStructuralFeature getFeature(Object o, String featureName) {
		if (o instanceof EObject) {
			EStructuralFeature feature = ((EObject) o).eClass().getEStructuralFeature(featureName);
			return feature;
		}
		return null;
	}
	
	public static String getMessage(IPatternMatch match, String message) {
		String[] tokens = message.split("\\$");
		StringBuilder newText = new StringBuilder();
		
		for (int i = 0;i<tokens.length;i++) {
			if (i % 2 == 0) {
				newText.append(tokens[i]);
			}
			else {
				String[] objectTokens = tokens[i].split("\\.");
				if (objectTokens.length > 0) {
					Object o = null;
					EStructuralFeature feature = null;
					
					if (objectTokens.length == 1) {
						o = match.get(objectTokens[0]);
						feature = DatabindingAdapterUtil.getFeature(o, "name");
					}
					if (objectTokens.length == 2) {
						o = match.get(objectTokens[0]);
						feature = DatabindingAdapterUtil.getFeature(o, objectTokens[1]);
					}
					
					if (o != null && feature != null) {
						Object value = ((EObject) o).eGet(feature);
						if (value != null) {
							newText.append(value.toString());
						}
						else {
							newText.append("null");
						}
					}
					else if (o != null) {
						newText.append(o.toString());
					}
				}	
				else {
					newText.append("[no such parameter]");
				}
			}
		}
		
		return newText.toString();
	}

    /**
     * @param pattern
     * @return
     */
    public static Map<String, String> calculateObservableValues(Pattern pattern) {
        Map<String, String> propertyMap = Maps.newHashMap();
        for (Variable v : pattern.getParameters()) {
            propertyMap.put(v.getName(), v.getName());
        }
        for (Annotation annotation : CorePatternLanguageHelper
                .getAnnotationsByName(pattern, OBSERVABLEVALUE_ANNOTATION)) {
    
            ListMultimap<String, ValueReference> parameterMap = CorePatternLanguageHelper
                    .getAnnotationParameters(annotation);
            List<ValueReference> nameAttributes = parameterMap.get("name");
            List<ValueReference> expressionAttributes = parameterMap.get("expression");
            if (nameAttributes.size() == 1 && expressionAttributes.size() == 1) {
                Preconditions.checkArgument(nameAttributes.get(0) instanceof StringValue);
                Preconditions.checkArgument(expressionAttributes.get(0) instanceof StringValue);

                StringValue name = (StringValue) nameAttributes.get(0);
                String key = name.getValue();
                StringValue expr = (StringValue) expressionAttributes.get(0);
                String value = expr.getValue();

                if (key != null && value != null) {
                    propertyMap.put(key, value);
                }
            }
    	}
        return propertyMap;
    }
}
