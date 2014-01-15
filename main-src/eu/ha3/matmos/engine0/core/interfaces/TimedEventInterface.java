package eu.ha3.matmos.engine0.core.interfaces;

import eu.ha3.matmos.engine0.core.implem.Event;

/*
--filenotes-placeholder
*/

public interface TimedEventInterface
{
	public void restart(ReferenceTime time);
	
	public void play(Provider<Event> eventProvider, ReferenceTime time, float fadeFactor);
}