/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Abel Hegedus, Tamas Szabo - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.emf.incquery.validation.runtime.ui;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.ui.IEditorPart;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra2.emf.incquery.validation.runtime.ValidationPartListener;
import org.eclipse.viatra2.emf.incquery.validation.runtime.ValidationUtil;

public class ValidationInitUtil {

	public static void initializeAdapters(IEditorPart activeEditor, Notifier root) throws IncQueryException {
//		if(adapterMap.containsKey(activeEditor)) {
			// FIXME define proper semantics for validation based on selection
			// FIXME handle already existing violations
			//adapterMap.get(activeEditor).addAll(adapters);
//		} else {
		if (!ValidationUtil.getAdapterMap().containsKey(activeEditor)) {
			ValidationUtil.addNotifier(activeEditor, root);
			activeEditor.getSite().getPage().addPartListener(ValidationPartListener.getInstance());
		}
	}
}