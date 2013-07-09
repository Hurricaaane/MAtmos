package eu.ha3.matmos.conv;

import eu.ha3.matmos.engine.interfaces.SoundRelay;

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
