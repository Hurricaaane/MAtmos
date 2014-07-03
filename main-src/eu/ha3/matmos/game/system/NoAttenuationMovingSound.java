package eu.ha3.matmos.game.system;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import eu.ha3.matmos.engine.core.implem.HelperFadeCalculator;

/*
--filenotes-placeholder
*/

public class NoAttenuationMovingSound extends MovingSound
{
	private HelperFadeCalculator fade;
	
	private boolean isLooping;
	private boolean usesPause;
	
	protected NoAttenuationMovingSound(
		ResourceLocation p_i45104_1_, HelperFadeCalculator fade, boolean isLooping, boolean usesPause)
	{
		super(p_i45104_1_);
		this.attenuationType = ISound.AttenuationType.NONE;
		this.repeat = true; // TODO: What does this do?
		
		this.isLooping = isLooping;
		this.usesPause = usesPause;
	}
	
	@Override
	public void update()
	{
		Entity e = Minecraft.getMinecraft().thePlayer;
		
		this.xPosF = (float) e.posX;
		this.yPosF = (float) e.posY;
		this.zPosF = (float) e.posZ;
		
		float volumeFactor = this.fade.calculateFadeFactor();
		float pitchFactor = 1f;
		if (volumeFactor < 0.01f)
		{
			if (this.usesPause)
			{
				// Set pitch to zero to vitrually pause it
				volumeFactor = 0f;
				pitchFactor = 0f;
			}
			else
			{
				// Kill
				this.donePlaying = true;
			}
		}
	}
}
