package net.minecraft.src;

import java.net.URL;

import paulscode.sound.SoundSystem;

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

public class MAtSoundStream
{
	private final String SOURCE_PREFIX = "MATMOS_SRM_";
	
	private final int number;
	private MAtSoundManagerChild refer;
	
	private final String sourceName;
	
	private String path;
	private float volume;
	private float pitch;
	
	private boolean isInitialized;
	private boolean loopingIsSet;
	private boolean isPaused;
	private URL poolURL;
	
	private float playbackVolume;
	
	public MAtSoundStream(int number, MAtSoundManagerChild refer)
	{
		this.number = number;
		this.refer = refer;
		
		this.sourceName = this.SOURCE_PREFIX + this.number;
		this.poolURL = null;
		
	}
	
	public void startStreaming(float fadeDuration, int timesToPlay)
	{
		ensureInitialized();
		
		SoundSystem sndSystem = this.refer.getSoundSystem();
		
		if (!this.loopingIsSet)
		{
			if (timesToPlay == 0)
			{
				sndSystem.setLooping(this.sourceName, true);
			}
			else
			{
				sndSystem.setLooping(this.sourceName, false);
			}
			
			this.loopingIsSet = true;
			
		}
		
		evaluateAndApplyVolume();
		
		if (fadeDuration == 0)
		{
			if (this.isPaused)
			{
				sndSystem.play(this.sourceName);
			}
			else
			{
				// Prevent a bug where fading out and re-playing it while it is fading out breaks it
				// This doesn't happen with fadeoutins
				sndSystem.play(this.sourceName);
				sndSystem.fadeOutIn(this.sourceName, this.poolURL, this.path, 1, 1);
			}
			
		}
		else
		{
			// This is a workaround to counter the bug that makes FadeIn impossible
			// Set 1 millisecond fade out
			//
			// http://www.java-gaming.org/index.php?action=profile;u=11099;sa=showPosts
			
			sndSystem.play(this.sourceName);
			sndSystem.fadeOutIn(this.sourceName, this.poolURL, this.path, 1, (long) fadeDuration * 1000L);
			
		}
		
	}
	
	private void ensureInitialized()
	{
		if (this.isInitialized)
			return;
		
		MAtMod.LOGGER.info("Initializing source: " + this.sourceName + " (" + this.path + ")");
		
		SoundPoolEntry poolEntry = this.refer.getSoundPoolEntryOf(this.path);
		if (poolEntry != null)
		{
			this.poolURL = poolEntry.soundUrl;
			
			MAtMod.LOGGER.info("Source: "
				+ this.sourceName + " is being initialized with URL: " + poolEntry.soundUrl.toString());
			
			SoundSystem sndSystem = this.refer.getSoundSystem();
			
			sndSystem.newStreamingSource(true, this.sourceName, poolEntry.soundUrl, this.path, true, 0, 0, 0, 0, 0);
			sndSystem.setTemporary(this.sourceName, false);
			sndSystem.setPitch(this.sourceName, this.pitch);
			
			sndSystem.setLooping(this.sourceName, true);
			sndSystem.activate(this.sourceName);
			
		}
		
		this.isInitialized = true;
	}
	
	public void setPath(String path)
	{
		this.path = path;
		
	}
	
	public void setVolume(float volume)
	{
		this.volume = volume;
		
	}
	
	public void setPitch(float pitch)
	{
		this.pitch = pitch;
		
	}
	
	public void setPlaybackVolumeMod(float playbackVolume)
	{
		this.playbackVolume = playbackVolume;
		
		if (this.isInitialized)
		{
			evaluateAndApplyVolume();
			
		}
	}
	
	private void evaluateAndApplyVolume()
	{
		if (!this.isInitialized)
			return;
		
		float volume = this.playbackVolume * this.volume;
		
		this.refer.getSoundSystem().setVolume(this.sourceName, volume);
		
	}
	
	public void stopStreaming(float fadeDuration)
	{
		SoundSystem sndSystem = this.refer.getSoundSystem();
		
		if (fadeDuration <= 0f)
		{
			sndSystem.stop(this.sourceName);
		}
		else
		{
			sndSystem.fadeOut(this.sourceName, null, (long) fadeDuration * 1000L);
		}
		
	}
	
	public void pauseStreaming()
	{
		SoundSystem sndSystem = this.refer.getSoundSystem();
		sndSystem.pause(this.sourceName);
		
		this.isPaused = true;
		
	}
	
	public void interruptStreaming()
	{
		SoundSystem sndSystem = this.refer.getSoundSystem();
		sndSystem.stop(this.sourceName);
		
	}
	
	public void unallocate()
	{
		if (!this.isInitialized)
			return;
		
		SoundSystem sndSystem = this.refer.getSoundSystem();
		interruptStreaming();
		sndSystem.removeSource(this.sourceName);
		
	}
	
}
