package eu.ha3.matmos.game.data.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.item.ItemStack;
import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.MODULE_CONSTANTS;
import eu.ha3.matmos.game.data.abstractions.module.Module;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;
import eu.ha3.matmos.game.system.MAtmosUtility;

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
		EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
		
		for (int i = 0; i < 4; i++)
		{
			ItemStack item = player.inventory.armorInventory[i];
			if (item != null)
			{
				setValue(i + "_item", MAtmosUtility.nameOf(item));
				setValue(i + "_damage", item.getItemDamage());
				setValue(i + "_name_display", item.getDisplayName());
			}
			else
			{
				setValue(i + "_item", MODULE_CONSTANTS.NO_ITEM);
				setValue(i + "_damage", 0);
				setValue(i + "_name_display", "");
			}
		}
	}
}
