package eu.ha3.matmos.engine.core.implem;

import eu.ha3.matmos.engine.core.interfaces.ReferenceTime;

/* x-placeholder */

public class SystemClock implements ReferenceTime
{
	@Override
	public long getMilliseconds()
	{
		return System.currentTimeMillis();
	}
}
