package net.minecraft.src;

import java.util.HashMap;
import java.util.Map;

import paulscode.sound.SoundSystem;
import eu.ha3.matmos.conv.AnyLogger;
import eu.ha3.matmos.conv.CustomVolume;
import eu.ha3.matmos.engine.SoundRelay;

/*
            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
                    Version 2, December 2004 

 Copyright (C) 2004 Sam Hocevar <sam@hocevar.net> 

 Everyone is permitted to copy and distribute verbatim or modified 
 copies of this license document, and changing it is allowed as long 
 as the name is changed. 

            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION 

  0. You just DO WHAT THE FUCK YOU WANT TO. 
*/

public class MAtSoundManagerChild implements SoundRelay, CustomVolume
{
	private MAtSoundManagerMaster master;
	
	private float managerVolume;
	
	private Map<Integer, MAtSoundStream> tokens;
	
	private float masterVolumeCheck;
	private float settingsVolumeCheck;
	
	public MAtSoundManagerChild(MAtSoundManagerMaster master)
	{
		this.master = master;
		
		this.managerVolume = 1f;
		
		this.tokens = new HashMap<Integer, MAtSoundStream>();
		this.masterVolumeCheck = -1;
		
	}
	
	private MAtSoundManagerMaster getMaster()
	{
		return this.master;
		
	}
	
	@Override
	public void setVolume(float vol)
	{
		this.managerVolume = vol;
		propagateVolume();
		
	}
	
	@Override
	public float getVolume()
	{
		return this.managerVolume;
	}
	
	@Override
	public void routine()
	{
		// TODO (22 aug 2012) : Switch to an event based system...
		if (this.masterVolumeCheck != getMaster().getVolume()
			|| this.settingsVolumeCheck != getMaster().getSettingsVolume())
		{
			propagateVolume();
			
			this.masterVolumeCheck = getMaster().getVolume();
			this.settingsVolumeCheck = getMaster().getSettingsVolume();
			
		}
	}
	
	private void propagateVolume()
	{
		float playbackVolume = computePlaybackVolume();
		
		for (MAtSoundStream stream : this.tokens.values())
		{
			stream.setPlaybackVolumeMod(playbackVolume);
			
		}
		
	}
	
	private float computePlaybackVolume()
	{
		return getMaster().getVolume() * this.managerVolume * getMaster().getSettingsVolume();
	}
	
	@Override
	public void cacheSound(String path)
	{
		getMaster().cacheSound(path);
		
	}
	
	@Override
	public void playSound(String path, float volume, float pitch, int meta)
	{
		this.master.playSound(path, this.managerVolume * volume, pitch, meta);
		
	}
	
	@Override
	public int getNewStreamingToken()
	{
		int token = getMaster().getNewStreamingToken();
		this.tokens.put(token, new MAtSoundStream(token, this));
		
		return token;
	}
	
	@Override
	public boolean setupStreamingToken(int token, String path, float volume, float pitch)
	{
		cacheSound(path);
		
		this.tokens.get(token).setPath(path);
		this.tokens.get(token).setVolume(volume);
		this.tokens.get(token).setPitch(pitch);
		
		this.tokens.get(token).setPlaybackVolumeMod(computePlaybackVolume());
		
		return true;
	}
	
	@Override
	public void startStreaming(int token, float fadeDuration, int timesToPlay)
	{
		this.tokens.get(token).startStreaming(fadeDuration, timesToPlay);
	}
	
	@Override
	public void stopStreaming(int token, float fadeDuration)
	{
		this.tokens.get(token).stopStreaming(fadeDuration);
		
	}
	
	@Override
	public void pauseStreaming(int token, float fadeDuration)
	{
		this.tokens.get(token).pauseStreaming();
		
	}
	
	@Override
	public void eraseStreamingToken(int token)
	{
		AnyLogger.info("Erasing token #" + token);
		
		MAtSoundStream stream = this.tokens.get(token);
		stream.interruptStreaming();
		stream.unallocate();
		this.tokens.remove(token);
		
	}
	
	public SoundPoolEntry getSoundPoolEntryOf(String path)
	{
		return this.master.getSoundPoolEntryOf(path);
	}
	
	public SoundSystem getSoundSystem()
	{
		return this.master.getSoundSystem();
	}
	
	@Override
	public void finalize()
	{
		AnyLogger.info("Calling finalizer of SMC");
		
		try
		{
			for (MAtSoundStream stream : this.tokens.values())
			{
				try
				{
					stream.unallocate();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
