package eu.ha3.matmos.game.system;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.src.SoundPoolEntry;
import paulscode.sound.SoundSystem;
import eu.ha3.matmos.conv.CustomVolume;
import eu.ha3.matmos.conv.MAtmosConvLogger;
import eu.ha3.matmos.engine.interfaces.SoundRelay;

/* x-placeholder */

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
		
		MAtSoundStream stream = this.tokens.get(token);
		stream.setWeakPath(path);
		stream.setVolume(volume);
		stream.setPitch(pitch);
		
		stream.setPlaybackVolumeMod(computePlaybackVolume());
		
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
		MAtmosConvLogger.info("Erasing token #" + token);
		
		MAtSoundStream stream = this.tokens.get(token);
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
		MAtmosConvLogger.info("Calling finalizer of SMC");
		
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
