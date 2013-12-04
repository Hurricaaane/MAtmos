package eu.ha3.matmos.conv;

import eu.ha3.matmos.engine.interfaces.SoundRelay;

/* x-placeholder */

public class SRCVNullObject implements ReplicableSoundRelay, CustomVolume
{
	@Override
	public void routine()
	{
	}
	
	@Override
	public void cacheSound(String path)
	{
	}
	
	@Override
	public void playSound(String path, float volume, float pitch, int meta)
	{
	}
	
	@Override
	public int getNewStreamingToken()
	{
		return 0;
	}
	
	@Override
	public boolean setupStreamingToken(int token, String path, float volume, float pitch)
	{
		return false;
	}
	
	@Override
	public void startStreaming(int token, float fadeDuration, int timesToPlay)
	{
	}
	
	@Override
	public void stopStreaming(int token, float fadeDuration)
	{
	}
	
	@Override
	public void pauseStreaming(int token, float fadeDuration)
	{
	}
	
	@Override
	public void eraseStreamingToken(int token)
	{
	}
	
	@Override
	public void setVolume(float vol)
	{
	}
	
	@Override
	public float getVolume()
	{
		return 0;
	}
	
	@Override
	public SoundRelay createChild()
	{
		return null;
	}
	
}
