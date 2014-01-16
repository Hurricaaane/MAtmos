package eu.ha3.matmos.engine0.game.data.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import eu.ha3.matmos.engine0.conv.ProcessorModel;
import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.engine0.game.data.MAtDataGatherer;
import eu.ha3.matmos.v170helper.Version170Helper;

/*
--filenotes-placeholder
*/

public class ModulePlayerHotbarItems extends ProcessorModel implements Module
{
	public static String NAME = "player_position";
	
	public ModulePlayerHotbarItems(Data data)
	{
		super(data, NAME, NAME + MAtDataGatherer.DELTA_SUFFIX);
	}
	
	@Override
	protected void doProcess()
	{
		EntityClientPlayerMP e = Minecraft.getMinecraft().thePlayer;
		boolean hasCurrentItem = e.inventory.getCurrentItem() != null;
		
		setValue("current_name", hasCurrentItem
			? Version170Helper.nameOf(e.inventory.getCurrentItem().getItem()) : MAtDataGatherer.NULL);
		setValue("current_meta", hasCurrentItem
			? Integer.toString(e.inventory.getCurrentItem().getItemDamage()) : MAtDataGatherer.NULL);
		setValue("current_slotnumber", e.inventory.currentItem);
	}
	
	@Override
	public String getModuleName()
	{
		return NAME;
	}
}
