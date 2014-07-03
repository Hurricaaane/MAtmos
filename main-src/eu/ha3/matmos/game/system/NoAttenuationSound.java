package eu.ha3.matmos.game.system;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

/*
--filenotes-placeholder
*/

public class NoAttenuationSound extends PositionedSoundRecord
{
	public NoAttenuationSound(ResourceLocation r, float a, float b, float c, float d, float e)
	{
		super(r, a, b, c, d, e);
		this.attenuationType = ISound.AttenuationType.NONE;
	}
}
