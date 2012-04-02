package org.eclipse.viatra2.emf.incquery.queryexplorer.util;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.CoreException;

public class ResourceChangeListener implements IResourceChangeListener {
	public void resourceChanged(IResourceChangeEvent event) {	
		if (event.getType() == IResourceChangeEvent.PRE_BUILD) {
			try {
				event.getDelta().accept(new DeltaVisitor());
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}
}