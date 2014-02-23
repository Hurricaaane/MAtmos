package eu.ha3.matmos.engine0.core.implem;

import java.util.List;

import eu.ha3.matmos.engine0.core.implem.abstractions.MultistateComponent;
import eu.ha3.matmos.engine0.core.implem.abstractions.Provider;
import eu.ha3.matmos.engine0.core.interfaces.ReferenceTime;
import eu.ha3.matmos.engine0.core.interfaces.Simulated;
import eu.ha3.matmos.engine0.core.interfaces.TimedEventInterface;

/*
--filenotes-placeholder
*/

public class TimedEventInformation extends MultistateComponent implements Simulated
{
	private float delayBeforeFadeIn = 0f;
	private float delayBeforeFadeOut = 0f;
	private float fadeInTime = 0f;
	private float fadeOutTime = 0f;
	
	//
	
	private final String machineName;
	private final Provider<Machine> provider;
	private final ReferenceTime time;
	private List<TimedEvent> events;
	
	private boolean isPlaying;
	private long startTime;
	private long stopTime;
	
	public TimedEventInformation(
		String machineName, Provider<Machine> provider, ReferenceTime time, List<TimedEvent> events,
		float delayBeforeFadeIn, float delayBeforeFadeOut, float fadeInTime, float fadeOutTime)
	{
		super("_TIMED:" + machineName);
		
		this.machineName = machineName;
		this.provider = provider;
		this.time = time;
		
		this.events = events;
		this.delayBeforeFadeIn = delayBeforeFadeIn;
		this.delayBeforeFadeOut = delayBeforeFadeOut;
		this.fadeInTime = fadeInTime;
		this.fadeOutTime = fadeOutTime;
	}
	
	private void signalPlayable()
	{
		this.startTime = this.time.getMilliseconds() + (long) (this.delayBeforeFadeIn * 1000);
	}
	
	private void signalStoppable()
	{
		this.stopTime = this.time.getMilliseconds() + (long) (this.delayBeforeFadeOut * 1000);
	}
	
	@Override
	public void evaluate()
	{
		if (!this.provider.exists(this.machineName))
			return;
		
		boolean active = this.provider.get(this.machineName).isActive();
		if (active != this.isActive)
		{
			this.isActive = active;
			incrementVersion();
			if (active)
			{
				signalPlayable();
			}
			else
			{
				signalStoppable();
			}
		}
	}
	
	@Override
	public void simulate()
	{
		if (this.isActive && !this.isPlaying)
		{
			if (this.time.getMilliseconds() > this.startTime)
			{
				this.isPlaying = true;
				for (TimedEventInterface t : this.events)
				{
					t.restart(this.time);
				}
			}
		}
		else if (!this.isActive && this.isPlaying)
		{
			if (this.time.getMilliseconds() > this.stopTime)
			{
				this.isPlaying = false;
			}
		}
		
		if (this.isPlaying || this.time.getMilliseconds() < this.stopTime + this.fadeOutTime)
		{
			play();
		}
	}
	
	private void play()
	{
		float fadeFactor = calculateFadeFactor();
		for (TimedEventInterface t : this.events)
		{
			t.play(this.time, fadeFactor);
		}
	}
	
	private float calculateFadeFactor()
	{
		float ret = 1f;
		long ms = this.time.getMilliseconds();
		
		if (this.isPlaying)
		{
			float fac = (ms - this.startTime) / (this.fadeInTime * 1f);
			if (fac > 1f)
			{
				fac = 1f;
			}
			else if (fac < 0f)
			{
				fac = 0f;
			}
			ret = fac;
		}
		else
		{
			float fac = (ms - this.stopTime) / (this.fadeOutTime * 1f);
			if (fac > 1f)
			{
				fac = 1f;
			}
			else if (fac < 0f)
			{
				fac = 0f;
			}
			ret = 1 - fac;
		}
		return ret;
	}
}
