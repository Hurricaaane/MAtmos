package eu.ha3.matmos.game.system;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound.AttenuationType;
import net.minecraft.util.ResourceLocation;
import paulscode.sound.SoundSystem;

/*
--filenotes-placeholder
*/

public class StreamingSoundUsingAccessor implements StreamingSound
{
	private static String prefix = "SSUA_";
	private static int token = 0;
	
	private final SoundAccessor accessor;
	private final String path;
	private final float volume;
	private final float pitch;
	private final boolean isLooping;
	private final boolean usesPause;
	
	private float volumeModulator = 1f;
	
	private boolean isSetup;
	private boolean isInterrupted;
	private boolean isPlaying;
	
	private String identifier;
	private String filename;
	
	public StreamingSoundUsingAccessor(
		SoundAccessor accessor, String path, float baseVolume, float pitch, boolean isLooping, boolean usesPause)
	{
		this.accessor = accessor;
		this.path = path;
		this.volume = baseVolume;
		this.pitch = pitch;
		this.isLooping = isLooping;
		this.usesPause = usesPause;
	}
	
	private void setup()
	{
		int token = StreamingSoundUsingAccessor.token++;
		
		this.identifier = prefix + token;
		this.filename = this.path; // XXX 2014-01-10 what is filename?!
		SoundSystem sys = this.accessor.getSoundSystem();
		
		sys.newStreamingSource(
			false, this.path, generateResourceLocation(this.path), this.identifier, this.isLooping, 0f, 0f, 0f,
			AttenuationType.NONE.func_148586_a(), 0f);
		
		sys.setVolume(this.identifier, this.volume * this.volumeModulator);
		sys.setPitch(this.identifier, this.pitch);
		sys.setLooping(this.identifier, this.isLooping);
	}
	
	private URL generateResourceLocation(String path)
	{
		URL url = null;
		final ResourceLocation loc = new ResourceLocation("assets/sounds/" + path);
		
		String opth =
			String.format("%s:%s:%s", new Object[] { "mcsounddomain", loc.getResourceDomain(), loc.getResourcePath() });
		
		URLStreamHandler urlStream = new URLStreamHandler() {
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
						return Minecraft.getMinecraft().getResourceManager().getResource(loc).getInputStream();
					}
				};
			}
		};
		
		try
		{
			url = new URL((URL) null, opth, urlStream);
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		return url;
	}
	
	@Override
	public void play(float fadeIn)
	{
		if (this.isInterrupted)
			return;
		
		if (!this.isSetup)
		{
			setup();
		}
		
		if (this.isPlaying)
			return;
		
		this.accessor.getSoundSystem().play(this.identifier);
		if (this.usesPause)
		{
			// Do nothing, play is enough
		}
		else if (fadeIn >= 0f)
		{
			this.accessor.getSoundSystem().fadeOutIn(this.identifier, this.filename, 1L, (long) (fadeIn * 1000f));
		}
		else
		{
			this.accessor.getSoundSystem().fadeOutIn(this.identifier, this.filename, 1, 1);
		}
		this.isPlaying = true;
	}
	
	@Override
	public void stop(float fadeOut)
	{
		if (this.isInterrupted)
			return;
		
		if (!this.isSetup)
			return;
		
		if (!this.isPlaying)
			return;
		
		if (this.usesPause)
		{
			this.accessor.getSoundSystem().pause(this.identifier);
		}
		else if (fadeOut >= 0f)
		{
			this.accessor.getSoundSystem().fadeOut(this.identifier, this.filename, (long) (fadeOut * 1000f));
		}
		else
		{
			this.accessor.getSoundSystem().stop(this.identifier);
		}
		this.isPlaying = false;
	}
	
	@Override
	public void dispose()
	{
		if (this.isInterrupted)
			return;
		
		if (!this.isSetup)
			return;
		
		stop(0f);
		this.accessor.getSoundSystem().removeSource(this.identifier);
		interrupt();
	}
	
	@Override
	public void interrupt()
	{
		this.isInterrupted = true;
	}
	
	@Override
	public void applyVolume(float volumeMod)
	{
		this.volumeModulator = volumeMod;
		if (this.isSetup)
		{
			this.accessor.getSoundSystem().setVolume(this.identifier, this.volume * this.volumeModulator);
		}
	}
}
