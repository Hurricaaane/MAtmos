package eu.ha3.matmos.game.system;

import eu.ha3.matmos.engine.core.interfaces.SoundRelay;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

/*
--filenotes-placeholder
*/

public class SoundHelperRelay extends SoundHelper implements SoundRelay
{
	private static int streamingToken;
	private Map<String, String> paths;
	
	public SoundHelperRelay(SoundAccessor accessor)
	{
		super(accessor);
		
		this.paths = new HashMap<String, String>();
	}
	
	@Override
	public void routine()
	{
	}
	
	@Override
	public void cacheSound(String path)
	{
		String dotted = path.replace(".ogg", "").replace('/', '.').replaceAll("[0-9]", "");
		this.paths.put(path, dotted);
	}
	
	@Override
	public void playSound(String path, float volume, float pitch, int meta)
	{
		// XXX 2014-01-12 TEMPORARY: USE MONO-STEREO
		//playStereo(this.paths.get(path), volume, pitch);
		Entity e = Minecraft.getMinecraft().thePlayer;
		if (meta <= 0)
		{
			playStereo(this.paths.get(path), volume, pitch);
		}
		else
		{
			playMono(this.paths.get(path), e.posX, e.posY, e.posZ, volume, pitch);
		}
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
		registerStreaming("" + token, path, volume, pitch, isLooping, usesPause);

		return true; 
	}
	
	@Override
	public void startStreaming(int token, float fadeDuration)
	{
		playStreaming("" + token, fadeDuration);
	}
	
	@Override
	public void stopStreaming(int token, float fadeDuration)
	{
		stopStreaming("" + token, fadeDuration);
	}

	@Override
	public void eraseStreamingToken(int token)
	{
	}
}
