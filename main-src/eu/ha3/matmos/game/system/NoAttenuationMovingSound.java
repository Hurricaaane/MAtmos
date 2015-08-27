package eu.ha3.matmos.game.system;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

/*
--filenotes-placeholder
*/

public class NoAttenuationMovingSound extends MovingSound
{
	protected NoAttenuationMovingSound(ResourceLocation myResource, float volume, float pitch)
	{
		super(myResource);

		this.attenuationType = ISound.AttenuationType.NONE;
		this.repeat = true;
		this.repeatDelay = 0;

		this.volume = volume;
		this.pitch = pitch;
	}
	
	@Override
	public void update()
	{
		Entity e = Minecraft.getMinecraft().thePlayer;
		
		this.xPosF = (float) e.posX;
		this.yPosF = (float) e.posY;
		this.zPosF = (float) e.posZ;
	}

	public void kill()
	{
		this.donePlaying = true;
	}
}
