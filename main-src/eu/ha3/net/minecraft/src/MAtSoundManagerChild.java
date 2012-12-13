package net.minecraft.src;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.client.Minecraft;
import paulscode.sound.SoundSystem;
import eu.ha3.matmos.engine.SoundRelay;
import eu.ha3.mc.haddon.PrivateAccessException;

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

public class MAtSoundManagerChild implements SoundRelay, MAtCustomVolume
{
	private MAtMod mod;
	
	private float managerVolume;
	
	private Random random;
	private Map<Integer, MAtSoundStream> tokens;
	
	private float masterVolumeCheck;
	private float settingsVolumeCheck;
	
	public MAtSoundManagerChild(MAtMod mod)
	{
		this.mod = mod;
		
		this.managerVolume = 1f;
		
		this.random = new Random();
		this.tokens = new HashMap<Integer, MAtSoundStream>();
		this.masterVolumeCheck = -1;
		
	}
	
	private MAtSoundManagerMaster getMaster()
	{
		return this.mod.getSoundManagerMaster();
		
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
		Minecraft mc = this.mod.manager().getMinecraft();
		float nx = (float) mc.thePlayer.posX;
		float ny = (float) mc.thePlayer.posY;
		float nz = (float) mc.thePlayer.posZ;
		
		String equivalent = getMaster().getSound(path);
		
		float soundEffectiveVolume = getMaster().getVolume() * this.managerVolume * volume;
		
		if (soundEffectiveVolume <= 0)
			return;
		
		if (meta > 0)
		{
			double angle = this.random.nextFloat() * 2 * Math.PI;
			nx = nx + (float) (Math.cos(angle) * meta);
			ny = ny + this.random.nextFloat() * meta * 0.2F - meta * 0.01F;
			nz = nz + (float) (Math.sin(angle) * meta);
			
			this.mod.getSoundCommunicator().playSound(equivalent, nx, ny, nz, soundEffectiveVolume, pitch, 0, 0F);
		}
		else
		{
			// NOTE: playSoundFX from Minecraft SoundManager
			//   does NOT work (actually, only works for stereo sounds).
			// Must use playSoundFX Proxy
			//   which will play the sound 2048 blocks above the player...
			//   ...and that somehow does the trick!
			
			ny = ny + 2048;
			this.mod.getSoundCommunicator().playSound(equivalent, nx, ny, nz, soundEffectiveVolume, pitch, 0, 0F);
			
		}
		
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
		MAtMod.LOGGER.info("Erasing token #" + token);
		
		MAtSoundStream stream = this.tokens.get(token);
		stream.interruptStreaming();
		stream.unallocate();
		this.tokens.remove(token);
		
	}
	
	public SoundPoolEntry getSoundPoolEntryOf(String path)
	{
		try
		{
			// soundPoolSounds
			return ((SoundPool) this.mod.util().getPrivateValueLiteral(
				net.minecraft.src.SoundManager.class, this.mod.manager().getMinecraft().sndManager, "b", 1))
				.getRandomSoundFromSoundPool(getMaster().getSound(path));
		}
		catch (PrivateAccessException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public SoundSystem getSoundSystem()
	{
		return this.mod.getSoundCommunicator().getSoundSystem();
	}
	
	@Override
	public void finalize()
	{
		MAtMod.LOGGER.info("Calling finalizer of SMC");
		
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
