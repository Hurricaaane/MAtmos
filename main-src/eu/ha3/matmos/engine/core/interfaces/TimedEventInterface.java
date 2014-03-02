package eu.ha3.matmos.engine.core.interfaces;

/*
--filenotes-placeholder
*/

public interface TimedEventInterface
{
	public void restart(ReferenceTime time);
	
	public void play(ReferenceTime time, float fadeFactor);
}