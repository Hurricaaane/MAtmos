package net.minecraft.src;

import eu.ha3.matmos.conv.CustomVolume;
import eu.ha3.matmos.conv.ReplicableSoundRelay;
import eu.ha3.matmos.engine.SoundRelay;
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

public class MAtSoundManagerNullObject implements ReplicableSoundRelay, CustomVolume
{

	@Override
	public void routine()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cacheSound(String path)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playSound(String path, float volume, float pitch, int meta)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getNewStreamingToken()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean setupStreamingToken(int token, String path, float volume, float pitch)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void startStreaming(int token, float fadeDuration, int timesToPlay)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopStreaming(int token, float fadeDuration)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pauseStreaming(int token, float fadeDuration)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void eraseStreamingToken(int token)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setVolume(float vol)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getVolume()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public SoundRelay createChild()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
}
