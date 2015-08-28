package eu.ha3.matmos.game.system;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/*
--filenotes-placeholder
*/

public class SoundHelper implements SoundCapabilities
{
	protected SoundAccessor accessor;
	protected Map<String, NoAttenuationMovingSound> streaming;
	
	private float volumeModulator;
	
	private boolean isInterrupt;
	
	public SoundHelper(SoundAccessor accessor)
	{
		this.accessor = accessor;
		this.streaming = new LinkedHashMap<String, NoAttenuationMovingSound>();
	}
	
	@Override
	public void playMono(String event, double xx, double yy, double zz, float volume, float pitch)
	{
		if (this.isInterrupt)
			return;
		
		// XXX 2014-01-10 what is the last boolean?
		playUnattenuatedSound(xx, yy, zz, event, volume * this.volumeModulator, pitch);
	}
	
	@Override
	public void playStereo(String event, float volume, float pitch)
	{
		if (this.isInterrupt)
			return;
		
		// Play the sound 2048 blocks above the player to keep support of mono sounds
		Entity e = Minecraft.getMinecraft().thePlayer;
		playUnattenuatedSound(e.posX, e.posY + 2048, e.posZ, event, volume * this.volumeModulator, pitch);
	}
	
	private void playUnattenuatedSound(double xx, double yy, double zz, String loc, float a, float b)
	{
		NoAttenuationSound nas =
			new NoAttenuationSound(new ResourceLocation(loc), a, b, (float) xx, (float) yy, (float) zz);
		
		Minecraft.getMinecraft().getSoundHandler().playSound(nas);
	}
	
	@Override
	public void registerStreaming(
		String customName, String path, float volume, float pitch, boolean isLooping, boolean usesPause)
	{
		if (this.isInterrupt)
			return;

		String loc = path.replace(".ogg", "").replace('/', '.').replaceAll("[0-9]", "");
		NoAttenuationMovingSound nams = new NoAttenuationMovingSound(new ResourceLocation(loc), volume, pitch, isLooping, usesPause);

		this.streaming.put(customName, nams);
	}
	
	@Override
	public void playStreaming(String customName, float fadeIn)
	{
		if (this.isInterrupt)
			return;
		
		if (!this.streaming.containsKey(customName))
		{
			IDontKnowHowToCode.warnOnce("Tried to play missing stream " + customName);
			return;
		}

		// Ensure previous sound is disposed of
		this.streaming.get(customName).dispose();

		NoAttenuationMovingSound copy = this.streaming.get(customName).copy();
		this.streaming.put(customName, copy);
		Minecraft.getMinecraft().getSoundHandler().playSound(copy);
	}
	
	@Override
	public void stopStreaming(String customName, float fadeOut)
	{
		if (this.isInterrupt)
			return;
		
		if (!this.streaming.containsKey(customName))
		{
			IDontKnowHowToCode.warnOnce("Tried to stop missing stream " + customName);
			return;
		}

		this.streaming.get(customName).dispose();
	}
	
	@Override
	public void stop()
	{
		if (this.isInterrupt)
			return;
		
		for (StreamingSound sound : this.streaming.values())
		{
			sound.dispose();
		}
	}
	
	@Override
	public void applyVolume(float volumeMod)
	{
		this.volumeModulator = volumeMod;
		for (StreamingSound sound : this.streaming.values())
		{
			sound.applyVolume(volumeMod);
		}
	}
	
	@Override
	public void interrupt()
	{
		this.isInterrupt = true;
	}
	
	@Override
	public void cleanUp()
	{
		if (this.isInterrupt)
			return;
		
		for (StreamingSound sound : this.streaming.values())
		{
			sound.dispose();
		}
		this.streaming.clear();
	}
	
}
