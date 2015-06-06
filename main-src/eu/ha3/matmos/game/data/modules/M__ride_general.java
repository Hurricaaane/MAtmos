package eu.ha3.matmos.game.data.modules;

import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.MODULE_CONSTANTS;
import eu.ha3.matmos.game.data.abstractions.module.Module;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;

/*
--filenotes-placeholder
*/

public class M__ride_general extends ModuleProcessor implements Module
{
	public M__ride_general(Data data)
	{
		super(data, "ride_general");
	}
	
	@Override
	protected void doProcess()
	{
		Entity ride = Minecraft.getMinecraft().thePlayer.ridingEntity;
		
		// http://stackoverflow.com/questions/2950319/is-null-check-needed-before-calling-instanceof
		
		// Only do null safe operations here
		
		setValue("minecart", ride instanceof EntityMinecart);
		setValue("boat", ride instanceof EntityBoat);
		setValue("pig", ride instanceof EntityPig);
		setValue("horse", ride instanceof EntityHorse);
		setValue("player", ride instanceof EntityPlayer);
		
		if (ride == null)
		{
			setValue("burning", false);
			setValue("entity_id", MODULE_CONSTANTS.NO_ENTITY);
			return;
		}
		
		// All null unsafe operations go here
		setValue("burning", ride.isBurning());
		setValue("entity_id", EntityList.getEntityID(ride));
	}
}