package eu.ha3.matmos.game.tobedeprecated;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;

/*
--filenotes-placeholder
*/

public class _PROCESSOR_MISSING extends ModuleProcessor
{
	public _PROCESSOR_MISSING(Data data, String name)
	{
		super(data, "_PROCESSOR_MISSING");
	}
	
	@Override
	protected void doProcess()
	{
		Minecraft mc = Minecraft.getMinecraft();
		World w = mc.theWorld;
		
		String deprecatedBiomeId = "-1";
		
		//setValueLegacyIntIndexes(26, w.canBlockSeeTheSky(x, y, z) && !(w.getTopSolidOrLiquidBlock(x, z) > y) ? 1 : 0);
		setValueLegacyIntIndexes(59, 0);
		setValueLegacyIntIndexes(5, w.provider.dimensionId);
		setValueLegacyIntIndexes(29, deprecatedBiomeId);
	}
}