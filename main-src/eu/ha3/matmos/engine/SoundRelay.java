package eu.ha3.matmos.engine;

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

public interface SoundRelay
{
	public void routine();
	
	public void cacheSound(String path);
	
	public void playSound(String path, float volume, float pitch, int meta);
	
	public int getNewStreamingToken();
	
	public boolean setupStreamingToken(int token, String path, float volume, float pitch);
	
	public void startStreaming(int token, float fadeDuration, int timesToPlay);
	
	public void stopStreaming(int token, float fadeDuration);
	
	public void pauseStreaming(int token, float fadeDuration);
	
	public void eraseStreamingToken(int token);
	
}
