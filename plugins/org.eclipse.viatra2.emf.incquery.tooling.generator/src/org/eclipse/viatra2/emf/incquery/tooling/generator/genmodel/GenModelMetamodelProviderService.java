package org.eclipse.viatra2.emf.incquery.tooling.generator.genmodel;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.viatra2.emf.incquery.core.project.ProjectGenerationHelper;
import org.eclipse.viatra2.emf.incquery.tooling.generator.generatorModel.GeneratorModelReference;
import org.eclipse.viatra2.emf.incquery.tooling.generator.generatorModel.IncQueryGeneratorModel;
import org.eclipse.viatra2.patternlanguage.scoping.MetamodelProviderService;
import org.eclipse.xtext.common.types.access.jdt.IJavaProjectProvider;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.SimpleScope;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

public class GenModelMetamodelProviderService extends MetamodelProviderService
		implements IEiqGenmodelProvider {

	@Inject
	IJavaProjectProvider projectProvider;
	@Inject
	IQualifiedNameConverter qualifiedNameConverter;

	@Override
	public IScope getAllMetamodelObjects(EObject ctx) {
		Iterable<IEObjectDescription> referencedPackages = Lists.newArrayList();
		try {
			IncQueryGeneratorModel generatorModel = getGeneratorModel(ctx);
			for (GeneratorModelReference ref : generatorModel.getGenmodels()) {

				Iterable<IEObjectDescription> packages = Iterables.transform(
						ref.getGenmodel().getGenPackages(),
						new Function<GenPackage, IEObjectDescription>() {
							public IEObjectDescription apply(GenPackage from) {
								EPackage ePackage = from.getEcorePackage();
								QualifiedName qualifiedName = qualifiedNameConverter
										.toQualifiedName(ePackage.getNsURI());
								return EObjectDescription.create(qualifiedName,
										ePackage, Collections.singletonMap(
												"nsURI", "true"));
							}
						});
				referencedPackages = Iterables.concat(referencedPackages,
						packages);
			}
		} catch (IllegalArgumentException e) {
			//TODO logging needed
			e.printStackTrace();
		}
		return new SimpleScope(super.getAllMetamodelObjects(ctx),
				referencedPackages);
	}

	@Override
	public EPackage loadEPackage(final String packageUri, ResourceSet set) {
		if (set != null && projectProvider != null) {
			IJavaProject javaProject = projectProvider.getJavaProject(set);
			IncQueryGeneratorModel generatorModel = getGeneratorModel(
					javaProject.getProject(), set);
			for (GeneratorModelReference ref : generatorModel.getGenmodels()) {

				Iterable<GenPackage> genPackages = Iterables.filter(ref
						.getGenmodel().getGenPackages(),
						new Predicate<GenPackage>() {
							public boolean apply(GenPackage genPackage) {
								return packageUri.equals(genPackage
										.getEcorePackage().getNsURI());
							}
						});
				Iterator<GenPackage> iterator = genPackages.iterator();
				if (iterator.hasNext()) {
					GenPackage genPackage = iterator.next();
					return genPackage.getEcorePackage();
				}
			}
		}
		return super.loadEPackage(packageUri, set);
	}

	@Override
	public IncQueryGeneratorModel getGeneratorModel(EObject pattern)
			throws IllegalArgumentException {
		Resource res = pattern.eResource();
		if (res != null && projectProvider != null) {
			ResourceSet set = res.getResourceSet();
			IJavaProject javaProject = projectProvider.getJavaProject(set);
			return getGeneratorModel(javaProject.getProject(), set);
		}
		throw new IllegalArgumentException(
				"The project of the context cannot be determined.");
	}

	public IncQueryGeneratorModel getGeneratorModel(IProject project) {
		return getGeneratorModel(project, new ResourceSetImpl());
	}

	@Override
	public IncQueryGeneratorModel getGeneratorModel(IProject project,
			ResourceSet set) {
		return ProjectGenerationHelper.getGeneratorModel(project, set);
	}

	@Override
	public GenPackage findGenPackage(EObject ctx, final EPackage ePackage) {
		IncQueryGeneratorModel eiqGenModel = getGeneratorModel(ctx);
		Iterable<GenPackage> genPackageIterable = Lists.newArrayList();
		for (GeneratorModelReference genModel : eiqGenModel.getGenmodels()) {
			Iterables.concat(genPackageIterable, genModel.getGenmodel().getGenPackages());
		}
		GenPackage genPackage = Iterables.find(genPackageIterable, new Predicate<GenPackage>() {
			public boolean apply(GenPackage genPackage) {
				return ePackage.equals(genPackage.getEcorePackage());
			}
		});
		return genPackage;
	}

	@Override
	public String getProperty(IncQueryGeneratorModel model, String categoryID,
			String propertyID) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public Map<String, String> getAllPropertiesOfCategory(
			IncQueryGeneratorModel model, String categoryID) {
		// TODO Auto-generated method stub
		return Maps.newHashMap();
	}

}
