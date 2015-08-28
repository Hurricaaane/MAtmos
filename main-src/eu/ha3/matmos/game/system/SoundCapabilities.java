package eu.ha3.matmos.game.system;

/*
--filenotes-placeholder
*/

public interface SoundCapabilities
{
	/**
	 * Plays a mono, localized sound at a certain location.
	 * 
	 * @param event
	 * @param xx
	 * @param yy
	 * @param zz
	 * @param volume
	 * @param pitch
	 */
	public void playMono(String event, double xx, double yy, double zz, float volume, float pitch);
	
	/**
	 * Plays a stereo, unlocalized sound.
	 * 
	 * @param event
	 * @param volume
	 * @param pitch
	 */
	public void playStereo(String event, float volume, float pitch);
	
	/**
	 * Registers a streaming sound.
	 * 
	 * @param customName
	 * @param path
	 * @param volume
	 * @param pitch
	 * @param isLooping
	 * @param usesPause
	 */
	public void registerStreaming(
		String customName, String path, float volume, float pitch, boolean isLooping, boolean usesPause);
	
	/**
	 * Plays a streaming sound, fading in if it's greater than zero. Fading unit
	 * is in seconds.
	 * 
	 * @param customName
	 * @param fadeIn
	 */
	public void playStreaming(String customName, float fadeIn);
	
	/**
	 * Stops a streaming sound, fading out if it's greater than zero. Fading
	 * unit is in seconds.
	 * 
	 * @param customName
	 * @param fadeOut
	 */
	public void stopStreaming(String customName, float fadeOut);
	
	/**
	 * Instantly applies a volume modulation of all currently running stuff and
	 * future ones.
	 * 
	 * @param volumeMod
	 */
	public void applyVolume(float volumeMod);
	
	/**
	 * Gracefully stops all activities provided by the implementation. It should
	 * stop all sounds from playing.
	 */
	public void stop();
	
	/**
	 * Clean up all resources that are not freed up. SoundCapabilities should be
	 * able to be used again.
	 */
	public void cleanUp();
	
	/**
	 * Brutally interrupts all activities provided by the implementation. This
	 * indicates the sound engine may have been dumped during runtime.
	 */
	public void interrupt();
}
