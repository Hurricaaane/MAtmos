package net.minecraft.src;

import java.util.HashMap;
import java.util.Map;

import WeatherPony.Seasons.api.Season;
import eu.ha3.matmos.engine.implem.IntegerData;

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

public class MAtProcessorSeasonsModAPI extends MAtProcessorModel
{
	private Map<Season, Integer> seasonsHash;
	
	public MAtProcessorSeasonsModAPI(MAtMod modIn, IntegerData dataIn, String normalNameIn, String deltaNameIn)
	{
		super(modIn, dataIn, normalNameIn, deltaNameIn);
		
		this.seasonsHash = new HashMap<Season, Integer>();
		this.seasonsHash.put(Season.NotInstalled, -1);
		this.seasonsHash.put(Season.NoSeason, 0);
		this.seasonsHash.put(Season.Spring, 1);
		this.seasonsHash.put(Season.Summer, 2);
		this.seasonsHash.put(Season.Autumn, 3);
		this.seasonsHash.put(Season.Winter, 4);
	}
	
	@Override
	protected void doProcess()
	{
		Minecraft mc = mod().manager().getMinecraft();
		EntityPlayerSP player = mc.thePlayer;
		int x = (int) Math.floor(player.posX);
		int y = (int) Math.floor(player.posY);
		int z = (int) Math.floor(player.posZ);
		
		if (y < 0)
		{
			y = 0;
		}
		else if (y > 255)
		{
			y = 255;
		}
		
		Integer ic = this.seasonsHash.get(Season.getSeason_Client(x, y, z));
		if (ic == null)
		{
			ic = -2;
		}
		
		setValue(0, 1);
		setValue(1, ic);
	}
}
