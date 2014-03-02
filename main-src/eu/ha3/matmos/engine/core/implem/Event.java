package eu.ha3.matmos.engine.core.implem;

import java.util.List;
import java.util.Random;

import eu.ha3.matmos.engine.core.implem.abstractions.Component;
import eu.ha3.matmos.engine.core.interfaces.EventInterface;
import eu.ha3.matmos.engine.core.interfaces.SoundRelay;

/* x-placeholder */

public class Event extends Component implements EventInterface
{
	private static Random random = new Random();
	
	public final List<String> paths;
	public final float volMin;
	public final float volMax;
	public final float pitchMin;
	public final float pitchMax;
	public final int distance;
	
	//
	
	private final SoundRelay relay;
	
	public Event(
		String name, SoundRelay relay, List<String> paths, float volMin, float volMax, float pitchMin, float pitchMax,
		int distance)
	{
		super(name);
		this.relay = relay;
		
		this.paths = paths;
		this.volMin = volMin;
		this.volMax = volMax;
		this.pitchMin = pitchMin;
		this.pitchMax = pitchMax;
		this.distance = distance;
	}
	
	@Override
	public void cacheSounds()
	{
		for (String path : this.paths)
		{
			this.relay.cacheSound(path);
		}
	}
	
	@Override
	public void playSound(float volMod, float pitchMod)
	{
		if (this.paths.isEmpty())
			return;
		
		float volume = this.volMax - this.volMin;
		float pitch = this.pitchMax - this.pitchMin;
		volume = this.volMin + (volume > 0 ? random.nextFloat() * volume : 0);
		pitch = this.pitchMin + (pitch > 0 ? random.nextFloat() * pitch : 0);
		
		String path = this.paths.get(random.nextInt(this.paths.size()));
		
		volume = volume * volMod;
		pitch = pitch * pitchMod;
		
		this.relay.playSound(path, volume, pitch, this.distance);
	}
}
