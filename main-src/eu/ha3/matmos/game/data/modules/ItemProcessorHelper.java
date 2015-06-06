package eu.ha3.matmos.game.data.modules;

import eu.ha3.matmos.game.data.MODULE_CONSTANTS;
import eu.ha3.matmos.game.data.abstractions.module.ProcessorModel;
import eu.ha3.matmos.game.system.MAtmosUtility;
import net.minecraft.item.ItemStack;

/*
--filenotes-placeholder
*/

public class ItemProcessorHelper
{
	public static void setValue(ProcessorModel model, ItemStack item, String prefix)
	{
		if (item == null)
		{
			model.setValue(prefix + "_item", MODULE_CONSTANTS.NO_ITEM);
			model.setValue(prefix + "_damage", MODULE_CONSTANTS.NO_META);
			model.setValue(prefix + "_name_display", MODULE_CONSTANTS.NO_NAME);
			model.setValue(prefix + "_powermeta", MODULE_CONSTANTS.NO_POWERMETA);
		}
		else
		{
			model.setValue(prefix + "_item", MAtmosUtility.nameOf(item));
			model.setValue(prefix + "_damage", item.getMetadata());
			model.setValue(prefix + "_name_display", item.getDisplayName());
			model.setValue(prefix + "_powermeta", MAtmosUtility.asPowerMeta(item));
		}
	}
}
