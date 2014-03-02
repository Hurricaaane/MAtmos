package eu.ha3.matmos.game.data.modules;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.module.AbstractEnchantmentModule;

/*
--filenotes-placeholder
*/

public class S__ench_current extends AbstractEnchantmentModule
{
	public S__ench_current(Data data)
	{
		super(data, "ench_current");
	}
	
	@Override
	protected ItemStack getItem(EntityPlayer player)
	{
		return player.getCurrentEquippedItem();
	}
}