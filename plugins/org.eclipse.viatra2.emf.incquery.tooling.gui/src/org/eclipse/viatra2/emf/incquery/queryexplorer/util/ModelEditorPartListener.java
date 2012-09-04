/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Tamas Szabo, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Tamas Szabo - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.queryexplorer.util;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;

/**
 * The PartListener is used to observe EditorPart close actions.
 * 
 * @author Tamas Szabo
 *
 */
public class ModelEditorPartListener implements IPartListener {
	
	@Override
	public void partActivated(IWorkbenchPart part) {

	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {

	}

	@Override
	public void partClosed(IWorkbenchPart part) {
		if (part != null && part instanceof IEditorPart) {
			IEditorPart closedEditor = (IEditorPart) part;
			
			if (closedEditor instanceof IEditingDomainProvider) {
				ResourceSet resourceSet = ((IEditingDomainProvider) closedEditor).getEditingDomain().getResourceSet();
				if (resourceSet.getResources().size() > 0) {
					if (QueryExplorer.getInstance() != null) {
						QueryExplorer.getInstance().getMatcherTreeViewerRoot().removePatternMatcherRoot(closedEditor, resourceSet);
					}
				}
			}
		}

	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {

	}

	@Override
	public void partOpened(IWorkbenchPart part) {

	}
}
