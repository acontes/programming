package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed;

import java.util.Observer;

public interface IObservable {
	public void addObserver(Observer o);
	public void notifyObservers(Object arg);
}
