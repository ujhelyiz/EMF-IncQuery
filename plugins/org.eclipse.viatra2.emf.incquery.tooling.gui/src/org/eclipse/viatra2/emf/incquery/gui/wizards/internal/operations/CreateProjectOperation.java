package org.eclipse.viatra2.emf.incquery.gui.wizards.internal.operations;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.viatra2.emf.incquery.core.project.ProjectGenerationHelper;

public class CreateProjectOperation extends
		WorkspaceModifyOperation {
	private final IProject projectHandle;
	private final IProjectDescription description;
	private final List<String> dependencies;

	public CreateProjectOperation(IProject projectHandle,
			IProjectDescription description, List<String> dependencies) {
		this.projectHandle = projectHandle;
		this.description = description;
		this.dependencies = dependencies;
	}

	protected void execute(IProgressMonitor monitor)
			throws CoreException {
		ProjectGenerationHelper.createProject(description,
				projectHandle, dependencies, monitor);
	}
}