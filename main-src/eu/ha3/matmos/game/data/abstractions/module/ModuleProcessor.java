package eu.ha3.matmos.game.data.abstractions.module;

import eu.ha3.matmos.engine0.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.processor.ProcessorModel;

/*
--filenotes-placeholder
*/

/**
 * A convenient class for modules that use the processor model. When calling
 * process(), doProcess() is called first, then the virtual sheets are applied
 * at the end.
 * 
 * @author Hurry
 */
public abstract class ModuleProcessor extends ProcessorModel implements Module
{
	public static final String DELTA_SUFFIX = "_delta";
	
	private final String name;
	
	public ModuleProcessor(Data data, String name)
	{
		this(data, name, false);
	}
	
	public ModuleProcessor(Data data, String name, boolean doNotUseDelta)
	{
		super(data, name, doNotUseDelta ? null : name + DELTA_SUFFIX);
		this.name = name;
	}
	
	@Override
	public final String getModuleName()
	{
		return this.name;
	}
}
