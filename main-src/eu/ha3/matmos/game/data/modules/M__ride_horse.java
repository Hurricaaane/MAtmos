package eu.ha3.matmos.game.data.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.module.Module;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;

/*
--filenotes-placeholder
*/

public class M__ride_horse extends ModuleProcessor implements Module
{
	public M__ride_horse(Data data)
	{
		super(data, "ride_horse");
	}
	
	@Override
	protected void doProcess()
	{
		Entity xride = Minecraft.getMinecraft().thePlayer.ridingEntity;
		
		if (xride == null || !(xride instanceof EntityHorse))
		{
			int no_null_yet;
			
			return;
		}
		
		EntityHorse ride = (EntityHorse) xride;
		
		setValue("jumping", ride.isHorseJumping());
		setValue("rearing", ride.isRearing());
		setValue("leashed", ride.getLeashed());
		setValue("chested", ride.isChested());
		setValue("tame", ride.isTame());
		
		setValue("type", ride.getHorseType());
		setValue("variant", ride.getHorseVariant());
		
		setValue("name_tag", ride.getCustomNameTag());
		
		setValue("health1k", (int) (ride.getHealth() * 1000));
		setValue("leashed_to_player", ride.getLeashed() && ride.getLeashedToEntity() == ride.riddenByEntity);
		
		if (ride.getLeashed() && ride.getLeashedToEntity() instanceof Entity)
		{
			Entity e = ride.getLeashedToEntity();
			setValue("leash_distance", (int) (e.getDistanceToEntity(ride) * 1000));
		}
		else
		{
			setValue("leash_distance", 0);
		}
		
		// Server only?
		setValue("temper", ride.getTemper());
		setValue("owner_name", ride.getOwnerName());
		setValue("reproduced", ride.getHasReproduced());
		setValue("bred", ride.func_110205_ce());
	}
}