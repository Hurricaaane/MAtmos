package eu.ha3.matmos.game.data.modules;

import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.module.Module;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;

/*
--filenotes-placeholder
*/

public class S__ply_leash extends ModuleProcessor implements Module
{
	public S__ply_leash(Data data)
	{
		super(data, "ply_leash");
	}
	
	@Override
	protected void doProcess()
	{
		EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
		World w = Minecraft.getMinecraft().theWorld;
		
		final int distance = 20;
		int count = 0;
		
		@SuppressWarnings("unchecked")
		List<EntityLiving> var6 =
			w.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getBoundingBox(
				player.posX - distance, player.posY - distance, player.posZ - distance, player.posX + distance,
				player.posY + distance, player.posZ + distance));
		
		if (var6 != null)
		{
			Iterator<EntityLiving> var7 = var6.iterator();
			
			while (var7.hasNext())
			{
				EntityLiving var8 = var7.next();
				
				if (var8.getLeashed() && var8.getLeashedToEntity() == player)
				{
					count = count + 1;
				}
			}
		}
		
		setValue("total", count);
	}
}
