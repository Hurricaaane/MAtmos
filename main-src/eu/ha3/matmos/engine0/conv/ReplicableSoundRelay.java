package eu.ha3.matmos.engine0.conv;

import eu.ha3.matmos.engine0.core.interfaces.SoundRelay;

/* x-placeholder */

public interface ReplicableSoundRelay extends SoundRelay
{
	public SoundRelay createChild();
}
