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
package org.eclipse.viatra2.emf.incquery.queryexplorer.handlers.util;

import org.eclipse.core.runtime.ILog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.viatra2.emf.incquery.gui.IncQueryGUIPlugin;
import org.eclipse.viatra2.emf.incquery.queryexplorer.QueryExplorer;
import org.eclipse.viatra2.emf.incquery.queryexplorer.content.matcher.MatcherTreeViewerRootKey;

/**
 * 
 * The class represents an instance model registered in the Query Explorer along with its source {@link IEditorPart}. 
 * Subclasses of this class must implement the editor specific handling of load/unload/showLocation actions. 
 * 
 * @author Tamas Szabo
 *
 */
public abstract class ModelConnector {

	protected MatcherTreeViewerRootKey key;
	protected IWorkbenchPage workbenchPage;
	protected ILog logger = IncQueryGUIPlugin.getDefault().getLog(); 
	
	public ModelConnector(MatcherTreeViewerRootKey key) {
		this.key = key;
		this.logger = IncQueryGUIPlugin.getDefault().getLog(); 
		this.workbenchPage = key.getEditorPart().getSite().getPage();
	}
	
	/**
	 * Loads the instance model into the {@link QueryExplorer}
	 */
	public abstract void loadModel();
	
	/**
	 * Unloads the instance model from the {@link QueryExplorer}
	 */
	public abstract void unloadModel();
	
	/**
	 * Shows the location of the given objects inside the specific editor
	 * 
	 * @param locationObjects the objects whose location will be shown
	 */
	public abstract void showLocation(Object[] locationObjects);
	
}
