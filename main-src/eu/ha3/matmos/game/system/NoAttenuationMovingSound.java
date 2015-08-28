package eu.ha3.matmos.game.system;

import eu.ha3.matmos.engine.core.implem.HelperFadeCalculator;
import eu.ha3.matmos.engine.core.implem.SystemClock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/*
--filenotes-placeholder
*/

public class NoAttenuationMovingSound extends MovingSound implements StreamingSound
{
	private boolean usesPause;
	private final HelperFadeCalculator helper = new HelperFadeCalculator(new SystemClock());
	private float desiredVolume;
	private float desiredPitch;

	protected NoAttenuationMovingSound(ResourceLocation myResource, float volume, float pitch, boolean isLooping, boolean usesPause)
	{
		super(myResource);

		this.attenuationType = ISound.AttenuationType.NONE;
		this.repeat = isLooping;
		this.repeatDelay = 0;

		this.desiredVolume = volume;
		this.desiredPitch = pitch;
		this.volume = volume;
		this.pitch = pitch;

		this.usesPause = usesPause;
	}
	
	public NoAttenuationMovingSound copy()
	{
		return new NoAttenuationMovingSound(this.getSoundLocation(), desiredVolume, desiredPitch, repeat, usesPause);
	}

	@Override
	public void update()
	{
		Entity e = Minecraft.getMinecraft().thePlayer;
		
		this.xPosF = (float) e.posX;
		this.yPosF = (float) e.posY;
		this.zPosF = (float) e.posZ;

		/*this.volume = helper.calculateFadeFactor() * desiredVolume;
		if (volume < 0.01f && usesPause)
		{
			pitch = 0f;
		}
		if (volume > 0.01f && usesPause)
		{
			pitch = desiredPitch;
		}*/
	}

	@Override
	public void play(float fadeIn)
	{
		//this.helper.fadeIn((long) (fadeIn * 1000));
	}

	@Override
	public void stop(float fadeOut)
	{
		//this.helper.fadeOut((long) (fadeOut * 1000));
	}

	@Override
	public void applyVolume(float volumeMod)
	{
		System.out.println("NOT IMPLEMENTED: Apply Volume");
	}

	@Override
	public void dispose()
	{
		this.donePlaying = true;
	}

	@Override
	public void interrupt()
	{
		this.donePlaying = true;
	}
}
