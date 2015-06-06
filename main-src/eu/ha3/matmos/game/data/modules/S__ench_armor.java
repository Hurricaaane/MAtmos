package eu.ha3.matmos.game.data.modules;

import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.module.AbstractEnchantmentModule;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/*
--filenotes-placeholder
*/

public class S__ench_armor extends AbstractEnchantmentModule
{
	private final int zeronth;
	
	public S__ench_armor(Data data, int indexZeroOrdinal)
	{
		super(data, "ench_armor" + (indexZeroOrdinal + 1));
		this.zeronth = indexZeroOrdinal;
	}
	
	@Override
	protected ItemStack getItem(EntityPlayer player)
	{
		return player.inventory.armorInventory[this.zeronth];
	}
}