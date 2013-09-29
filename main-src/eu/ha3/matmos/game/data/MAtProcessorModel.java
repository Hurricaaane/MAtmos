package eu.ha3.matmos.game.data;

import eu.ha3.matmos.conv.ProcessorModel;
import eu.ha3.matmos.engine.implem.IntegerData;
import eu.ha3.matmos.game.system.MAtMod;

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

public abstract class MAtProcessorModel extends ProcessorModel
{
	private MAtMod mod;
	
	public MAtProcessorModel(MAtMod modIn, IntegerData dataIn, String normalNameIn, String deltaNameIn)
	{
		super(dataIn, normalNameIn, deltaNameIn);
		this.mod = modIn;
		
	}
	
	public MAtMod mod()
	{
		return this.mod;
	}
}
