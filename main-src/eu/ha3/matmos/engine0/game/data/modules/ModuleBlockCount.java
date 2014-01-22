package eu.ha3.matmos.engine0.game.data.modules;

import java.util.LinkedHashSet;
import java.util.Set;

import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.engine0.game.data.MAtDataGatherer;
import eu.ha3.matmos.engine0.game.data.abstractions.processor.ProcessorModel;

/*
--filenotes-placeholder
*/

public class ModuleBlockCount extends ProcessorModel implements Module
{
	public static String NAME = "block_curadius8";
	private Set<String> limbo = new LinkedHashSet<String>();
	private Set<String> discovered = new LinkedHashSet<String>();
	
	public ModuleBlockCount(Data data)
	{
		super(data, NAME, NAME + MAtDataGatherer.DELTA_SUFFIX);
		data.getSheet(NAME).setDefaultValue("0");
	}
	
	@Override
	protected void doProcess()
	{
		//Entity e = Minecraft.getMinecraft().thePlayer;
		
		this.discovered.add(null); // add all new blocks
		
		// Reset all missing blocks to zero
		this.limbo.removeAll(this.discovered);
		for (String missing : this.limbo)
		{
			setValue(missing, 0);
		}
		
		Set<String> swap = this.limbo;
		this.limbo = this.discovered;
		this.discovered = swap;
	}
	
	@Override
	public String getModuleName()
	{
		return NAME;
	}
}