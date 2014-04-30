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
		this.field_147666_i = ISound.AttenuationType.NONE;
		this.field_147659_g = true; // TODO: What does this do?
		
		this.isLooping = isLooping;
		this.usesPause = usesPause;
	}
	
	@Override
	public void update()
	{
		Entity e = Minecraft.getMinecraft().thePlayer;
		
		this.field_147660_d = (float) e.posX;
		this.field_147661_e = (float) e.posY;
		this.field_147658_f = (float) e.posZ;
		
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
				this.field_147668_j = true;
			}
		}
	}
}
