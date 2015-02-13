package eu.ha3.matmos.game.data.modules;

import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.module.Module;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;

/*
--filenotes-placeholder
*/

public class M__ply_armor extends ModuleProcessor implements Module
{
	public M__ply_armor(Data data)
	{
		super(data, "ply_armor");
	}
	
	@Override
	protected void doProcess()
	{
        // dag edit EntityClientPlayerMP -> EntityPlayerSP
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		
		for (int i = 0; i < 4; i++)
		{
			ItemStack item = player.inventory.armorInventory[i];
			ItemProcessorHelper.setValue(this, item, Integer.toString(i));
		}
	}
}
