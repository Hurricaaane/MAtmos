package eu.ha3.matmos.game.data.abstractions.module;

import eu.ha3.matmos.engine.core.interfaces.Data;

import java.util.Map;
import java.util.TreeMap;

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
public abstract class ModuleProcessor extends ProcessorModel implements EntryBasedModule
{
	public static final String DELTA_SUFFIX = "_delta";
	
	private final String name;
	
	private Map<String, EI> eis = new TreeMap<String, EI>();
	
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
	
	@Override
	public Map<String, EI> getModuleEntries()
	{
		return this.eis;
	}
	
	/**
	 * Internal method to create an EI.
	 * 
	 * @param name
	 * @param desc
	 */
	protected void EI(String name, String desc)
	{
		this.eis.put(name, new EI(name, desc));
	}
	
	/**
	 * Internal method to register an EI.
	 * 
	 * @param ei
	 */
	protected void EI(EI ei)
	{
		this.eis.put(ei.getName(), ei);
	}
}
