package eu.ha3.matmos.game.data.modules;

import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.module.Module;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.*;

/*
--filenotes-placeholder
*/

public class M__gui_general extends ModuleProcessor implements Module
{
	public M__gui_general(Data data)
	{
		super(data, "gui_general");
	}
	
	@Override
	protected void doProcess()
	{
		GuiScreen gui = Minecraft.getMinecraft().currentScreen;
		
		setValue("open", gui != null);
		
		setValue("is_commandblock", gui instanceof GuiCommandBlock);
		
		setValue("is_container", gui instanceof GuiContainer);
		
		setValue("is_inventory", gui instanceof GuiInventory);
		setValue("is_creative", gui instanceof GuiContainerCreative);
		
		setValue("is_beacon", gui instanceof GuiBeacon);
		setValue("is_brewing", gui instanceof GuiBrewingStand);
		setValue("is_chest", gui instanceof GuiChest);
		setValue("is_crafting", gui instanceof GuiCrafting);
		setValue("is_dispenser", gui instanceof GuiDispenser);
		setValue("is_enchantment", gui instanceof GuiEnchantment);
		setValue("is_furnace", gui instanceof GuiFurnace);
		setValue("is_hopper", gui instanceof GuiHopper);
		setValue("is_npc_trade", gui instanceof GuiMerchant);
		setValue("is_anvil", gui instanceof GuiRepair);
		setValue("is_horse", gui instanceof GuiScreenHorseInventory);
	}
}
