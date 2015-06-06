package eu.ha3.matmos.game.data.modules;

import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.module.Module;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;

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
			//setValue("jumping", false);
			setValue("rearing", false);
			setValue("saddled", false);
			setValue("leashed", false);
			setValue("chested", false);
			setValue("tame", false);
			setValue("type", 0);
			setValue("variant", 0);
			setValue("name_tag", "");
			setValue("health1k", 0);
			setValue("leashed_to_player", false);
			setValue("ridden_by_owner", false);
			setValue("leashed_to_owner", false);
			setValue("leash_distance", 0);
			setValue("temper", 0);
			setValue("owner_name", "");
			setValue("reproduced", false);
			setValue("bred", false);
			
			return;
		}
		
		EntityHorse ride = (EntityHorse) xride;
		
		//setValue("jumping", ride.isHorseJumping()); // not functionnal
		setValue("rearing", ride.isRearing());
		setValue("saddled", ride.isHorseSaddled());
		setValue("leashed", ride.getLeashed());
		setValue("chested", ride.isChested());
		setValue("tame", ride.isTame());
		
		setValue("type", ride.getHorseType());
		setValue("variant", ride.getHorseVariant());
		
		setValue("name_tag", ride.getCustomNameTag());
		
		setValue("health1k", (int) (ride.getHealth() * 1000));
		setValue("leashed_to_player", ride.getLeashed() && ride.getLeashedToEntity() instanceof EntityPlayer);
        // dag edit func_152119_ch() -> getOwnerId()
		setValue(
			"ridden_by_owner",
			ride.riddenByEntity instanceof EntityPlayer
				&& !ride.getOwnerId().equals("")
				&& ride.getOwnerId().equals(((EntityPlayer) ride.riddenByEntity).getGameProfile().getId().toString()));
        // dag edit func_152119_ch() -> getOwnerId()
		setValue(
			"leashed_to_owner",
			ride.getLeashedToEntity() instanceof EntityPlayer
				&& !ride.getOwnerId().equals("")
				&& ride.getOwnerId().equals(((EntityPlayer) ride.getLeashedToEntity()).getGameProfile().getId().toString()));
		
		if (ride.getLeashed() && ride.getLeashedToEntity() != null)
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
        // dag edit func_152119_ch() -> getOwnerId()
		setValue("owner_uuid", ride.getOwnerId());
		setValue("reproduced", ride.getHasReproduced());
        // dag edit func_110205_ce() -> isBreeding() [this is probably wrong, not sure what 'bred' represents]
        setValue("bred", ride.isBreeding());
	}
}