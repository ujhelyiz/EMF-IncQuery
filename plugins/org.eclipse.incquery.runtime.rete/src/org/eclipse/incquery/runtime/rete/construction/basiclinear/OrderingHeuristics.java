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

package org.eclipse.incquery.runtime.rete.construction.basiclinear;

import java.util.Comparator;
import java.util.Set;

import org.eclipse.incquery.runtime.rete.collections.CollectionsFactory;
import org.eclipse.incquery.runtime.rete.construction.Stub;
import org.eclipse.incquery.runtime.rete.construction.psystem.DeferredPConstraint;
import org.eclipse.incquery.runtime.rete.construction.psystem.EnumerablePConstraint;
import org.eclipse.incquery.runtime.rete.construction.psystem.PConstraint;
import org.eclipse.incquery.runtime.rete.construction.psystem.PVariable;
import org.eclipse.incquery.runtime.rete.construction.psystem.basicenumerables.ConstantValue;
import org.eclipse.incquery.runtime.rete.util.OrderingCompareAgent;

/**
 * @author Bergmann Gábor
 * 
 */
public class OrderingHeuristics<PatternDescription, StubHandle, Collector> implements Comparator<PConstraint> {
    private Stub<StubHandle> stub;

    /**
     * @param stub
     */
    public OrderingHeuristics(Stub<StubHandle> stub) {
        super();
        this.stub = stub;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(PConstraint o1, PConstraint o2) {
        return new OrderingCompareAgent<PConstraint>(o1, o2) {
            @Override
            protected void doCompare() {
                boolean temp = true && consider(preferTrue(isConstant(a), isConstant(b)))
                        && consider(preferTrue(isReady(a), isReady(b)));
                if (!temp)
                    return;

                Set<PVariable> bound1 = boundVariables(a);
                Set<PVariable> bound2 = boundVariables(b);
                swallowBoolean(temp && consider(preferTrue(isBound(a, bound1), isBound(b, bound2)))
                        && consider(preferMore(degreeBound(a, bound1), degreeBound(b, bound2)))
                        && consider(preferLess(degreeFree(a, bound1), degreeFree(b, bound2)))

                        && consider(preferLess(a.toString(), b.toString())));
            }
        }.compare();
    }

    boolean isConstant(PConstraint o) {
        return (o instanceof ConstantValue<?, ?>);
    }

    boolean isReady(PConstraint o) {
        return (o instanceof EnumerablePConstraint<?, ?>)
                || (o instanceof DeferredPConstraint<?, ?> && ((DeferredPConstraint<PatternDescription, StubHandle>) o)
                        .isReadyAt(stub));
    }

    Set<PVariable> boundVariables(PConstraint o) {
        Set<PVariable> boundVariables = CollectionsFactory.getSet();//new HashSet<PVariable>(o.getAffectedVariables());
        boundVariables.retainAll(stub.getVariablesIndex().keySet());
        return boundVariables;
    }

    boolean isBound(PConstraint o, Set<PVariable> boundVariables) {
        return boundVariables.size() == o.getAffectedVariables().size();
    }

    int degreeBound(PConstraint o, Set<PVariable> boundVariables) {
        return boundVariables.size();
    }

    int degreeFree(PConstraint o, Set<PVariable> boundVariables) {
        return o.getAffectedVariables().size() - boundVariables.size();
    }

}
