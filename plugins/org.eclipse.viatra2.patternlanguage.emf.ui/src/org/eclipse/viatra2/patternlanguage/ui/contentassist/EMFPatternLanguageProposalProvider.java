/*******************************************************************************
 * Copyright (c) 2010-2012, Zoltan Ujhelyi, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Zoltan Ujhelyi - initial API and implementation
 *******************************************************************************/
package org.eclipse.viatra2.patternlanguage.ui.contentassist;

import static org.eclipse.emf.ecore.util.EcoreUtil.getRootContainer;

import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.viatra2.patternlanguage.EMFPatternLanguageScopeHelper;
import org.eclipse.viatra2.patternlanguage.ResolutionException;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionElement;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PathExpressionHead;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Pattern;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.PatternBody;
import org.eclipse.viatra2.patternlanguage.core.patternLanguage.Variable;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.ClassType;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.EMFPatternLanguagePackage;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PackageImport;
import org.eclipse.viatra2.patternlanguage.eMFPatternLanguage.PatternModel;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.EnumRule;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.IScopeProvider;
import org.eclipse.xtext.ui.editor.contentassist.ConfigurableCompletionProposal;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor;
import org.eclipse.xtext.ui.editor.contentassist.PrefixMatcher;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
/**
 * see http://www.eclipse.org/Xtext/documentation/latest/xtext.html#contentAssist on how to customize content assistant
 */
public class EMFPatternLanguageProposalProvider extends AbstractEMFPatternLanguageProposalProvider {
	
	private static final Set<String> FILTERED_KEYWORDS = Sets.newHashSet("pattern");
	
	@Inject
	IScopeProvider scopeProvider;
	@Inject
	ReferenceProposalCreator crossReferenceProposalCreator;
	
	public static class ClassifierPrefixMatcher extends PrefixMatcher {
		private PrefixMatcher delegate;

		private IQualifiedNameConverter qualifiedNameConverter;

		
		public ClassifierPrefixMatcher(PrefixMatcher delegate, IQualifiedNameConverter qualifiedNameConverter) {
			this.delegate = delegate;
			this.qualifiedNameConverter = qualifiedNameConverter;
		}

		@Override
		public boolean isCandidateMatchingPrefix(String name, String prefix) {
			if (delegate.isCandidateMatchingPrefix(name, prefix))
				return true;
			QualifiedName qualifiedName = qualifiedNameConverter.toQualifiedName(name);
			QualifiedName qualifiedPrefix = qualifiedNameConverter.toQualifiedName(prefix);
			if (qualifiedName.getSegmentCount() > 1) {
				if (qualifiedPrefix.getSegmentCount() == 1)
					return delegate.isCandidateMatchingPrefix(qualifiedName.getSegment(1),
							qualifiedPrefix.getFirstSegment());
				if (!delegate.isCandidateMatchingPrefix(qualifiedName.getFirstSegment(),
						qualifiedPrefix.getFirstSegment()))
					return false;
				return delegate.isCandidateMatchingPrefix(qualifiedName.getSegment(1), qualifiedPrefix.getSegment(1));
			}
			return false;
		}

	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.xtext.xbase.ui.contentassist.XbaseProposalProvider#completeKeyword(org.eclipse.xtext.Keyword, org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext, org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor)
	 */
	@Override
	public void completeKeyword(Keyword keyword,
			ContentAssistContext contentAssistContext,
			ICompletionProposalAcceptor acceptor) {
		// ignore keywords in FILTERED set
		if (FILTERED_KEYWORDS.contains(keyword.getValue())) {
			return;
		}
		super.completeKeyword(keyword, contentAssistContext, acceptor);
	}	
	
	@Override
	public void complete_ValueReference(EObject model, RuleCall ruleCall,
			ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		super.complete_ValueReference(model, ruleCall, context, acceptor);
		if (model instanceof PathExpressionHead) {
			PathExpressionHead head = (PathExpressionHead) model;
			try {
				//XXX The following code re-specifies scoping instead of reusing the scope provider
				EClassifier typeClassifier = EMFPatternLanguageScopeHelper.calculateExpressionType(head);
				if (typeClassifier instanceof EEnum) {
					//In case of EEnums add Enum Literal constants
					EEnum type = (EEnum) typeClassifier;
					for (EEnumLiteral literal : type.getELiterals()) {
						acceptor.accept(createCompletionProposal(
								"::" + literal.getName(), type.getName() + "::" + literal.getName(),
								null, context));
					}
				}
				//XXX The following code re-specifies scoping instead of reusing the scope provider
				// Always refer to existing variables
				PatternBody body = (PatternBody) head.eContainer()/*PathExpression*/.eContainer()/*PatternBody*/;
				for (Variable var : body.getVariables()) {
					acceptor.accept(createCompletionProposal(var.getName(), context));
				}
				Pattern pattern = (Pattern) body.eContainer();
				for (Variable var : pattern.getParameters()) {
					acceptor.accept(createCompletionProposal(var.getName(), context));
				}
			} catch (ResolutionException e) {
			}
		}
	}

	@Override
	public void completeType_Typename(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		PatternModel pModel = null;
		EObject root = getRootContainer(model);
		if (root instanceof PatternModel){
			pModel = (PatternModel) root;
		}
		ContentAssistContext.Builder myContextBuilder = context.copy();
		myContextBuilder.setMatcher(new ClassifierPrefixMatcher(context.getMatcher(), getQualifiedNameConverter()));
		ClassType type = null;
		if (model instanceof Variable) {
			type = (ClassType) ((Variable) model).getType();
		} else {
			return;
		}
		
		ICompositeNode node = NodeModelUtils.getNode(type);
		int offset = node.getOffset();
		Region replaceRegion = new Region(offset, context.getReplaceRegion()
				.getLength() + context.getReplaceRegion().getOffset() - offset);
		myContextBuilder.setReplaceRegion(replaceRegion);
		myContextBuilder.setLastCompleteNode(node);
		StringBuilder availablePrefix = new StringBuilder(4);
		for (ILeafNode leaf : node.getLeafNodes()) {
			if (leaf.getGrammarElement() != null && !leaf.isHidden()) {
				if ((leaf.getTotalLength() + leaf.getTotalOffset()) < context
						.getOffset())
					availablePrefix.append(leaf.getText());
				else
					availablePrefix.append(leaf.getText().substring(0,
							context.getOffset() - leaf.getTotalOffset()));
			}
			if (leaf.getTotalOffset() >= context.getOffset())
				break;
		}
		myContextBuilder.setPrefix(availablePrefix.toString());
		
		ContentAssistContext myContext = myContextBuilder.toContext();
		for (PackageImport declaration : pModel.getImportPackages()) {
			if (declaration.getEPackage() != null) {
				createClassifierProposals(declaration, model, myContext, acceptor);
			}
		}
	}
	
	private void createClassifierProposals(PackageImport declaration, EObject model,
			ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		//String alias = declaration.getAlias();
		//QualifiedName prefix = (!Strings.isEmpty(alias)) 
		//	? QualifiedName.create(getValueConverter().toString(alias,"ID")) 
		//	: null;
		boolean createDatatypeProposals = modelOrContainerIs(model, Variable.class);
		boolean createEnumProposals = modelOrContainerIs(model, EnumRule.class);
		boolean createClassProposals = modelOrContainerIs(model, Variable.class);
		Function<IEObjectDescription, ICompletionProposal> factory = getProposalFactory(null, context);
		for (EClassifier classifier : declaration.getEPackage().getEClassifiers()) {
			if (classifier instanceof EDataType && createDatatypeProposals || classifier instanceof EEnum
					&& createEnumProposals || classifier instanceof EClass && createClassProposals) {
				String classifierName = getValueConverter().toString(classifier.getName(), "ID");
				QualifiedName proposalQualifiedName = /*(prefix != null) ? prefix.append(classifierName) :*/ QualifiedName
						.create(classifierName);
				IEObjectDescription description = EObjectDescription.create(proposalQualifiedName, classifier);
				ConfigurableCompletionProposal proposal = (ConfigurableCompletionProposal) factory.apply(description);
				if (proposal != null) {
					/*if (prefix != null)
						proposal.setDisplayString(classifier.getName() + " - " + alias);*/
					proposal.setPriority(proposal.getPriority() * 2);
				}
				acceptor.accept(proposal);
			}
		}
	}
	
	private boolean modelOrContainerIs(EObject model, Class<?>... types) {
		for (Class<?> type : types) {
			if (type.isInstance(model) || type.isInstance(model.eContainer()))
				return true;
		}
		return false;
	}

	public void complete_RefType(PathExpressionElement model, RuleCall ruleCall,
			ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		IScope scope = scopeProvider.getScope(model.getTail(),
				EMFPatternLanguagePackage.Literals.REFERENCE_TYPE__REFNAME);
		crossReferenceProposalCreator.lookupCrossReference(scope, model,
				EMFPatternLanguagePackage.Literals.REFERENCE_TYPE__REFNAME,
				acceptor, Predicates.<IEObjectDescription> alwaysTrue(),
				getProposalFactory(ruleCall.getRule().getName(), context));
	}

	@Override
	public void completeRefType_Refname(EObject model, Assignment assignment,
			ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// This method is deliberately empty.
		// This override prohibits the content assist to suggest incorrect parameters. 
	}
	
}
