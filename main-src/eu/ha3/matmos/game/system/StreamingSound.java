package eu.ha3.matmos.game.system;

/*
--filenotes-placeholder
*/

public interface StreamingSound
{
	/**
	 * Fading unit is in seconds.
	 * 
	 * @param fadeIn
	 */
	public void play(float fadeIn);
	
	/**
	 * Fading unit is in seconds.
	 * 
	 * @param fadeOut
	 */
	public void stop(float fadeOut);
	
	/**
	 * Instantly applies a volume modulation to this stream, upon the initially
	 * set volume.
	 * 
	 * @param volumeMod
	 */
	public void applyVolume(float volumeMod);
	
	/**
	 * Dispose of this StreamingSound. The StreamingSound should never be able
	 * to be used again.
	 */
	public void dispose();
	
	public void interrupt();
}
