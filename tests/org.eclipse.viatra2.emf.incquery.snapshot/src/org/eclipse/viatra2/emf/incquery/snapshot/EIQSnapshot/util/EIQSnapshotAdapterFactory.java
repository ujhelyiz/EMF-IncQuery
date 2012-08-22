/*******************************************************************************
 * Copyright (c) 2010-2012, Gabor Bergmann, Abel Hegedus, Istvan Rath and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Gabor Bergmann, Abel Hegedus - initial API and implementation
 *******************************************************************************/

/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.EIQSnapshotPackage
 * @generated
 */
public class EIQSnapshotAdapterFactory extends AdapterFactoryImpl {
	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static EIQSnapshotPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EIQSnapshotAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = EIQSnapshotPackage.eINSTANCE;
		}
	}

	/**
	 * Returns whether this factory is applicable for the type of the object.
	 * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
	 * <!-- end-user-doc -->
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object object) {
		if (object == modelPackage) {
			return true;
		}
		if (object instanceof EObject) {
			return ((EObject)object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/**
	 * The switch that delegates to the <code>createXXX</code> methods.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EIQSnapshotSwitch<Adapter> modelSwitch =
		new EIQSnapshotSwitch<Adapter>() {
			@Override
			public Adapter caseMatchSetRecord(MatchSetRecord object) {
				return createMatchSetRecordAdapter();
			}
			@Override
			public Adapter caseMatchRecord(MatchRecord object) {
				return createMatchRecordAdapter();
			}
			@Override
			public Adapter caseMatchSubstitutionRecord(MatchSubstitutionRecord object) {
				return createMatchSubstitutionRecordAdapter();
			}
			@Override
			public Adapter caseEMFSubstitution(EMFSubstitution object) {
				return createEMFSubstitutionAdapter();
			}
			@Override
			public Adapter caseIntSubstitution(IntSubstitution object) {
				return createIntSubstitutionAdapter();
			}
			@Override
			public Adapter caseLongSubstitution(LongSubstitution object) {
				return createLongSubstitutionAdapter();
			}
			@Override
			public Adapter caseDoubleSubstitution(DoubleSubstitution object) {
				return createDoubleSubstitutionAdapter();
			}
			@Override
			public Adapter caseFloatSubstitution(FloatSubstitution object) {
				return createFloatSubstitutionAdapter();
			}
			@Override
			public Adapter caseBooleanSubstitution(BooleanSubstitution object) {
				return createBooleanSubstitutionAdapter();
			}
			@Override
			public Adapter caseStringSubstitution(StringSubstitution object) {
				return createStringSubstitutionAdapter();
			}
			@Override
			public Adapter caseDateSubstitution(DateSubstitution object) {
				return createDateSubstitutionAdapter();
			}
			@Override
			public Adapter caseEnumSubstitution(EnumSubstitution object) {
				return createEnumSubstitutionAdapter();
			}
			@Override
			public Adapter caseMiscellaneousSubstitution(MiscellaneousSubstitution object) {
				return createMiscellaneousSubstitutionAdapter();
			}
			@Override
			public Adapter caseIncQuerySnapshot(IncQuerySnapshot object) {
				return createIncQuerySnapshotAdapter();
			}
			@Override
			public Adapter defaultCase(EObject object) {
				return createEObjectAdapter();
			}
		};

	/**
	 * Creates an adapter for the <code>target</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param target the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
	@Override
	public Adapter createAdapter(Notifier target) {
		return modelSwitch.doSwitch((EObject)target);
	}


	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchSetRecord <em>Match Set Record</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchSetRecord
	 * @generated
	 */
	public Adapter createMatchSetRecordAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchRecord <em>Match Record</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchRecord
	 * @generated
	 */
	public Adapter createMatchRecordAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchSubstitutionRecord <em>Match Substitution Record</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MatchSubstitutionRecord
	 * @generated
	 */
	public Adapter createMatchSubstitutionRecordAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.EMFSubstitution <em>EMF Substitution</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.EMFSubstitution
	 * @generated
	 */
	public Adapter createEMFSubstitutionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.IntSubstitution <em>Int Substitution</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.IntSubstitution
	 * @generated
	 */
	public Adapter createIntSubstitutionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.LongSubstitution <em>Long Substitution</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.LongSubstitution
	 * @generated
	 */
	public Adapter createLongSubstitutionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.DoubleSubstitution <em>Double Substitution</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.DoubleSubstitution
	 * @generated
	 */
	public Adapter createDoubleSubstitutionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.FloatSubstitution <em>Float Substitution</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.FloatSubstitution
	 * @generated
	 */
	public Adapter createFloatSubstitutionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.BooleanSubstitution <em>Boolean Substitution</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.BooleanSubstitution
	 * @generated
	 */
	public Adapter createBooleanSubstitutionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.StringSubstitution <em>String Substitution</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.StringSubstitution
	 * @generated
	 */
	public Adapter createStringSubstitutionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.DateSubstitution <em>Date Substitution</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.DateSubstitution
	 * @generated
	 */
	public Adapter createDateSubstitutionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.EnumSubstitution <em>Enum Substitution</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.EnumSubstitution
	 * @generated
	 */
	public Adapter createEnumSubstitutionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MiscellaneousSubstitution <em>Miscellaneous Substitution</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.MiscellaneousSubstitution
	 * @generated
	 */
	public Adapter createMiscellaneousSubstitutionAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.IncQuerySnapshot <em>Inc Query Snapshot</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.eclipse.viatra2.emf.incquery.snapshot.EIQSnapshot.IncQuerySnapshot
	 * @generated
	 */
	public Adapter createIncQuerySnapshotAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for the default case.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @generated
	 */
	public Adapter createEObjectAdapter() {
		return null;
	}

} //EIQSnapshotAdapterFactory
