package eu.ha3.matmos.engine0.game.system;

import eu.ha3.matmos.engine0.core.interfaces.SoundRelay;

/*
--filenotes-placeholder
*/

public class SoundHelperRelay extends SoundHelper implements SoundRelay
{
	private static int streamingToken;
	
	public SoundHelperRelay(SoundAccessor accessor)
	{
		super(accessor);
	}
	
	@Override
	public void routine()
	{
	}
	
	@Override
	public void cacheSound(String path)
	{
		// no need
	}
	
	@Override
	public void playSound(String path, float volume, float pitch, int meta)
	{
		playSound(path, volume, pitch, meta);
	}
	
	@Override
	public int getNewStreamingToken()
	{
		return SoundHelperRelay.streamingToken++;
	}
	
	@Override
	public boolean setupStreamingToken(
		int token, String path, float volume, float pitch, boolean isLooping, boolean usesPause)
	{
		registerStreaming(token + "_", path, volume, pitch, isLooping, usesPause);
		return true;
	}
	
	@Override
	public void startStreaming(int token, float fadeDuration)
	{
	}
	
	@Override
	public void stopStreaming(int token, float fadeDuration)
	{
	}
	
	@Override
	public void eraseStreamingToken(int token)
	{
	}
}
