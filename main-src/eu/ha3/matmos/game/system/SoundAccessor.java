package eu.ha3.matmos.game.system;

import net.minecraft.client.audio.SoundManager;
import paulscode.sound.SoundSystem;

/*
--filenotes-placeholder
*/

public interface SoundAccessor
{
	public SoundManager getSoundManager();
	
	public SoundSystem getSoundSystem();
}
