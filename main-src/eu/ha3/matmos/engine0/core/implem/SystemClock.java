package eu.ha3.matmos.engine0.core.implem;

import eu.ha3.matmos.engine0.core.interfaces.ReferenceTime;

/* x-placeholder */

public class SystemClock implements ReferenceTime
{
	@Override
	public long getMilliseconds()
	{
		return System.currentTimeMillis();
	}
}
