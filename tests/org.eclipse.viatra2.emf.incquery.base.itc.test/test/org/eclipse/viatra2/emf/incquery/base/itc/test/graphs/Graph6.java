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

package org.eclipse.viatra2.emf.incquery.base.itc.test.graphs;

import org.eclipse.viatra2.emf.incquery.base.itc.alg.misc.Tuple;
import org.eclipse.viatra2.emf.incquery.base.itc.test.misc.TestObserver;


public class Graph6 extends TestGraph<Integer> {

	private static final long serialVersionUID = -3807323812221410872L;
	
	public Graph6() {
		super(new TestObserver());
	}
	
	public void modify() {
		Integer n1 = Integer.valueOf(1);
		Integer n2 = Integer.valueOf(2);
		Integer n3 = Integer.valueOf(3);
		Integer n4 = Integer.valueOf(4);
		Integer n5 = Integer.valueOf(5);
		Integer n6 = Integer.valueOf(6);
		Integer n7 = Integer.valueOf(7);
		
		this.insertNode(n1);
		this.insertNode(n2);
		this.insertNode(n3);
		this.insertNode(n4);
		this.insertNode(n5);
		this.insertNode(n6);
		this.insertNode(n7);
		
		this.observer.getTuples().clear();
		this.observer.getTuples().add(new Tuple<Integer>(n1, n2));
		this.insertEdge(n1, n2);
		
		this.observer.getTuples().clear();
		this.observer.getTuples().add(new Tuple<Integer>(n4, n5));
		this.insertEdge(n4, n5);
		
		this.observer.getTuples().clear();
		this.observer.getTuples().add(new Tuple<Integer>(n2, n4));
		this.observer.getTuples().add(new Tuple<Integer>(n2, n5));
		this.observer.getTuples().add(new Tuple<Integer>(n1, n4));
		this.observer.getTuples().add(new Tuple<Integer>(n1, n5));
		this.insertEdge(n2, n4);

		this.observer.getTuples().clear();
		this.observer.getTuples().add(new Tuple<Integer>(n3, n5));
		this.insertEdge(n3, n5);
		
		this.observer.getTuples().clear();
		this.observer.getTuples().add(new Tuple<Integer>(n5, n6));
		this.observer.getTuples().add(new Tuple<Integer>(n4, n6));
		this.observer.getTuples().add(new Tuple<Integer>(n3, n6));
		this.observer.getTuples().add(new Tuple<Integer>(n2, n6));
		this.observer.getTuples().add(new Tuple<Integer>(n1, n6));
		this.insertEdge(n5, n6);
		
		this.observer.getTuples().clear();
		this.observer.getTuples().add(new Tuple<Integer>(n6, n7));
		this.observer.getTuples().add(new Tuple<Integer>(n5, n7));
		this.observer.getTuples().add(new Tuple<Integer>(n4, n7));
		this.observer.getTuples().add(new Tuple<Integer>(n3, n7));
		this.observer.getTuples().add(new Tuple<Integer>(n2, n7));
		this.observer.getTuples().add(new Tuple<Integer>(n1, n7));
		this.insertEdge(n6, n7);
		
		this.observer.getTuples().clear();
		this.observer.getTuples().add(new Tuple<Integer>(n2, n3));
		this.observer.getTuples().add(new Tuple<Integer>(n1, n3));
		this.insertEdge(n2, n3);
		
		this.observer.getTuples().clear();
		this.observer.getTuples().add(new Tuple<Integer>(n2, n3));
		this.observer.getTuples().add(new Tuple<Integer>(n1, n3));
		this.observer.getTuples().add(new Tuple<Integer>(n3, n5));
		this.observer.getTuples().add(new Tuple<Integer>(n3, n6));
		this.observer.getTuples().add(new Tuple<Integer>(n3, n7));
		this.insertEdge(n3, n5);
		
		this.observer.getTuples().clear();
		this.observer.getTuples().add(new Tuple<Integer>(n1, n5));
		this.observer.getTuples().add(new Tuple<Integer>(n1, n6));
		this.observer.getTuples().add(new Tuple<Integer>(n1, n7));
		this.observer.getTuples().add(new Tuple<Integer>(n2, n5));
		this.observer.getTuples().add(new Tuple<Integer>(n2, n6));
		this.observer.getTuples().add(new Tuple<Integer>(n2, n7));
		this.observer.getTuples().add(new Tuple<Integer>(n4, n5));
		this.observer.getTuples().add(new Tuple<Integer>(n4, n6));
		this.observer.getTuples().add(new Tuple<Integer>(n4, n7));
		this.insertEdge(n4, n5);
	}
}
