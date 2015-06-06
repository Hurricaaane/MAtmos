package eu.ha3.matmos.engine.core.interfaces;

/*
--filenotes-placeholder
*/

import net.minecraft.client.resources.IResourcePack;

public interface EventInterface
{
	
	public abstract void cacheSounds(IResourcePack resourcePack);
	
	public abstract void playSound(float volMod, float pitchMod);
	
}