/*******************************************************************************
 * Copyright (c) 2004-2010 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.base.comprehension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap.Entry;

/**
 * @author Bergmann Gábor
 * 
 * Does not directly visit derived links, unless marked as a WellBehavingFeature.
 * Derived edges are automatically interpreted correctly in these cases:
 *  - EFeatureMaps
 *  - eOpposites of containments
 *  
 *
 */
public class EMFModelComprehension {

	/**
	 * Should not traverse this feature directly. 
	 * It is still possible that it can be represented in IQBase if {@link #representable(EStructuralFeature)} is true.
	 */
	public static boolean untraversableDirectly(EStructuralFeature feature) {
		boolean suspect = feature.isDerived() || feature.isVolatile();
		if(suspect) {
			// override support here 
			// (e.g. if manual notifications available, or no changes expected afterwards)
			suspect = !WellbehavingDerivedFeatureRegistry.isWellbehavingFeature(feature);
			// TODO verbose flag somewhere to ease debugging (for such warnings)
			// TODO add warning about not visited subtree (containment, FeatureMap and annotation didn't define otherwise)
		}
		return suspect;
	}
	
	/**
	 * This feature can be represented in IQBase.
	 */
	public static boolean representable(EStructuralFeature feature) {
		if (!untraversableDirectly(feature)) return true; 
		
		if (feature instanceof EReference) {
			final EReference reference = (EReference) feature;
			if (reference.isContainer() && representable(reference.getEOpposite())) return true;
		}

        boolean isMixed = "mixed".equals(EcoreUtil.getAnnotation(feature.getEContainingClass(), ExtendedMetaData.ANNOTATION_URI, "kind"));
        if (isMixed) return true; // TODO maybe check the "name"=":mixed" or ":group" feature for representability?
		
		final String groupAnnotation = EcoreUtil.getAnnotation(feature, ExtendedMetaData.ANNOTATION_URI, "group");
		if (groupAnnotation != null && groupAnnotation.length()>1 && '#' == groupAnnotation.charAt(0)) {
			final String groupFeatureName = groupAnnotation.substring(1);
			final EStructuralFeature groupFeature = feature.getEContainingClass().getEStructuralFeature(groupFeatureName);
			return representable(groupFeature);
		}
			
		return false;
	}
	
	public static void traverseModel(EMFVisitor visitor, Notifier source) {
		if (source == null) return;
		if (source instanceof EObject) {
			traverseObject(visitor, (EObject) source);
		}
		else if (source instanceof Resource) {
			traverseResource(visitor, (Resource) source);
		}
		else if (source instanceof ResourceSet) {
			traverseResourceSet(visitor, (ResourceSet) source);
		}
	}		
	
	public static void traverseResourceSet(EMFVisitor visitor, ResourceSet source) {
		if (source == null) return;
		final List<Resource> resources = new ArrayList<Resource>(source.getResources());
		for (Resource resource : resources) {
			traverseResource(visitor, resource);
		}
	}
	
	public static void traverseResource(EMFVisitor visitor, Resource source) {
		if (source == null) return;
		if(visitor.pruneSubtrees(source))
			return;
		final EList<EObject> contents = source.getContents();
		for (EObject eObject : contents) {
			traverseObject(visitor, eObject);
		}
	}

	
	public static void traverseObject(EMFVisitor visitor, EObject source) {
		if (source == null) return;
		if(source.eIsProxy()) {
			if (visitor.forceProxyResolution()) 
				source = EcoreUtil.resolve(source, source);
			if (source.eIsProxy()) {
				visitor.visitUnresolvableProxyObject(source);
				return;
			}
		}

		visitor.visitElement(source);
		for (EStructuralFeature feature: source.eClass().getEAllStructuralFeatures()) {
			if (untraversableDirectly(feature)) continue;
			final boolean visitorPrunes = visitor.pruneFeature(feature);
			if (visitorPrunes && !unprunableFeature(visitor, source, feature)) continue;
		
			if (feature.isMany()) {
				Collection<?> targets = (Collection<?>) source.eGet(feature);
				for (Object target : targets) {
					traverseFeatureInternal(visitor, source, feature, target, visitorPrunes);	
				}
			} else {
				Object target = source.eGet(feature);
				if (target != null) traverseFeatureInternal(visitor, source, feature, target, visitorPrunes);
			}
		}
	}
	
	private static boolean unprunableFeature(EMFVisitor visitor, EObject source, EStructuralFeature feature) {	
		return
				(
					feature instanceof EAttribute && 
					EcorePackage.eINSTANCE.getEFeatureMapEntry().equals(
							((EAttribute)feature).getEAttributeType()
					)
				) || (
						feature instanceof EReference &&
						((EReference)feature).isContainment() &&
						(!visitor.pruneSubtrees(source) || ((EReference)feature).getEOpposite() != null)
				);
	}



	public static void traverseFeature(
			EMFVisitor visitor, EObject source, EStructuralFeature feature, Object target) 
	{
		if (target == null) return;
		if (untraversableDirectly(feature)) return;	
		traverseFeatureInternalSimple(visitor, source, feature, target);
	}


	private static void traverseFeatureInternalSimple(EMFVisitor visitor,
			EObject source, EStructuralFeature feature, Object target) 
	{
		final boolean visitorPrunes = visitor.pruneFeature(feature);
		if (visitorPrunes && !unprunableFeature(visitor, source, feature)) return;
		
		traverseFeatureInternal(visitor, source, feature, target, visitorPrunes);
	}
	
	/**
	 * @pre target != null
	 */
	private static void traverseFeatureInternal(
			EMFVisitor visitor, EObject source, EStructuralFeature feature, Object target, boolean visitorPrunes) 
	{
		if (feature instanceof EAttribute) {
			if (!visitorPrunes) visitor.visitAttribute(source, (EAttribute)feature, target);
			if (target instanceof FeatureMap.Entry) { // emulated derived edge based on FeatureMap
				Entry entry = (FeatureMap.Entry) target;
				final EStructuralFeature emulated = entry.getEStructuralFeature();
				final Object emulatedTarget = entry.getValue();
				
				emulateUntraversableFeature(visitor, source, emulated, emulatedTarget);
			}
		} else if (feature instanceof EReference) {
			EReference reference = (EReference)feature;
			EObject targetObject = (EObject)target;
			if(targetObject.eIsProxy()) {
				if (visitor.forceProxyResolution()) 
					targetObject = EcoreUtil.resolve(targetObject,source);
				if (targetObject.eIsProxy()) {
					visitor.visitUnresolvableProxyFeature(source, reference, targetObject);
					return;
				}
			}
			if (reference.isContainment()) {
				if (!visitorPrunes) visitor.visitInternalContainment(source, reference, targetObject);
				if (!visitor.pruneSubtrees(source)) traverseObject(visitor, targetObject);
				
				final EReference opposite = reference.getEOpposite();
				if (opposite != null) { // emulated derived edge based on container opposite					
					emulateUntraversableFeature(visitor, targetObject, opposite, source);
				}
			} else {
//			if (containedElements.contains(target)) 
				if (!visitorPrunes) visitor.visitNonContainmentReference(source, reference, targetObject);
			}
//			else
//				visitor.visitExternalReference(source, reference, targetObject);
		}
		
	}

	/**
	 * Emulates a derived edge, if it is not visited otherwise
	 * @pre target != null
	 */
	private static void emulateUntraversableFeature(EMFVisitor visitor,
			EObject source, final EStructuralFeature emulated, final Object target) 
	{
		if (untraversableDirectly(emulated)) 
			traverseFeatureInternalSimple(visitor, source, emulated, target);
	}

}