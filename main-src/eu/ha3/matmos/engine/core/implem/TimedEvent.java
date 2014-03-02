package eu.ha3.matmos.engine.core.implem;

import java.util.Random;

import eu.ha3.matmos.engine.core.implem.abstractions.Provider;
import eu.ha3.matmos.engine.core.interfaces.ReferenceTime;
import eu.ha3.matmos.engine.core.interfaces.TimedEventInterface;

/* x-placeholder */

public class TimedEvent implements TimedEventInterface
{
	private static Random random = new Random();
	
	private String event;
	private final Provider<Event> provider;
	private final float volMod;
	private final float pitchMod;
	private final float delayMin;
	private final float delayMax;
	private final float delayStart;
	
	private long nextPlayTime;
	
	public TimedEvent(
		String event, Provider<Event> provider, float volMod, float pitchMod, float delayMin, float delayMax,
		float delayStart)
	{
		this.event = event;
		this.provider = provider;
		this.volMod = volMod;
		this.pitchMod = pitchMod;
		this.delayMin = delayMin;
		this.delayMax = delayMax;
		this.delayStart = delayStart;
		
		if (delayMax < delayMin)
		{
			delayMax = delayMin;
		}
	}
	
	@Override
	public void restart(ReferenceTime time)
	{
		if (this.delayStart == 0)
		{
			this.nextPlayTime = time.getMilliseconds() + (long) (random.nextFloat() * this.delayMax * 1000);
		}
		else
		{
			this.nextPlayTime = time.getMilliseconds() + (long) (this.delayStart * 1000);
		}
	}
	
	@Override
	public void play(ReferenceTime time, float fadeFactor)
	{
		if (time.getMilliseconds() < this.nextPlayTime)
			return;
		
		if (this.provider.exists(this.event))
		{
			this.provider.get(this.event).playSound(this.volMod * fadeFactor, this.pitchMod);
		}
		
		if (this.delayMin == this.delayMax && this.delayMin > 0)
		{
			while (this.nextPlayTime < time.getMilliseconds())
			{
				this.nextPlayTime = this.nextPlayTime + (long) (this.delayMin * 1000);
			}
		}
		else
		{
			this.nextPlayTime =
				time.getMilliseconds()
					+ (long) ((this.delayMin + random.nextFloat() * (this.delayMax - this.delayMin)) * 1000);
		}
	}
}
