package eu.ha3.matmos.engine0.game.system;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;

/*
--filenotes-placeholder
*/

public class SoundHelper implements SoundCapabilities
{
	protected SoundAccessor accessor;
	protected Map<String, StreamingSound> streaming;
	
	private float volumeModulator;
	
	private boolean isInterrupt;
	
	public SoundHelper(SoundAccessor accessor)
	{
		this.accessor = accessor;
		this.streaming = new LinkedHashMap<String, StreamingSound>();
	}
	
	@Override
	public void playMono(String event, double xx, double yy, double zz, float volume, float pitch)
	{
		if (this.isInterrupt)
			return;
		
		// XXX 2014-01-10 what is the last boolean?
		Minecraft.getMinecraft().theWorld.playSound(xx, yy, zz, event, volume * this.volumeModulator, pitch, false);
	}
	
	@Override
	public void playStereo(String event, float volume, float pitch)
	{
		if (this.isInterrupt)
			return;
		
		Minecraft.getMinecraft().thePlayer.playSound(event, volume * this.volumeModulator, pitch);
	}
	
	@Override
	public void registerStreaming(
		String customName, String path, float volume, float pitch, boolean isLooping, boolean usesPause)
	{
		if (this.isInterrupt)
			return;
		
		StreamingSound sound =
			new StreamingSoundUsingAccessor(
				this.accessor, path, volume * this.volumeModulator, pitch, isLooping, usesPause);
		this.streaming.put(customName, sound);
	}
	
	@Override
	public void playStreaming(String customName, int fadeIn)
	{
		if (this.isInterrupt)
			return;
		
		if (this.streaming.containsKey(customName))
			throw new RuntimeException();
		
		this.streaming.get(customName).play(fadeIn);
	}
	
	@Override
	public void stopStreaming(String customName, int fadeOut)
	{
		if (this.isInterrupt)
			return;
		
		if (this.streaming.containsKey(customName))
			throw new RuntimeException();
		
		this.streaming.get(customName).stop(fadeOut);
	}
	
	@Override
	public void stop()
	{
		if (this.isInterrupt)
			return;
		
		for (StreamingSound sound : this.streaming.values())
		{
			sound.stop(0f);
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
