package eu.ha3.matmos.game.data.abstractions.module;

import eu.ha3.matmos.engine.core.interfaces.Data;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;

import java.util.LinkedHashSet;
import java.util.Set;

/* x-placeholder */

/**
 * An abstract module that extracts all enchantments associated to an item
 * defined by the implementing class.
 * 
 * @author Hurry
 */
public abstract class AbstractEnchantmentModule extends ModuleProcessor implements RegistryBasedModule
{
	private Set<String> oldThings = new LinkedHashSet<String>();
	
	public AbstractEnchantmentModule(Data dataIn, String name)
	{
		super(dataIn, name);
		dataIn.getSheet(name).setDefaultValue("0");
		dataIn.getSheet(name + ModuleProcessor.DELTA_SUFFIX).setDefaultValue("0");
	}
	
	@Override
	public String getRegistryName()
	{
		return "enchantment";
	}
	
	@Override
	protected void doProcess()
	{
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		ItemStack item = getItem(player);
		
		for (String i : this.oldThings)
		{
			setValue(i, 0);
		}
		this.oldThings.clear();
		
		if (item != null && item.getEnchantmentTagList() != null && item.getEnchantmentTagList().tagCount() > 0)
		{
			int total = item.getEnchantmentTagList().tagCount();
			
			NBTTagList enchantments = item.getEnchantmentTagList();
			for (int i = 0; i < total; i++)
			{
				int id = enchantments.getCompoundTagAt(i).getShort("id");
				
				short lvl = enchantments.getCompoundTagAt(i).getShort("lvl");
				setValue(Integer.toString(id), Short.toString(lvl));
				this.oldThings.add(Integer.toString(id));
			}
		}
	}
	
	protected abstract ItemStack getItem(EntityPlayer player);
	
}
