/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Mark Czotter, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi, Mark Czotter - initial API and implementation
 *******************************************************************************/

package org.eclipse.incquery.tooling.core.generator.builder;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.PackageImport;
import org.eclipse.incquery.patternlanguage.emf.eMFPatternLanguage.PatternModel;
import org.eclipse.incquery.patternlanguage.emf.helper.EMFPatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.helper.CorePatternLanguageHelper;
import org.eclipse.incquery.patternlanguage.patternLanguage.CheckConstraint;
import org.eclipse.incquery.patternlanguage.patternLanguage.Constraint;
import org.eclipse.incquery.patternlanguage.patternLanguage.Pattern;
import org.eclipse.incquery.patternlanguage.patternLanguage.PatternBody;
import org.eclipse.incquery.runtime.util.CheckExpressionUtil;
import org.eclipse.incquery.tooling.core.generator.ExtensionGenerator;
import org.eclipse.incquery.tooling.core.generator.GenerateMatcherFactoryExtension;
import org.eclipse.incquery.tooling.core.generator.GenerateXExpressionEvaluatorExtension;
import org.eclipse.incquery.tooling.core.generator.builder.xmi.XmiModelSupport;
import org.eclipse.incquery.tooling.core.generator.fragments.IGenerationFragment;
import org.eclipse.incquery.tooling.core.generator.fragments.IGenerationFragmentProvider;
import org.eclipse.incquery.tooling.core.generator.genmodel.IEiqGenmodelProvider;
import org.eclipse.incquery.tooling.core.generator.util.EMFPatternLanguageJvmModelInferrerUtil;
import org.eclipse.incquery.tooling.core.project.ProjectGenerationHelper;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.xtext.builder.BuilderParticipant;
import org.eclipse.xtext.builder.EclipseResourceFileSystemAccess2;
import org.eclipse.xtext.generator.IGenerator;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescription.Delta;
import org.eclipse.xtext.ui.resource.IStorage2UriMapper;
import org.eclipse.xtext.util.Pair;
import org.eclipse.xtext.xbase.XExpression;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * @author Mark Czotter
 */
public class EMFPatternLanguageBuilderParticipant extends BuilderParticipant {

    @Inject
    private Injector injector;

    @Inject
    private IGenerator generator;

    @Inject
    private IGenerationFragmentProvider fragmentProvider;

    @Inject
    private EMFPatternLanguageJvmModelInferrerUtil util;

    @Inject
    private XmiModelSupport xmiModelSupport;

    @Inject
    private EnsurePluginSupport ensureSupport;

    @Inject
    private CleanSupport cleanSupport;

    @Inject
    private EclipseResourceSupport eclipseResourceSupport;

    @Inject
    private GenerateMatcherFactoryExtension matcherFactoryExtensionGenerator;

    @Inject
    private GenerateXExpressionEvaluatorExtension xExpressionEvaluatorExtensionGenerator;

    @Inject
    private IEiqGenmodelProvider genmodelProvider;

    @Inject
    private Logger logger;

    @Inject
    private IStorage2UriMapper storage2UriMapper;

    @Override
    public void build(final IBuildContext context, IProgressMonitor monitor) throws CoreException {
        if (!isEnabled(context)) {
            return;
        }
        final List<IResourceDescription.Delta> relevantDeltas = getRelevantDeltas(context);
        if (relevantDeltas.isEmpty()) {
            return;
        }
        // monitor handling
        if (monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        SubMonitor progress = SubMonitor.convert(monitor, 5);
        final IProject modelProject = context.getBuiltProject();
        modelProject.refreshLocal(IResource.DEPTH_INFINITE, progress.newChild(1));
        if (context.getBuildType() == BuildType.CLEAN || context.getBuildType() == BuildType.RECOVERY) {
            cleanSupport.fullClean(context, progress.newChild(1));
            // invoke clean build on main project src-gen
            super.build(context, progress.newChild(1));
            if (context.getBuildType() == BuildType.CLEAN) {
                // work 2 unit if clean build is performed (xmi build, and
                // ensure)
                progress.worked(2);
                return;
            }
        } else {
            ensureSupport.clean();
            cleanSupport.normalClean(context, relevantDeltas, progress.newChild(1));
        }
        super.build(context, progress.newChild(1));
        // normal cleanUp and codegen done on every delta, do XMI Model build
        xmiModelSupport.build(relevantDeltas.get(0), context, progress.newChild(1));
        // normal code generation done, extensions, packages ready to add to the
        // plug-ins
        ensureSupport.ensure(modelProject, progress.newChild(1));
    }

    @Override
    protected void handleChangedContents(Delta delta, IBuildContext context,
            EclipseResourceFileSystemAccess2 fileSystemAccess) throws CoreException {
        // TODO: we will run out of memory here if the number of deltas is large
        // enough
        Resource deltaResource = context.getResourceSet().getResource(delta.getUri(), true);
        if (shouldGenerate(deltaResource, context)) {
            try {
                // do inferred jvm model to code transformation
                generator.doGenerate(deltaResource, fileSystemAccess);
                doPostGenerate(deltaResource, context);
            } catch (RuntimeException e) {
                if (e.getCause() instanceof CoreException) {
                    throw (CoreException) e.getCause();
                }
                throw e;
            }
        }
    }

    /**
     * From all {@link Pattern} instance in the current deltaResource, computes various additions to the modelProject,
     * and executes the provided fragments. Various contribution: package export, MatcherFactory extension, validation
     * constraint stuff.
     * 
     * 
     * @param deltaResource
     * @param context
     * @throws CoreException
     */
    private void doPostGenerate(Resource deltaResource, IBuildContext context) throws CoreException {
        final IProject project = context.getBuiltProject();
        ExtensionGenerator extGenerator = new ExtensionGenerator();
        extGenerator.setProject(project);
        calculateEMFModelProjects(deltaResource, project);
        TreeIterator<EObject> it = deltaResource.getAllContents();
        while (it.hasNext()) {
            EObject obj = it.next();
            if (obj instanceof Pattern && !CorePatternLanguageHelper.isPrivate((Pattern) obj)) {
                Pattern pattern = (Pattern) obj;

                Iterable<IPluginExtension> matcherFactoryExtensionContribution = matcherFactoryExtensionGenerator
                        .extensionContribution(pattern, extGenerator);
                ensureSupport.appendAllExtension(project, matcherFactoryExtensionContribution);

                for (PatternBody patternBody : pattern.getBodies()) {
                    for (Constraint constraint : patternBody.getConstraints()) {
                        if (constraint instanceof CheckConstraint) {
                            CheckConstraint checkConstraint = (CheckConstraint) constraint;
                            XExpression xExpression = checkConstraint.getExpression();
                            String expressionID = CheckExpressionUtil.getExpressionUniqueID(pattern, xExpression);
                            String expressionUniqueNameInPattern = CheckExpressionUtil
                                    .getExpressionUniqueNameInPattern(pattern, xExpression);
                            Iterable<IPluginExtension> xExpressionEvaluatorExtensionContribution = xExpressionEvaluatorExtensionGenerator
                                    .extensionContribution(pattern, expressionID, expressionUniqueNameInPattern,
                                            extGenerator);
                            ensureSupport.appendAllExtension(project, xExpressionEvaluatorExtensionContribution);
                        }
                    }
                }

                executeGeneratorFragments(context.getBuiltProject(), pattern);
                ensureSupport.exportPackage(project, util.getPackageName(pattern));
            }
        }
    }

    private void calculateEMFModelProjects(Resource deltaResource, IProject project) {
        TreeIterator<EObject> it = deltaResource.getAllContents();
        while (it.hasNext()) {
            EObject obj = it.next();
            if (obj instanceof PatternModel) {
                PatternModel patternModel = (PatternModel) obj;
                for (PackageImport packageImport : EMFPatternLanguageHelper.getPackageImportsIterable(patternModel)) {
                    GenPackage genPackage = genmodelProvider.findGenPackage(packageImport, packageImport.getEPackage());
                    if (genPackage != null) {
                        String modelPluginID = genPackage.getGenModel().getModelPluginID();
                        if (modelPluginID != null && !modelPluginID.isEmpty()) {
                            ensureSupport.addModelBundleId(project, modelPluginID);
                        }
                    }
                }
                it.prune();
            }
        }
    }

    /**
     * Executes all {@link IGenerationFragment} provided for the current {@link Pattern}.
     * 
     * @param modelProject
     * @param pattern
     * @throws CoreException
     */
    private void executeGeneratorFragments(IProject modelProject, Pattern pattern) throws CoreException {
        for (IGenerationFragment fragment : fragmentProvider.getFragmentsForPattern(pattern)) {
            try {
                injector.injectMembers(fragment);
                executeGeneratorFragment(fragment, modelProject, pattern);
            } catch (Exception e) {
                String msg = String.format("Exception when executing generation for '%s' in fragment '%s'",
                        CorePatternLanguageHelper.getFullyQualifiedName(pattern), fragment.getClass()
                                .getCanonicalName());
                logger.error(msg, e);
            }
        }
    }

    private void executeGeneratorFragment(IGenerationFragment fragment, IProject modelProject, Pattern pattern)
            throws CoreException {
        IProject targetProject = createOrGetTargetProject(modelProject, fragment);
        EclipseResourceFileSystemAccess2 fsa = eclipseResourceSupport.createProjectFileSystemAccess(targetProject);
        fragment.generateFiles(pattern, fsa);
        // Generating Eclipse extensions
        ExtensionGenerator exGenerator = new ExtensionGenerator();
        exGenerator.setProject(targetProject);
        Iterable<IPluginExtension> extensionContribution = fragment.extensionContribution(pattern, exGenerator);
        // Gathering all registered extensions together to avoid unnecessary
        // plugin.xml modifications
        // Both for performance and for avoiding race conditions
        ensureSupport.appendAllExtension(targetProject, extensionContribution);
    }

    /**
     * Creates or finds {@link IProject} associated with the {@link IGenerationFragment}. If the project exist
     * dependencies ensured based on the {@link IGenerationFragment} contribution. If the project not exist, it will be
     * initialized.
     * 
     * @param modelProject
     * @param fragment
     * @return
     * @throws CoreException
     */
    private IProject createOrGetTargetProject(IProject modelProject, IGenerationFragment fragment) throws CoreException {
        String postfix = fragment.getProjectPostfix();
        String modelProjectName = ProjectGenerationHelper.getBundleSymbolicName(modelProject);
        if (postfix == null || postfix.isEmpty()) {
            ProjectGenerationHelper.ensureBundleDependencies(modelProject,
                    Lists.newArrayList(fragment.getProjectDependencies()));
            return modelProject;
        } else {
            List<String> dependencies = Lists.newArrayList();
            dependencies.add(modelProjectName);
            dependencies.addAll(ensureSupport.getModelBundleDependencies(modelProject));
            dependencies.addAll(Lists.newArrayList(fragment.getProjectDependencies()));
            IProject targetProject = fragmentProvider.getFragmentProject(modelProject, fragment);
            if (!targetProject.exists()) {
                ProjectGenerationHelper.initializePluginProject(targetProject, dependencies,
                        fragment.getAdditionalBinIncludes());
            } else {
                ProjectGenerationHelper.ensureBundleDependencies(targetProject, dependencies);
            }
            return targetProject;
        }
    }

    @Override
    protected boolean shouldGenerate(Resource resource, IBuildContext context) {
        try {
            Iterable<Pair<IStorage, IProject>> storages = storage2UriMapper.getStorages(resource.getURI());
            for (Pair<IStorage, IProject> pair : storages) {
                if (pair.getFirst() instanceof IFile && pair.getSecond().equals(context.getBuiltProject())) {
                    IFile file = (IFile) pair.getFirst();
                    return file.findMaxProblemSeverity("org.eclipse.xtext.ui.check", true, IResource.DEPTH_INFINITE) != IMarker.SEVERITY_ERROR;
                }
            }
            return false;
        } catch (CoreException exc) {
            throw new WrappedException(exc);
        }
    }
}
