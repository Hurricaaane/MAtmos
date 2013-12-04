package eu.ha3.matmos.engine.interfaces;

/* x-placeholder */

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
