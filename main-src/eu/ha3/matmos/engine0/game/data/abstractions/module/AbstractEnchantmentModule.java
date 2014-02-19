package eu.ha3.matmos.engine0.game.data.abstractions.module;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import eu.ha3.matmos.engine0.core.interfaces.Data;

/* x-placeholder */

/**
 * An abstract module that extracts all enchantments associated to an item
 * defined by the implementing class.
 * 
 * @author Hurry
 * 
 */
public abstract class AbstractEnchantmentModule extends ModuleProcessor implements Module
{
	public AbstractEnchantmentModule(Data dataIn, String name)
	{
		super(dataIn, name);
	}
	
	@Override
	protected void doProcess()
	{
		// Required to handle disappearing enchantments and unequipped armor
		for (String entry : this.sheet.keySet())
		{
			setValue(entry, "0");
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
				
				//short lvl = ((NBTTagCompound) enchantments.func_150305_b(i)).getShort("lvl");
				short lvl = enchantments.func_150305_b(i).getShort("lvl");
				setValue("id_" + id, Short.toString(lvl));
			}
		}
	}
	
	protected abstract ItemStack getItem(EntityPlayer player);
	
}
