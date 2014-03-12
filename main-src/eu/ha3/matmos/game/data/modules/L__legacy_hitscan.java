package eu.ha3.matmos.game.data.modules;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.MODULE_CONSTANTS;
import eu.ha3.matmos.game.data.abstractions.module.Module;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;
import eu.ha3.matmos.game.system.MAtmosUtility;

/*
--filenotes-placeholder
*/

public class L__legacy_hitscan extends ModuleProcessor implements Module
{
	private final Map<MovingObjectType, String> equiv = new HashMap<MovingObjectPosition.MovingObjectType, String>();
	
	public L__legacy_hitscan(Data data)
	{
		super(data, "legacy_hitscan");
		
		// The ordinal values was different back then, "0" was the block.
		this.equiv.put(MovingObjectType.MISS, "-1");
		this.equiv.put(MovingObjectType.ENTITY, "1");
		this.equiv.put(MovingObjectType.BLOCK, "0");
	}
	
	@Override
	protected void doProcess()
	{
		Minecraft mc = Minecraft.getMinecraft();
		
		if (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit == null)
		{
			setValue("mouse_over_something", false);
			setValue("mouse_over_what_remapped", -1);
			setValue("block_as_number", MODULE_CONSTANTS.LEGACY_NO_BLOCK_IN_THIS_CONTEXT);
			setValue("meta_as_number", MODULE_CONSTANTS.LEGACY_NO_BLOCK_IN_THIS_CONTEXT);
			
			return;
		}
		
		setValue("mouse_over_something", mc.objectMouseOver.typeOfHit != MovingObjectType.MISS);
		setValue("mouse_over_what_remapped", this.equiv.get(mc.objectMouseOver.typeOfHit));
		setValue(
			"block_as_number",
			mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK
				? MAtmosUtility.legacyOf(MAtmosUtility.getBlockAt(
					mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ))
				: MODULE_CONSTANTS.LEGACY_NO_BLOCK_IN_THIS_CONTEXT);
		setValue(
			"meta_as_number",
			mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK ? MAtmosUtility.getMetaAt(
				mc.objectMouseOver.blockX, mc.objectMouseOver.blockY, mc.objectMouseOver.blockZ,
				MODULE_CONSTANTS.LEGACY_NO_BLOCK_OUT_OF_BOUNDS) : MODULE_CONSTANTS.LEGACY_NO_BLOCK_IN_THIS_CONTEXT);
	}
}