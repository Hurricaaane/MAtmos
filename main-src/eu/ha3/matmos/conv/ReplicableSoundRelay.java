package eu.ha3.matmos.conv;

import eu.ha3.matmos.engine.interfaces.SoundRelay;

/* x-placeholder */

public interface ReplicableSoundRelay extends SoundRelay
{
	public SoundRelay createChild();
}
