package org.eclipse.viatra2.emf.incquery.triggerengine.firing;

import org.eclipse.viatra2.emf.incquery.triggerengine.ActivationMonitor;

/**
 * Classes implement this interface to provide notifications about the changes in the 
 * collection of activations within an agenda. 
 * 
 * @author Tamas Szabo
 *
 */
public interface ActivationNotificationProvider {
	
	/**
	 * Registers a listener that will be called each time the set of activations is modified.
	 * 
	 * @param listener a ActivationNotificationListener to be called after each update.
	 * 
	 * @return false if the callback was already registered.
	 */
	public boolean addActivationNotificationListener(ActivationNotificationListener listener);
	
	/**
	 * Removes a previously registered listener. See addActivationNotificationListener().
	 * 
	 * @param listener the listener to remove
	 * 
	 * @return false if the callback was not registered.
	 */
	public boolean removeActivationNotificationListener(ActivationNotificationListener listener);

	/**
	 * Instantiates a new {@link ActivationMonitor} that will keep track of the changes of activations.
	 * 
	 * @param fillAtStart indicates whether to initialize the monitor with the activations during creation
	 * 
	 * @return the {@link ActivationMonitor} instance
	 */
	public ActivationMonitor newActivationMonitor(boolean fillAtStart);
}
