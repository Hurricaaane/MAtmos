package eu.ha3.matmos.engine0.game.data.modules;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.engine0.game.data.abstractions.module.Module;
import eu.ha3.matmos.engine0.game.data.abstractions.module.ModuleProcessor;

/*
--filenotes-placeholder
*/

public class S__ply_hitscan extends ModuleProcessor implements Module
{
	private final Map<MovingObjectType, String> equiv = new HashMap<MovingObjectPosition.MovingObjectType, String>();
	
	public S__ply_hitscan(Data data)
	{
		super(data, "ply_hitscan");
		this.equiv.put(MovingObjectType.MISS, "");
		this.equiv.put(MovingObjectType.ENTITY, "entity");
		this.equiv.put(MovingObjectType.BLOCK, "block");
	}
	
	@Override
	protected void doProcess()
	{
		Minecraft mc = Minecraft.getMinecraft();
		setValue("mouse_over_something", mc.objectMouseOver != null);
		setValue("mouse_over_what_remapped", this.equiv.get(mc.objectMouseOver.typeOfHit));
	}
}