package eu.ha3.matmos.engine.core.implem;

import eu.ha3.matmos.engine.core.implem.abstractions.MultistateComponent;
import eu.ha3.matmos.engine.core.implem.abstractions.Provider;
import eu.ha3.matmos.engine.core.interfaces.ReferenceTime;
import eu.ha3.matmos.engine.core.interfaces.Simulated;
import eu.ha3.matmos.engine.core.interfaces.TimedEventInterface;

import java.util.List;

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
	
	private HelperFadeCalculator calc;
	
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
		
		this.calc = new HelperFadeCalculator(time);
		
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
				this.calc.fadeIn((long) (this.fadeInTime * 1000));
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
				this.calc.fadeOut((long) (this.fadeOutTime * 1000));
			}
		}
		
		if (this.isPlaying || this.calc.calculateFadeFactor() > 0f)
		{
			play();
		}
	}
	
	private void play()
	{
		float fadeFactor = this.calc.calculateFadeFactor();
		for (TimedEventInterface t : this.events)
		{
			t.play(this.time, fadeFactor);
		}
	}
}
