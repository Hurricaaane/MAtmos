package eu.ha3.matmos.game.data.modules;

import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.module.Module;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;
import eu.ha3.matmos.game.system.MAtmosUtility;
import eu.ha3.mc.haddon.PrivateAccessException;
import eu.ha3.mc.haddon.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

/*
--filenotes-placeholder
*/

public class M__ply_general extends ModuleProcessor implements Module
{
    private final Utility util;

    public M__ply_general(Data data, Utility util)
    {
        super(data, "ply_general");
        this.util = util;
    }

    @Override
    protected void doProcess()
    {
        // dag edit EntityClientPlayerMP -> EntityPlayerSP
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

        setValue("in_water", player.isInWater());
        setValue("wet", player.isWet());
        setValue("on_ground", player.onGround);
        setValue("burning", player.isBurning());
        setValue("jumping", player.movementInput.jump);
        try
        {
            setValue("in_web", (Boolean) this.util.getPrivate(player, "isInWeb"));
        }
        catch (PrivateAccessException e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        setValue("on_ladder", player.isOnLadder());

        setValue("blocking", player.isBlocking());
        setValue("sprinting", player.isSprinting());
        setValue("sneaking", player.isSneaking());
        setValue("airborne", player.isAirBorne);
        setValue("using_item", player.isUsingItem());
        setValue("riding", player.isRiding());
        setValue("creative", player.capabilities.isCreativeMode);
        setValue("flying", player.capabilities.isFlying);

        setValue("under_water", MAtmosUtility.isUnderwaterAnyGamemode());
    }
}
