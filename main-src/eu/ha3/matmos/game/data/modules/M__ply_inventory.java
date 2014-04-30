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

public class M__ply_inventory extends ModuleProcessor implements Module
{
	public M__ply_inventory(Data data)
	{
		super(data, "ply_inventory");
	}
	
	@Override
	protected void doProcess()
	{
		EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
		
		setValue("held_slot", player.inventory.currentItem);
		if (player.inventory.getCurrentItem() != null)
		{
			ItemStack item = player.inventory.getCurrentItem();
			setValue("current_item", MAtmosUtility.nameOf(player.inventory.getCurrentItem()));
			setValue("current_damage", player.inventory.getCurrentItem().getItemDamage());
			setValue("current_name_display", item.getDisplayName());
		}
		else
		{
			setValue("current_item", MODULE_CONSTANTS.NO_ITEM);
			setValue("current_damage", 0);
			setValue("current_name_display", "");
		}
	}
}
