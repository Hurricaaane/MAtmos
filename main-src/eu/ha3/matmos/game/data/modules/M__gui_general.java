package eu.ha3.matmos.game.data.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.module.Module;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;

/*
--filenotes-placeholder
*/

public class M__gui_general extends ModuleProcessor implements Module
{
	public M__gui_general(Data data)
	{
		super(data, "gui_general");
	}
	
	@Override
	protected void doProcess()
	{
		GuiScreen gui = Minecraft.getMinecraft().currentScreen;
		
		setValue("open", gui != null);
		setValue("instanceof_container", gui instanceof GuiContainer);
	}
}
