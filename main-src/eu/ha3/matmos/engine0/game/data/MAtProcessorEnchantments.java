package eu.ha3.matmos.engine0.game.data;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import eu.ha3.matmos.engine0.core.implem.SelfGeneratingData;
import eu.ha3.matmos.engine0.game.system.MAtMod;

/* x-placeholder */

public abstract class MAtProcessorEnchantments extends MAtProcessorModel
{
	public MAtProcessorEnchantments(MAtMod modIn, SelfGeneratingData dataIn, String normalNameIn, String deltaNameIn)
	{
		super(modIn, dataIn, normalNameIn, deltaNameIn);
	}
	
	@Override
	protected void doProcess()
	{
		Set<String> required = getRequired();
		
		// Required to handle disappearing enchantments and unequipped armor
		for (String i : required)
		{
			setValue(i, 0);
		}
		
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		ItemStack item = getItem(player);
		
		if (item != null && item.getEnchantmentTagList() != null && item.getEnchantmentTagList().tagCount() > 0)
		{
			int total = item.getEnchantmentTagList().tagCount();
			
			NBTTagList enchantments = item.getEnchantmentTagList();
			for (int i = 0; i < total; i++)
			{
				// tagAt
				//int id = ((NBTTagCompound) enchantments.func_150305_b(i)).getShort("id");
				int id = enchantments.func_150305_b(i).getShort("id");
				
				if (required.contains(id))
				{
					//short lvl = ((NBTTagCompound) enchantments.func_150305_b(i)).getShort("lvl");
					short lvl = enchantments.func_150305_b(i).getShort("lvl");
					setValueLegacyIntIndexes(id, lvl);
				}
			}
		}
	}
	
	protected abstract ItemStack getItem(EntityPlayer player);
	
}
