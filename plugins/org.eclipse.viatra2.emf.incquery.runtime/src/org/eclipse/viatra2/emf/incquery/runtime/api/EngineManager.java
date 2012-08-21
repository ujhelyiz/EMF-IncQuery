/*******************************************************************************
 * Copyright (c) 2004-2008 Gabor Bergmann and Daniel Varro
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Gabor Bergmann - initial API and implementation
 *******************************************************************************/

package org.eclipse.viatra2.emf.incquery.runtime.api;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.viatra2.emf.incquery.runtime.exception.IncQueryException;
import org.eclipse.viatra2.emf.incquery.runtime.internal.BaseIndexListener;



/**
 * Global registry of active EMF-IncQuery engines.
 * @author Bergmann Gábor
 *
 */
public class EngineManager {
	private static EngineManager instance = new EngineManager();

	/**
	 * @return the singleton instance
	 */
	public static EngineManager getInstance() {
		return instance;
	}
	/**
	 * Only a weak reference is kept on engine, 
	 * so that it can be GC'ed if the model becomes unreachable.
	 * 
	 * <p>it will not be GC'ed before because of {@link BaseIndexListener#iqEngine}
	 */
	Map<Notifier, WeakReference<IncQueryEngine>> engines;
	
	EngineManager() {
		super();
		engines = new WeakHashMap<Notifier, WeakReference<IncQueryEngine>>();
	}
	
	/**
	 * Creates an EMF-IncQuery engine at an EMF model root (recommended: Resource or ResourceSet) or retrieves an already existing one. 
	 * 
	 * The scope of pattern matching will be the given EMF model root and below (see FAQ for more precise definition). 
	 * The match set of any patterns will be incrementally refreshed upon updates from this scope.
	 * 
	 * @param emfRoot the root of the EMF containment hierarchy where this engine should operate. Recommended: Resource or ResourceSet.
	 * @return a new or previously existing engine
	 * @throws IncQueryException
	 */
	public IncQueryEngine getIncQueryEngine(Notifier emfRoot) throws IncQueryException {
		IncQueryEngine engine = getEngineInternal(emfRoot);
		if (engine == null) {	
			engine = new IncQueryEngine(this, emfRoot);
			engines.put(emfRoot, new WeakReference<IncQueryEngine>(engine));
		}
		return engine;
	}

	/**
	 * Retrieves an already existing EMF-IncQuery engine. 
	 * 
	 * @param emfRoot the root of the EMF containment hierarchy where this engine operates.
	 * @return a previously existing engine, or null if no engine is active for the given EMF model root
	 */
	public IncQueryEngine getIncQueryEngineIfExists(Notifier emfRoot) {
		return getEngineInternal(emfRoot);
	}

	/**
   * Creates an EMF-IncQuery engine at an EMF model root (recommended: Resource or ResourceSet) even if there is an existing one. 
   * 
   * The scope of pattern matching will be the given EMF model root and below (see FAQ for more precise definition). 
   * The match set of any patterns will be incrementally refreshed upon updates from this scope.
   * 
   * @param emfRoot the root of the EMF containment hierarchy where this engine should operate. Recommended: Resource or ResourceSet.
   * @return a new existing engine
   * @throws IncQueryException
   */
	public IncQueryEngine getUnmanagedIncQueryEngine(Notifier emfRoot) throws IncQueryException {
	  return new IncQueryEngine(null, emfRoot);
	}

	/**
	 * Disconnects the engine that was previously attached at the given EMF model root. 
	 * Matcher objects will continue to return stale results. 
	 * If no references are retained to the matchers or the engine, they can eventually be GC'ed, 
	 * 	and they won't block the EMF model from being GC'ed anymore. 
	 * @return true is an engine was found and disconnected, false if no engine was found for the given root.
	 */
	public boolean disposeEngine(Notifier emfRoot) {
		IncQueryEngine engine = getEngineInternal(emfRoot);
		if (engine == null) return false;
		else {
			engine.dispose();
			return true;
		}
	}

	/**
	 * @param emfRoot
	 */
	void killInternal(Notifier emfRoot) {
		engines.remove(emfRoot);
	}
	/**
	 * @param emfRoot
	 * @return
	 */
	private IncQueryEngine getEngineInternal(Notifier emfRoot) {
		final WeakReference<IncQueryEngine> engineRef = engines.get(emfRoot);
		IncQueryEngine engine = engineRef == null ? null : engineRef.get();
		return engine;
	}
	
}
