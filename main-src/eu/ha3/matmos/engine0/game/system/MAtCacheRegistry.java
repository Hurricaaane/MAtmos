package eu.ha3.matmos.engine0.game.system;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.Minecraft;
import eu.ha3.matmos.engine0.conv.CacheRegistry;

/* x-placeholder */

public class MAtCacheRegistry implements CacheRegistry
{
	private Set<String> set;
	
	public MAtCacheRegistry()
	{
		this.set = new HashSet<String>();
	}
	
	@Override
	public void clear()
	{
		this.set.clear();
	}
	
	@Override
	public void cacheSound(String path)
	{
		if (this.set.contains(path))
			return;
		
		Minecraft.getMinecraft().sndManager.addSound(path);
		this.set.add(path);
		
		//MAtmosConvLogger.info("Cached sound " + path);
	}
}
