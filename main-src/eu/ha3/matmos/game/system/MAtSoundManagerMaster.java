package eu.ha3.matmos.game.system;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.src.Minecraft;
import net.minecraft.src.SoundPoolEntry;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import eu.ha3.matmos.conv.CustomVolume;
import eu.ha3.matmos.conv.ReplicableSoundRelay;
import eu.ha3.matmos.game.data.MAtAccessors;

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

public class MAtSoundManagerMaster implements ReplicableSoundRelay, CustomVolume
{
	private MAtMod mod;
	
	private float volume;
	
	private int nbTokens;
	private Map<String, String> soundequivalences;
	
	private float settingsVolume;
	
	private Random random;
	
	public MAtSoundManagerMaster(MAtMod mAtmosHaddon)
	{
		this.mod = mAtmosHaddon;
		
		this.volume = 1F;
		
		this.nbTokens = 0;
		this.soundequivalences = new HashMap<String, String>();
		
		this.settingsVolume = 0F;
		this.random = new Random();
	}
	
	public SoundSystem sndSystem()
	{
		return this.mod.getSoundCommunicator().getSoundSystem();
		
	}
	
	@Override
	public float getVolume()
	{
		return this.volume;
		
	}
	
	@Override
	public void routine()
	{
		updateSettingsVolume();
	}
	
	@Override
	public void cacheSound(String path)
	{
		getSound(path);
		//this.cacheRegistry.cacheSound(path);
	}
	
	public String getSound(String soundPath)
	{
		if (this.soundequivalences.containsKey(soundPath))
			return this.soundequivalences.get(soundPath);
		
		String quant = soundPath.substring(0, soundPath.indexOf("."));
		String dotted = quant.replaceAll("/", ".");
		while (Character.isDigit(soundPath.charAt(dotted.length() - 1)))
		{
			dotted = dotted.substring(0, dotted.length() - 1);
		}
		
		this.soundequivalences.put(soundPath, dotted);
		
		return dotted;
		
	}
	
	@Override
	public void playSound(String path, float volume, float pitch, int meta)
	{
		Minecraft mc = Minecraft.getMinecraft();
		float nx = (float) mc.thePlayer.posX;
		float ny = (float) mc.thePlayer.posY;
		float nz = (float) mc.thePlayer.posZ;
		
		String equivalent = getSound(path);
		
		float soundEffectiveVolume = getVolume() * volume;
		
		if (soundEffectiveVolume <= 0)
			return;
		
		if (meta > 0)
		{
			double angle = this.random.nextFloat() * 2 * Math.PI;
			nx = nx + (float) (Math.cos(angle) * meta);
			ny = ny + this.random.nextFloat() * meta * 0.2F - meta * 0.01F;
			nz = nz + (float) (Math.sin(angle) * meta);
			
			this.mod.getSoundCommunicator().playSound(
				equivalent, nx, ny, nz, soundEffectiveVolume, pitch, SoundSystemConfig.ATTENUATION_NONE, 0F);
		}
		else
		{
			// NOTE: playSoundFX from Minecraft SoundManager
			//   does NOT work (actually, only works for stereo sounds).
			// Must use playSoundFX Proxy
			//   which will play the sound 2048 blocks above the player...
			//   ...and that somehow does the trick!
			
			ny = ny + 2048;
			this.mod.getSoundCommunicator().playSound(
				equivalent, nx, ny, nz, soundEffectiveVolume, pitch, SoundSystemConfig.ATTENUATION_NONE, 0F);
		}
	}
	
	@Override
	public synchronized int getNewStreamingToken()
	{
		int token = this.nbTokens;
		this.nbTokens = this.nbTokens + 1;
		
		return token;
	}
	
	@Override
	public synchronized boolean setupStreamingToken(int token, String path, float volume, float pitch)
	{
		// Master NEVER manages stream playback.
		
		return true;
	}
	
	@Override
	public synchronized void startStreaming(int token, float fadeDuration, int timesToPlay)
	{
		// Master NEVER manages stream playback.
	}
	
	@Override
	public synchronized void stopStreaming(int token, float fadeDuration)
	{
		// Master NEVER manages stream playback.
	}
	
	@Override
	public synchronized void pauseStreaming(int token, float fadeDuration)
	{
		// Master NEVER manages stream playback.
	}
	
	@Override
	public synchronized void eraseStreamingToken(int token)
	{
		// Master NEVER manages stream playback.
	}
	
	@Override
	public void setVolume(float modifier)
	{
		this.volume = modifier;
		
	}
	
	private void updateSettingsVolume()
	{
		Minecraft mc = Minecraft.getMinecraft();
		
		if (this.settingsVolume != mc.gameSettings.soundVolume)
		{
			this.settingsVolume = mc.gameSettings.soundVolume;
		}
	}
	
	public float getSettingsVolume()
	{
		return this.settingsVolume;
	}
	
	public SoundPoolEntry getSoundPoolEntryOf(String path)
	{
		return MAtAccessors.getSoundPoolSounds(this.mod.util()).getRandomSoundFromSoundPool(getSound(path));
	}
	
	public SoundSystem getSoundSystem()
	{
		return this.mod.getSoundCommunicator().getSoundSystem();
	}
	
	@Override
	public MAtSoundManagerChild createChild()
	{
		return new MAtSoundManagerChild(this);
	}
	
}
