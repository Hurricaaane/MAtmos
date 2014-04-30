package eu.ha3.matmos.engine.core.implem;

import eu.ha3.matmos.engine.core.interfaces.ReferenceTime;

/*
--filenotes-placeholder
*/

public class HelperFadeCalculator
{
	private final ReferenceTime time;
	
	private long fadeInTime = 0;
	private long fadeInDuration = 0;
	
	private long fadeOutTime = 0;
	private long fadeOutDuration = 0;
	
	private boolean foLast = true;
	private boolean complete = true;
	private float fade = 0;
	
	public HelperFadeCalculator(ReferenceTime time)
	{
		this.time = time;
	}
	
	public void fadeIn(long durationMs)
	{
		float currentFade = calculateFadeFactor();
		
		long minTime = (long) (currentFade * durationMs);
		
		this.fadeInTime = this.time.getMilliseconds() - minTime;
		this.fadeInDuration = durationMs;
		
		this.foLast = false;
		this.complete = false;
	}
	
	public void fadeOut(long durationMs)
	{
		float currentFade = calculateFadeFactor();
		
		long minTime = (long) (durationMs - durationMs * currentFade);
		
		this.fadeOutTime = this.time.getMilliseconds() - minTime;
		this.fadeOutDuration = durationMs;
		
		this.foLast = true;
		this.complete = false;
	}
	
	public float calculateFadeFactor()
	{
		if (this.complete)
			return this.fade;
		
		long curTime = this.time.getMilliseconds();
		if (this.foLast)
		{
			this.fade = 1 - (curTime - this.fadeOutTime) / (float) this.fadeOutDuration;
			if (this.fade < 0f)
			{
				this.fade = 0f;
				this.complete = true;
			}
		}
		else
		{
			this.fade = (curTime - this.fadeInTime) / (float) this.fadeInDuration;
			if (this.fade > 1f)
			{
				this.fade = 1f;
				this.complete = true;
			}
		}
		
		return this.fade;
	}
}
