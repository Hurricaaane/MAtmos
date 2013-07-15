package net.minecraft.src;

import java.util.HashSet;
import java.util.Set;

/*
            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
                    Version 2, December 2004 

 Copyright (C) 2004 Sam Hocevar <sam@hocevar.net> 

 Everyone is permitted to copy and distribute verbatim or modified 
 copies of this license document, and changing it is allowed as long 
 as the name is changed. 

            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE 
   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION 

  0. You just DO WHAT THE FUCK YOU WANT TO. 
*/

public class MAtCacheRegistry
{
	private Set<String> set;
	
	public MAtCacheRegistry()
	{
		this.set = new HashSet<String>();
	}
	
	public void clear()
	{
		this.set.clear();
	}
	
	public void cacheSound(String path)
	{
		if (this.set.contains(path))
			return;
		
		Minecraft.getMinecraft().sndManager.addSound(path);
		this.set.add(path);
		
		//MAtmosConvLogger.info("Cached sound " + path);
	}
}
