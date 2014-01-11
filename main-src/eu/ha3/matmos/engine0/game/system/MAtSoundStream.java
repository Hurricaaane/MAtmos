package eu.ha3.matmos.engine0.game.system;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import paulscode.sound.SoundSystem;
import eu.ha3.matmos.engine0.conv.MAtmosConvLogger;

/* x-placeholder */

@Deprecated
public class MAtSoundStream
{
	private final String SOURCE_PREFIX = "MATMOS_SRM_";
	
	private final int number;
	private MAtSoundManagerChild soundManagerReference;
	
	private final String sourceName;
	
	private String path;
	private ResourceLocation loc;
	
	private float volume;
	private float pitch;
	
	private boolean isInitialized;
	private boolean loopingIsSet;
	private boolean isPaused;
	private URL poolURL;
	
	private boolean isValid;
	
	private float playbackVolume;
	
	public MAtSoundStream(int number, MAtSoundManagerChild refer)
	{
		this.number = number;
		this.soundManagerReference = refer;
		
		this.sourceName = this.SOURCE_PREFIX + this.number;
		this.poolURL = null;
		
	}
	
	public void startStreaming(float fadeDuration, int timesToPlay)
	{
		ensureInitialized();
		
		if (!this.isValid)
			return;
		
		SoundSystem sndSystem = this.soundManagerReference.getSoundSystem();
		if (sndSystem == null)
		{
			invalidSoundSystem();
			return;
		}
		
		if (!this.loopingIsSet)
		{
			if (timesToPlay == 0)
			{
				sndSystem.setLooping(this.sourceName, true);
			}
			else
			{
				sndSystem.setLooping(this.sourceName, false);
			}
			
			this.loopingIsSet = true;
			
		}
		
		evaluateAndApplyVolume();
		
		if (fadeDuration == 0)
		{
			if (this.isPaused)
			{
				sndSystem.play(this.sourceName);
			}
			else
			{
				// Prevent a bug where fading out and re-playing it while it is fading out breaks it
				// This doesn't happen with fadeoutins
				sndSystem.play(this.sourceName);
				sndSystem.fadeOutIn(this.sourceName, this.poolURL, this.path, 1, 1);
			}
			
		}
		else
		{
			// This is a workaround to counter the bug that makes FadeIn impossible
			// Set 1 millisecond fade out
			//
			// http://www.java-gaming.org/index.php?action=profile;u=11099;sa=showPosts
			
			sndSystem.play(this.sourceName);
			sndSystem.fadeOutIn(this.sourceName, this.poolURL, this.path, 1, (long) fadeDuration * 1000L);
		}
	}
	
	private void ensureInitialized()
	{
		if (this.isInitialized)
			return;
		
		MAtmosConvLogger.info("Initializing source: " + this.sourceName + " (" + this.path + ")");
		
		SoundSystem sndSystem = this.soundManagerReference.getSoundSystem();
		if (sndSystem != null)
		{
			sndSystem.newStreamingSource(true, this.sourceName, this.poolURL, this.path, true, 0, 0, 0, 0, 0);
			sndSystem.setTemporary(this.sourceName, false);
			sndSystem.setPitch(this.sourceName, this.pitch);
			
			sndSystem.setLooping(this.sourceName, true);
			sndSystem.activate(this.sourceName);
			this.isValid = true;
			
		}
		else if (sndSystem == null)
		{
			invalidSoundSystem();
		}
		
		this.isInitialized = true;
	}
	
	public void setWeakPath(String path)
	{
		this.path = path;
		this.loc = new ResourceLocation("assets/sounds/" + path);
		
		String opth =
			String.format(
				"%s:%s:%s", new Object[] { "mcsounddomain", this.loc.getResourceDomain(), this.loc.getResourcePath() });
		URLStreamHandler url = new URLStreamHandler() {
			@Override
			protected URLConnection openConnection(final URL par1URL)
			{
				return new URLConnection(par1URL) {
					@Override
					public void connect()
					{
					}
					
					@Override
					public InputStream getInputStream() throws IOException
					{
						return Minecraft
							.getMinecraft().getResourceManager().getResource(MAtSoundStream.this.loc).getInputStream();
					}
				};
			}
		};
		
		try
		{
			this.poolURL = new URL((URL) null, opth, url);
		}
		catch (MalformedURLException var4)
		{
		}
	}
	
	public void setVolume(float volume)
	{
		this.volume = volume;
		
	}
	
	public void setPitch(float pitch)
	{
		this.pitch = pitch;
		
	}
	
	public void setPlaybackVolumeMod(float playbackVolume)
	{
		this.playbackVolume = playbackVolume;
		
		if (this.isInitialized)
		{
			evaluateAndApplyVolume();
			
		}
	}
	
	private void evaluateAndApplyVolume()
	{
		if (!this.isInitialized)
			return;
		
		if (!this.isValid)
			return;
		
		float volume = this.playbackVolume * this.volume;
		
		SoundSystem sndSystem = this.soundManagerReference.getSoundSystem();
		if (sndSystem == null)
		{
			invalidSoundSystem();
			return;
		}
		
		sndSystem.setVolume(this.sourceName, volume);
		
	}
	
	public void stopStreaming(float fadeDuration)
	{
		if (!this.isValid)
			return;
		
		SoundSystem sndSystem = this.soundManagerReference.getSoundSystem();
		if (sndSystem == null)
		{
			invalidSoundSystem();
			return;
		}
		
		if (fadeDuration <= 0f)
		{
			sndSystem.stop(this.sourceName);
		}
		else
		{
			sndSystem.fadeOut(this.sourceName, null, (long) fadeDuration * 1000L);
		}
		
	}
	
	public void pauseStreaming()
	{
		if (!this.isValid)
			return;
		
		SoundSystem sndSystem = this.soundManagerReference.getSoundSystem();
		if (sndSystem == null)
		{
			invalidSoundSystem();
			return;
		}
		
		sndSystem.pause(this.sourceName);
		
		this.isPaused = true;
		
	}
	
	public void unallocate()
	{
		if (!this.isInitialized)
			return;
		
		if (!this.isValid)
			return;
		
		SoundSystem sndSystem = this.soundManagerReference.getSoundSystem();
		if (sndSystem == null)
		{
			invalidSoundSystem();
			return;
		}
		
		sndSystem.stop(this.sourceName);
		sndSystem.removeSource(this.sourceName);
		
	}
	
	private void invalidSoundSystem()
	{
		MAtmosConvLogger.warning("Tried to perform an operation on null SoundSystem");
		Thread.dumpStack();
	}
	
}
