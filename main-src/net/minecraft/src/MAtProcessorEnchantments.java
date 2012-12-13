package net.minecraft.src;

import eu.ha3.matmos.engine.Data;

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

public abstract class MAtProcessorEnchantments extends MAtProcessorModel
{
	MAtProcessorEnchantments(MAtMod modIn, Data dataIn, String normalNameIn, String deltaNameIn)
	{
		super(modIn, dataIn, normalNameIn, deltaNameIn);
	}
	
	@Override
	void doProcess()
	{
		// Sets everything to 0 if no such armor
		for (int i = 0; i < 64; i++)
		{
			setValue(i, 0);
		}
		
		EntityPlayer player = mod().manager().getMinecraft().thePlayer;
		ItemStack item = getItem(player);
		
		if (item != null && item.getEnchantmentTagList() != null && item.getEnchantmentTagList().tagCount() > 0)
		{
			int total = item.getEnchantmentTagList().tagCount();
			
			NBTTagList enchantments = item.getEnchantmentTagList();
			for (int i = 0; i < total; i++)
			{
				short id = ((NBTTagCompound) enchantments.tagAt(i)).getShort("id");
				short lvl = ((NBTTagCompound) enchantments.tagAt(i)).getShort("lvl");
				
				if (id < 64 && i >= 0)
				{
					setValue(id, lvl);
				}
			}
		}
	}
	
	protected abstract ItemStack getItem(EntityPlayer player);
	
}
