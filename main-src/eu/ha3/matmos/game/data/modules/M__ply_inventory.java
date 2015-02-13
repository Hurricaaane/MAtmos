package eu.ha3.matmos.game.data.modules;

import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.module.Module;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

/*
--filenotes-placeholder
*/

public class M__ply_inventory extends ModuleProcessor implements Module
{
    public M__ply_inventory(Data data)
    {
        super(data, "ply_inventory");
    }

    @Override
    protected void doProcess()
    {
        // dag edit EntityClientPlayerMP -> EntityPlayerSP
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

        setValue("held_slot", player.inventory.currentItem);
        ItemProcessorHelper.setValue(this, player.inventory.getCurrentItem(), "current");
        ItemProcessorHelper.setValue(this, player.inventory.getItemStack(), "item_in_cursor");
    }
}
