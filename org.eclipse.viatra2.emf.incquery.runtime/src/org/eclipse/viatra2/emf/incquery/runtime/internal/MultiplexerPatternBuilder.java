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

package org.eclipse.viatra2.emf.incquery.runtime.internal;

import java.util.HashMap;

import org.eclipse.viatra2.emf.incquery.runtime.extensibility.BuilderRegistry;
import org.eclipse.viatra2.emf.incquery.runtime.extensibility.IStatelessRetePatternBuilder;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.IRetePatternBuilder;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.ReteContainerBuildable;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.RetePatternBuildException;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.matcher.IPatternMatcherContext;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Receiver;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.network.Supplier;
import org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.remote.Address;

/**
 * Internal RetePatternBuilder that multiplexes build requests to contributions to the BuilderRegistry.
 * Multiplexation is keyed by pattern fqn.
 * @author Bergmann Gábor
 *
 */
public class MultiplexerPatternBuilder implements
		IRetePatternBuilder<String, Address<? extends Supplier>, Address<? extends Receiver>>
{
	ReteContainerBuildable<String> baseBuildable;
	IPatternMatcherContext<String> context;

	/**
	 * @param baseBuildable
	 * @param context
	 */
	public MultiplexerPatternBuilder(ReteContainerBuildable<String> baseBuildable,
			IPatternMatcherContext<String> context) {
		super();
		this.baseBuildable = baseBuildable;
		this.context = context;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.IRetePatternBuilder#construct(java.lang.Object)
	 */
	@Override
	public Address<? extends Receiver> construct(String gtPattern)
			throws RetePatternBuildException {
		IStatelessRetePatternBuilder builder = BuilderRegistry.getContributedStatelessPatternBuilders().get(gtPattern);
		if (builder != null) return builder.construct(baseBuildable, context, gtPattern);
		else throw new RetePatternBuildException("No RETE pattern builder generated for pattern {1}.",
				new String[]{gtPattern}, gtPattern);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.IRetePatternBuilder#getPosMapping(java.lang.Object)
	 */
	@Override
	public HashMap<Object, Integer> getPosMapping(String gtPattern) {
		IStatelessRetePatternBuilder builder = BuilderRegistry.getContributedStatelessPatternBuilders().get(gtPattern);
		if (builder != null) return builder.getPosMapping(gtPattern); else return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.IRetePatternBuilder#refresh()
	 */
	@Override
	public void refresh() {
		throw new UnsupportedOperationException();
	}

//	/* (non-Javadoc)
//	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.IRetePatternBuilder#getBuildable()
//	 */
//	@Override
//	public Buildable<String, Address<? extends Supplier>, Address<? extends Receiver>> getBuildable() {
//		return baseBuildable;
//	}

	/* (non-Javadoc)
	 * @see org.eclipse.viatra2.gtasm.patternmatcher.incremental.rete.construction.IRetePatternBuilder#getContext()
	 */
	@Override
	public IPatternMatcherContext<String> getContext() {
		return context;
	}

}