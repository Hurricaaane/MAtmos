package eu.ha3.matmos.engine.core.implem;

import eu.ha3.matmos.engine.core.implem.abstractions.Component;
import eu.ha3.matmos.engine.core.interfaces.EventInterface;
import eu.ha3.matmos.engine.core.interfaces.SoundRelay;
import eu.ha3.matmos.log.MAtLog;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
	public void cacheSounds(IResourcePack resourcePack)
	{
        List<String> toRemove = new ArrayList<String>();
		for (String path : this.paths)
		{
            if (resourcePack.resourceExists(new ResourceLocation("minecraft", "sounds/" + path)))
			    this.relay.cacheSound(path);
            else
            {
                MAtLog.warning("File: " + path + " appears to be missing from: " + resourcePack.getPackName() + " [This sound will not be cached or played in-game]");
                toRemove.add(path);
            }
		}
        paths.removeAll(toRemove);
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
