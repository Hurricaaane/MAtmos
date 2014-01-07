package eu.ha3.matmos.engine0.game.system;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.client.Minecraft;
import paulscode.sound.SoundSystemConfig;
import eu.ha3.matmos.engine0.conv.CustomVolume;
import eu.ha3.matmos.engine0.conv.ReplicableSoundRelay;
import eu.ha3.matmos.v170helper.Version170Helper;

/* x-placeholder */

public class MAtSoundManagerMaster implements ReplicableSoundRelay, CustomVolume
{
	private float volume;
	
	private int nbTokens;
	private Map<String, String> soundequivalences;
	
	private float settingsVolume;
	
	private Random random;
	
	public MAtSoundManagerMaster()
	{
		this.volume = 1F;
		
		this.nbTokens = 0;
		this.soundequivalences = new HashMap<String, String>();
		
		this.settingsVolume = 0F;
		this.random = new Random();
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
		
		// TODO 2013-12-06 : It should be mandatory that all global sounds must be set as stereo sounds
		if (meta > 0)
		{
			// Play a positoned sound
			double angle = this.random.nextFloat() * 2 * Math.PI;
			nx = nx + (float) (Math.cos(angle) * meta);
			ny = ny + this.random.nextFloat() * meta * 0.2F - meta * 0.01F;
			nz = nz + (float) (Math.sin(angle) * meta);
			
			Version170Helper.playSound(
				equivalent, nx, ny, nz, soundEffectiveVolume, pitch, SoundSystemConfig.ATTENUATION_NONE, 0F);
		}
		else
		{
			// Play a pseudo-global sound
			
			// NOTE: playSoundFX from Minecraft SoundManager
			//   does NOT work (actually, only works for stereo sounds).
			// Must use playSoundFX Proxy
			//   which will play the sound 2048 blocks above the player...
			//   ...and that somehow does the trick!
			
			ny = ny + 2048;
			Version170Helper.playSound(
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
		
		// XXX 1.7.2
		if (this.settingsVolume != Version170Helper.getSoundVolume())
		{
			this.settingsVolume = Version170Helper.getSoundVolume();
		}
	}
	
	public float getSettingsVolume()
	{
		return this.settingsVolume;
	}
	
	@Override
	public MAtSoundManagerChild createChild()
	{
		return new MAtSoundManagerChild(this);
	}
	
}
